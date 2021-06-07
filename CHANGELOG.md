# 更新日志

## [Unreleased]

- 修复非增量编译时，没有清空输出目录的问题(#88)
- 修改group为：io.github.meituan-dianping
- 迁移发布到mavenCenter
- 升级gradle版本到4.10，兼容Android Studio 4.2版本

## 1.2.0

- 添加FragmentTransactionUriRequest和基类，支持Activity to Fragment和Fragment to Fragment 的路由跳转
- 修改PageAnnotationProcessor，支持@RouterPage注解的Fragment的自动配置FragmentTransactionHandler
- 修复在开启instant_run的情况下编译出错的问题
- 修复多模块下dex archive merge失败问题
- 修复URI分发路径不匹配问题


## 1.1.2

- 升级Android Gradle Plugin版本为3.2.1
- 添加FragmentUriRequest，用于从fragment跳转的场景


## 1.1.0

### 改动

- Gradle插件方案升级，降低插件复杂度，提高兼容和可靠性，解决高版本Android Gradle Plugin的兼容问题。
    > 备注：原先的方案是annotationProcessor生成Java代码+Java资源文件，Gradle插件在Debug模式下合并资源文件到Assets，Release模式使用Transform生成初始化类。
    > 新方案为注解直接生成Java初始化代码，Gradle插件使用Transform合并初始化代码。
- Windows平台编译兼容问题解决。
- ServiceLoader的字符串引用接口和实现类，改为直接引用class，支持Proguard优化，运行时不需要反射class；为避免main dex capacity exceeded问题，ServiceLoaderInit初始化类改为反射调用。
- 去掉原先的自动配置Proguard功能，用户可根据需要自行配置，进一步提升Proguard优化空间。
- 插件的代码细节优化完善，增加若干注释。

### 升级需知

- 由于实现方案变动，从低版本升级到1.1.0版本，需要同步升级router模块依赖、注解生成器、Gradle插件，且所有配置了注解生成器的模块要重新发布AAR，否则会出现问题。
- 需要配置Proguard，详见最新使用文档。

## 1.0.42

- 在UriRequest中加入对是否跳过拦截器的控制
- 对应@RouterPage注解的Activity的启动，自动拼装PageAnnotationHandler.SCHEME_HOST和path
- 去掉ChainedAsyncHelper

## 1.0.41

- 第一个开源版本
