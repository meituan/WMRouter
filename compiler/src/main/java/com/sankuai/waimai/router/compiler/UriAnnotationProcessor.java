package com.sankuai.waimai.router.compiler;

import com.google.auto.service.AutoService;
import com.sankuai.waimai.router.annotation.RouterUri;
import com.sankuai.waimai.router.interfaces.Const;
import com.squareup.javapoet.CodeBlock;
import com.sun.tools.javac.code.Symbol;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class UriAnnotationProcessor extends BaseProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        if (annotations == null || annotations.isEmpty()) {
            return false;
        }
        CodeBlock.Builder builder = CodeBlock.builder();
        String hash = null;
        for (Element element : env.getElementsAnnotatedWith(RouterUri.class)) {
            if (!(element instanceof Symbol.ClassSymbol)) {
                continue;
            }
            boolean isActivity = isActivity(element);
            boolean isHandler = isHandler(element);
            boolean isFragment = isFragment(element);
            boolean isFragmentV4 = isFragmentV4(element);
            if (!isActivity && !isHandler && !isFragment && !isFragmentV4) {
                continue;
            }

            Symbol.ClassSymbol cls = (Symbol.ClassSymbol) element;
            RouterUri uri = cls.getAnnotation(RouterUri.class);
            if (uri == null) {
                continue;
            }

            if (hash == null) {
                hash = hash(cls.className());
            }
            CodeBlock handler;
            if (isFragment || isFragmentV4) {
                handler = buildFragmentHandler(cls);
            } else {
                handler = buildHandler(isActivity, cls);
            }
            CodeBlock interceptors = buildInterceptors(getInterceptors(uri));

            // scheme, host, path, handler, exported, interceptors
            String[] pathList = uri.path();
            for (String path : pathList) {
                builder.addStatement("handler.register($S, $S, $S, $L, $L$L)",
                        uri.scheme(),
                        uri.host(),
                        path,
                        handler,
                        uri.exported(),
                        interceptors);
            }
        }
        buildHandlerInitClass(builder.build(), "UriAnnotationInit" + Const.SPLITTER + hash,
                Const.URI_ANNOTATION_HANDLER_CLASS, Const.URI_ANNOTATION_INIT_CLASS);
        return true;
    }

    /**
     * 创建Handler。格式：<code>"com.demo.TestActivity"</code> 或 <code>new TestHandler()</code>
     */
    public CodeBlock buildFragmentHandler( Symbol.ClassSymbol cls) {
        CodeBlock.Builder b = CodeBlock.builder();
        b.add("new $T($S)", className(Const.FRAGMENT_HANDLER_CLASS), cls.className());
        return b.build();
    }

    private static List<? extends TypeMirror> getInterceptors(RouterUri scheme) {
        try {
            scheme.interceptors();
        } catch (MirroredTypesException mte) {
            return mte.getTypeMirrors();
        }
        return null;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Collections.singletonList(RouterUri.class.getName()));
    }
}
