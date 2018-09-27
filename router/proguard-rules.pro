# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 保留ServiceLoaderInit类，由反射调用
-keep class com.sankuai.waimai.router.generated.ServiceLoaderInit { *; }

# 避免注解在shrink阶段就被移除，导致obfuscate阶段注解失效、实现类仍然被混淆
-keep @interface com.sankuai.waimai.router.annotation.RouterService
