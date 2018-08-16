package com.sankuai.waimai.router.plugin;

import com.android.build.api.transform.Format;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;

import com.google.common.collect.ImmutableSet;
import com.sankuai.waimai.router.interfaces.Const;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class WMRouterTransform extends Transform {

    private static final String TRANSFORM = "Transform: ";

    private WMRouterServiceProcessor mProcessor;

    public void setProcessor(WMRouterServiceProcessor processor) {
        mProcessor = processor;
    }

    @Override
    public String getName() {
        return Const.NAME;
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_JARS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation invocation) {
        WMRouterLogger.info(TRANSFORM + "start...");
        long ms = System.currentTimeMillis();

        for (TransformInput input : invocation.getInputs()) {
            input.getJarInputs().parallelStream().forEach(jarInput -> {
                File dest = invocation.getOutputProvider().getContentLocation(
                        jarInput.getName(), jarInput.getContentTypes(), jarInput.getScopes(),
                        Format.JAR);
                try {
                    FileUtils.copyFile(jarInput.getFile(), dest);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            input.getDirectoryInputs().parallelStream().forEach(directoryInput -> {
                File dest = invocation.getOutputProvider().getContentLocation(
                        directoryInput.getName(), directoryInput.getContentTypes(),
                        directoryInput.getScopes(), Format.DIRECTORY);
                try {
                    FileUtils.copyDirectory(directoryInput.getFile(), dest);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        if (mProcessor != null) {
            File dest = invocation.getOutputProvider().getContentLocation(
                    "WMRouter", TransformManager.CONTENT_CLASS,
                    ImmutableSet.of(QualifiedContent.Scope.PROJECT), Format.DIRECTORY);
            mProcessor.generateServiceInitClass(dest.getAbsolutePath());
        }

        WMRouterLogger.info(TRANSFORM + "cost %s ms", System.currentTimeMillis() - ms);
    }
}
