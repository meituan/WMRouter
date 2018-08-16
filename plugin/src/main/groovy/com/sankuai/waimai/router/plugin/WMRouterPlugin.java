package com.sankuai.waimai.router.plugin;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.BaseExtension;
import com.android.build.gradle.api.ApplicationVariant;
import com.android.build.gradle.internal.pipeline.TransformTask;
import com.android.build.gradle.internal.transforms.ProGuardTransform;
import com.sankuai.waimai.router.interfaces.Const;

import org.gradle.api.Action;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.ExtraPropertiesExtension;

import java.util.Set;

/**
 * 插件所做工作：
 * 1、读取ServiceLoader配置文件，检查key是否冲突
 * 2、合并ServiceLoader配置文件到Assets
 * 3、根据ServiceLoader配置生成Class（Release环境）
 * 4、ServiceLoader配置中声明的Class添加到ProGuard的keep列表中
 */
public class WMRouterPlugin implements Plugin<Project> {

    private WMRouterTransform mTransform;

    @Override
    public void apply(Project project) {
        WMRouterExtension extension = project.getExtensions()
                .create(Const.NAME, WMRouterExtension.class);

        if (useTransform(project)) {
            WMRouterLogger.info("register transform");
            mTransform = new WMRouterTransform();
            project.getExtensions().findByType(BaseExtension.class)
                    .registerTransform(mTransform);
        }

        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                if (extension.getEnable()) {
                    WMRouterLogger.setConfig(extension);
                    applyTasks(project, extension);
                }
            }
        });
    }

    private void applyTasks(Project project, WMRouterExtension extension) {
        DomainObjectSet<ApplicationVariant> variants =
                project.getExtensions().findByType(AppExtension.class).getApplicationVariants();

        for (ApplicationVariant variant : variants) {
            String variantName = capitalize(variant.getName());

            WMRouterServiceProcessor processor = new WMRouterServiceProcessor();

            // find services, write assets
            Task mergeJavaResTask = project.getTasks().findByName(
                    "transformResourcesWithMergeJavaResFor" + variantName);
            mergeJavaResTask.doLast(new Action<Task>() {
                @Override
                public void execute(Task task) {
                    processor.findServices(project, mergeJavaResTask.getOutputs(), variant);
                    if (mTransform != null) {
                        mTransform.setProcessor(processor);
                    }
                    processor.writeToAssets(project, variant);
                }
            });

            // config proguard
            Task task = project.getTasks().findByName(
                    "transformClassesAndResourcesWithProguardFor" + variantName);
            if (task instanceof TransformTask &&
                    ((TransformTask) task).getTransform() instanceof ProGuardTransform) {
                task.doFirst(new Action<Task>() {
                    @Override
                    public void execute(Task task) {
                        ProGuardTransform transform =
                                (ProGuardTransform) ((TransformTask) task).getTransform();
                        configureProguard(transform, processor, extension);
                    }
                });
            }
        }
    }

    private void configureProguard(ProGuardTransform transform, WMRouterServiceProcessor processor, WMRouterExtension extension) {
        WMRouterLogger.info("configure proguard");
        // 接口类：keep类名和成员
        Set<String> interfaceNames = processor.getInterfaceNames();
        if (interfaceNames != null && !interfaceNames.isEmpty()) {
            for (String clazz : interfaceNames) {
                try {
                    transform.keep("class " + clazz + " { *; }");
                    WMRouterLogger.debug("keep class '%s'", clazz);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
        // 实现类：keep类名和成员
        Set<String> implementationNames = processor.getImplementationNames();
        if (implementationNames != null && !implementationNames.isEmpty()) {
            for (String clazz : implementationNames) {
                try {
                    transform.keep("class " + clazz + " { *; }");
                    WMRouterLogger.debug("keep class '%s'", clazz);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String capitalize(CharSequence str) {
        return (str == null || str.length() == 0) ? "" : "" + Character.toUpperCase(str.charAt(0))
                + str.subSequence(1, str.length());
    }

    private static boolean useTransform(Project project) {
        ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
        if (ext.has(WMRouterExtension.USE_TRANSFORM)) {
            Object o = ext.get(WMRouterExtension.USE_TRANSFORM);
            if (o instanceof Boolean) {
                return ((Boolean) o);
            }
        }
        for (String taskName : project.getGradle().getStartParameter().getTaskNames()) {
            if (taskName.toLowerCase().endsWith("release")) {
                return true;
            }
        }
        return false;
    }
}
