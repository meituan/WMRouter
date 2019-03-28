package com.sankuai.waimai.router.compiler;

import com.google.auto.service.AutoService;
import com.sankuai.waimai.router.annotation.RouterPage;
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
public class PageAnnotationProcessor extends BaseProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        if (annotations == null || annotations.isEmpty()) {
            return false;
        }
        CodeBlock.Builder builder = CodeBlock.builder();
        String hash = null;
        for (Element element : env.getElementsAnnotatedWith(RouterPage.class)) {
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
            RouterPage page = cls.getAnnotation(RouterPage.class);
            if (page == null) {
                continue;
            }

            if (hash == null) {
                hash = hash(cls.className());
            }

            CodeBlock handler;
            if(isFragment || isFragmentV4){
                handler = buildFragmentHandler(cls);
            }else {
                handler = buildHandler(isActivity, cls);
            }
            CodeBlock interceptors = buildInterceptors(getInterceptors(page));

            // path, handler, interceptors
            String[] pathList = page.path();
            for (String path : pathList) {
                builder.addStatement("handler.register($S, $L$L)",
                        path,
                        handler,
                        interceptors);
            }
        }
        buildHandlerInitClass(builder.build(), "PageAnnotationInit" + Const.SPLITTER + hash,
                Const.PAGE_ANNOTATION_HANDLER_CLASS, Const.PAGE_ANNOTATION_INIT_CLASS);
        return true;
    }

    private static List<? extends TypeMirror> getInterceptors(RouterPage page) {
        try {
            page.interceptors();
        } catch (MirroredTypesException mte) {
            return mte.getTypeMirrors();
        }
        return null;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Collections.singletonList(RouterPage.class.getName()));
    }


    /**
     * 创建Handler。格式： <code>new FragmentTransactionHandler("FragmentName")</code>
     */
    public CodeBlock buildFragmentHandler( Symbol.ClassSymbol cls) {
        CodeBlock.Builder b = CodeBlock.builder();
        b.add("new $T($S)", className(Const.FRAGMENT_HANDLER_CLASS), cls.className());
        return b.build();
    }
}
