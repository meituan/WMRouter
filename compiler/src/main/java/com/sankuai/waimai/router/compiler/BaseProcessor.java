package com.sankuai.waimai.router.compiler;

import static com.google.common.base.Charsets.UTF_8;

import com.sankuai.waimai.router.interfaces.Const;
import com.sankuai.waimai.router.service.ServiceImpl;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * Created by jzj on 2018/3/23.
 */

public abstract class BaseProcessor extends AbstractProcessor {

    protected Filer filer;
    protected Types types;
    protected Elements elements;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        types = processingEnvironment.getTypeUtils();
        elements = processingEnvironment.getElementUtils();
    }

    public TypeMirror typeMirror(String className) {
        return elements.getTypeElement(className).asType();
    }

    public boolean isSubType(TypeMirror type, String className) {
        return type != null && types.isSubtype(type, typeMirror(className));
    }

    public boolean isSubType(Element element, String className) {
        return element != null && isSubType(element.asType(), className);
    }

    public boolean isSubType(Element element, TypeMirror typeMirror) {
        return element != null && types.isSubtype(element.asType(), typeMirror);
    }

    /**
     * 非抽象类
     */
    public boolean isConcreteType(Element element) {
        return element instanceof TypeElement && !element.getModifiers().contains(
                Modifier.ABSTRACT);
    }

    /**
     * 非抽象子类
     */
    public boolean isConcreteSubType(Element element, String className) {
        return isConcreteType(element) && isSubType(element, className);
    }

    /**
     * 非抽象子类
     */
    public boolean isConcreteSubType(Element element, TypeMirror typeMirror) {
        return isConcreteType(element) && isSubType(element, typeMirror);
    }

    public boolean isType(TypeMirror interfaces, String name) {
        return interfaces != null && interfaces.toString().equals(name);
    }

    public boolean isActivity(Element element) {
        return isConcreteSubType(element, Const.ACTIVITY_CLASS);
    }

    public boolean isHandler(Element element) {
        return isConcreteSubType(element, Const.URI_HANDLER_CLASS);
    }

    public boolean isInterceptor(Element element) {
        return isConcreteSubType(element, Const.URI_INTERCEPTOR_CLASS);
    }

    public static String getClassName(TypeMirror typeMirror) {
        return typeMirror == null ? "" : typeMirror.toString();
    }

    public void writeHandlerInitClass(CodeBlock code, String hash,
            String genClassName, String handlerClassName, String interfaceName) {
        try {
            genClassName += Const.SPLITTER + hash;
            MethodSpec methodSpec = MethodSpec.methodBuilder(Const.INIT_METHOD)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.VOID)
                    .addParameter(TypeName.get(typeMirror(handlerClassName)), "handler")
                    .addCode(code)
                    .build();
            TypeSpec typeSpec = TypeSpec.classBuilder(genClassName)
                    .addSuperinterface(TypeName.get(typeMirror(interfaceName)))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(methodSpec)
                    .build();
            JavaFile.builder(Const.GEN_PKG, typeSpec)
                    .build()
                    .writeTo(filer);
            String fullImplName = Const.GEN_PKG + Const.DOT + genClassName;
            String config = new ServiceImpl(null, fullImplName, false).toConfig();
            writeInterfaceServiceFile(interfaceName, Collections.singletonList(config));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CodeBlock buildHandler(boolean isActivity, Symbol.ClassSymbol cls) {
        CodeBlock.Builder b = CodeBlock.builder();
        if (isActivity) {
            b.add("$S", cls.className());
        } else {
            b.add("new $T()", cls);
        }
        return b.build();
    }

    public CodeBlock buildInterceptors(List<? extends TypeMirror> interceptors) {
        CodeBlock.Builder b = CodeBlock.builder();
        if (interceptors != null && interceptors.size() > 0) {
            for (TypeMirror type : interceptors) {
                if (type instanceof Type.ClassType) {
                    Symbol.TypeSymbol e = ((Type.ClassType) type).asElement();
                    if (e instanceof Symbol.ClassSymbol && isInterceptor(e)) {
                        b.add(", new $T()", e);
                    }
                }
            }
        }
        return b.build();
    }

    public void writeInterfaceServiceFile(String interfaceName, Collection<String> lines) {
        writeServiceFile(Const.SERVICE_PATH + interfaceName, lines);
    }

    public void writeServiceFile(String fileName, Collection<String> lines) {
        if (isEmpty(fileName) || isEmpty(lines)) {
            return;
        }
        try {
            FileObject res = filer.createResource(StandardLocation.CLASS_OUTPUT, "", fileName);
            OutputStream os = res.openOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, UTF_8));
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String hash(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(str.hashCode());
        }
    }

    public static boolean notEmpty(String path) {
        return path != null && path.length() > 0;
    }

    public static boolean isEmpty(String path) {
        return path == null || path.length() == 0;
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Returns the binary name of a reference type. For example,
     * {@code com.google.Foo$Bar}, instead of {@code com.google.Foo.Bar}.
     */
    public static String getBinaryName(TypeElement element) {
        return getBinaryNameImpl(element, element.getSimpleName().toString());
    }

    private static String getBinaryNameImpl(TypeElement element, String className) {
        Element enclosingElement = element.getEnclosingElement();

        if (enclosingElement instanceof PackageElement) {
            PackageElement pkg = (PackageElement) enclosingElement;
            if (pkg.isUnnamed()) {
                return className;
            }
            return pkg.getQualifiedName() + "." + className;
        }

        TypeElement typeElement = (TypeElement) enclosingElement;
        return getBinaryNameImpl(typeElement, typeElement.getSimpleName() + "$" + className);
    }
}
