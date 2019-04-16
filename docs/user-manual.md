# WMRouter设计与使用文档


## 目录

<!-- TOC -->

- [URI跳转核心设计思路与接口](#uri跳转核心设计思路与接口)
- [接入](#接入)
- [发起URI跳转](#发起uri跳转)
- [URI分发流程与配置](#uri分发流程与配置)
- [ServiceLoader模块的使用](#serviceloader模块的使用)
- [高级配置](#高级配置)
- [注意事项](#注意事项)
- [常见问题排查](#常见问题排查)

<!-- /TOC -->


## URI跳转核心设计思路与接口

下图展示了WMRouter中URI跳转的核心设计思路。借鉴网络请求的机制，WMRouter中的每次URI跳转视为发起一个UriRequest；URI跳转请求被WMRouter逐层分发给一系列的UriHandler进行处理；每个UriHandler处理之前可以被UriInterceptor拦截，并插入一些特殊操作。

![](images/design.png)


### UriRequest

UriRequest中包含Context、URI和Fields，其中Fields为HashMap<String, Object>，可以通过Key存放任意数据。简单起见，UriRequest类同时承担了Response的功能，跳转请求的结果，也会被保存到Fields中。
存放到Fields中的常见字段举例如下，也可以根据需要自定义，**为了避免冲突，建议字段名用完整的包名开头**。

- Intent的Extra参数，Bundle类型
- 用于startActivityForResult的RequestCode，int类型
- 用于overridePendingTransition方法的页面切换动画资源，int[]类型
- 本次跳转结果的监听器，OnCompleteListener类型


每次URI跳转请求会有一个ResultCode（类似HTTP请求的ResponseCode），表示跳转结果，也存放在Fields中。常见Code如下，用户也可以自定义Code，**为了避免冲突，自定义Code应使用负数值**。

- 200：跳转成功
- 301：重定向到其他URI，会再次跳转
- 400：请求错误，通常是Context或URI为空
- 403：禁止跳转，例如跳转白名单以外的HTTP链接、Activity的exported为false等
- 404：找不到目标(Activity或UriHandler)
- 500：发生错误

总结来说，UriRequest用于实现一次URI跳转中所有组件之间的通信功能。


### UriHandler

UriHandler用于处理URI跳转请求，可以嵌套从而逐层分发和处理请求。UriHandler是异步结构，接收到UriRequest后处理（例如跳转Activity等），如果处理完成，则调用`callback.onComplete()`并传入ResultCode；如果没有处理，则调用`callback.onNext()`继续分发。

下面的示例代码展示了一个只处理HTTP链接的UriHandler的实现。

```java
public interface UriCallback {

    /**
     * 处理完成，继续后续流程。
     */
    void onNext();

    /**
     * 处理完成，终止分发流程。
     *
     * @param resultCode 结果
     */
    void onComplete(int resultCode);
}

public class DemoUriHandler extends UriHandler {
    public void handle(@NonNull final UriRequest request, @NonNull final UriCallback callback) {
        Uri uri = request.getUri();
        // 处理HTTP链接
        if ("http".equalsIgnoreCase(uri.getScheme())) {
            try {
                // 调用系统浏览器
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(uri);
                request.getContext().startActivity(intent);
                // 跳转成功
                callback.onComplete(UriResult.CODE_SUCCESS);
            } catch (Exception e) {
                // 跳转失败
                callback.onComplete(UriResult.CODE_ERROR);
            }
        } else {
            // 非HTTP链接不处理，继续分发
            callback.onNext();
        }
    }
    // ...
}
```


### UriInterceptor

UriInterceptor为拦截器，不做最终的URI跳转操作，但可以在最终的跳转前进行各种同步/异步操作，常见操作举例如下：

- URI跳转拦截，禁止特定的URI跳转，直接返回403（例如禁止跳转非meituan域名的HTTP链接）
- URI参数修改（例如在HTTP链接末尾添加query参数）
- 各种中间处理（例如打开登录页登录、获取定位、发网络请求）
- ……

每个UriHandler都可以添加若干UriInterceptor。在UriHandler基类中，handle()方法先调用抽象方法`shouldHandle()`判断是否要处理UriRequest，如果需要处理，则逐个执行Interceptor，最后再调用`handleInternal()`方法进行跳转操作。

举例来说，跳转某些页面需要先登录，可以实现一个LoginInterceptor如下。

```java
public class LoginInterceptor implements UriInterceptor {

    @Override
    public void intercept(@NonNull UriRequest request, @NonNull final UriCallback callback) {
        final FakeAccountService accountService = FakeAccountService.getInstance();
        if (accountService.isLogin()) {
            // 已经登录，不需处理，继续跳转流程
            callback.onNext();
        } else {
            // 没登录，提示登录并启动登录页
            Toast.makeText(request.getContext(), "请先登录~", Toast.LENGTH_SHORT).show();
            accountService.registerObserver(new FakeAccountService.Observer() {
                @Override
                public void onLoginSuccess() {
                    accountService.unregisterObserver(this);
                    // 登录成功，继续跳转
                    callback.onNext();
                }

                @Override
                public void onLoginFailure() {
                    accountService.unregisterObserver(this);
                    // 登录失败，终止流程，返回错误ResultCode
                    callback.onComplete(CustomUriResult.CODE_LOGIN_FAILURE);
                }
            });
            // 启动登录页
            startActivity(request.getContext(), LoginActivity.class);
        }
    }
}
```


### 灵活性与易用性的平衡

由于WMRouter是一个开放式组件化框架，UriRequest可以存放任意数据，UriHandler、UriInterceptor可以完全自定义，不同的UriHandler可以任意组合，具有很大的灵活性。但过于灵活容易导致易用性的下降，即使对于最常规最简单的应用，也需要复杂的配置才能完成功能。

为了在两者之间平衡，WMRouter对包结构进行了划分，核心接口和实现类提供基础通用能力，尽可能保留最大的灵活性。可以在core包基础上进行自定义开发和配置，独立运行。

- core：提供核心接口和实现类，提供基础通用能力。
- utils：通用工具类。
- components：辅助功能组件。

在保证核心组件灵活性的基础上，WMRouter又封装了一系列通用实现类，并组合成一套默认实现，满足绝大多数使用场景。

- activity：Activity跳转相关。
- regex：正则匹配相关。
- common：UriHandler、UriInterceptor、UriRequest通用实现类。

WMRouter还提供了ServiceLoader模块。

- service：ServiceLoader模块。
- method：方法调用，提供了几个通用接口，基于ServiceLoader实现方法调用。


## 接入

### Gradle配置

1. 在基础库中增加依赖（1.x为版本号）。

    ```groovy
    repositories {
        jcenter()
    }
    dependencies {
        compile 'com.sankuai.waimai.router:router:1.x'
    }
    ```

2. 在使用了注解的每个模块中配置注解生成器，包括Application和Library工程。

    Java模块的配置：

    ```groovy
    repositories {
        jcenter()
    }
    dependencies {
        annotationProcessor 'com.sankuai.waimai.router:compiler:1.x'
    }
    ```

    Kotlin模块的配置：

    ```groovy
    apply plugin: 'com.android.library'
    apply plugin: 'kotlin-android'
    apply plugin: 'kotlin-android-extensions'
    // 添加kapt插件
    apply plugin: 'kotlin-kapt'

    repositories {
        jcenter()
    }
    dependencies {
        kapt 'com.sankuai.waimai.router:compiler:1.x'
    }
    ```

3. 在Application工程中，配置Gradle插件。

    根目录的`build.gradle`：

    ```groovy
    buildscript {
        repositories {
            jcenter()
        }
        dependencies {
            // Android Gradle插件
            classpath 'com.android.tools.build:gradle:3.2.1'
            // 添加WMRouter插件
            classpath "com.sankuai.waimai.router:plugin:1.x"
        }
    }
    ```

    > 注意：如果项目配置的Android Gradle插件版本比WMRouter依赖的版本低，默认会覆盖为高版本（可通过`./gradlew buildEnvironment`命令查看classpath的依赖关系）。如果不希望被覆盖，可以尝试把配置改成：
    > ```groovy
    > classpath("com.sankuai.waimai.router:plugin:1.x") {
    >     exclude group: 'com.android.tools.build'
    > }
    > ```

    Application模块中的`build.gradle`：

    ```groovy
    apply plugin: 'com.android.application'
    // 应用WMRouter插件
    apply plugin: 'WMRouter'
    ```

4. Proguard配置。

    WMRouter已经内置的Proguard配置如下（详见源码`router/proguard-rules.pro`），使用AAR依赖时一般不需要重复配置。

    ```bash
    # 保留ServiceLoaderInit类，需要反射调用
    -keep class com.sankuai.waimai.router.generated.ServiceLoaderInit { *; }

    # 避免注解在shrink阶段就被移除，导致obfuscate阶段注解失效、实现类仍然被混淆
    -keep @interface com.sankuai.waimai.router.annotation.RouterService
    ```

    如果使用了`@RouterService`注解和ServiceLoader加载实例的功能，会反射调用构造方法，应根据实际情况配置Proguard，避免实现类中的构造方法被移除，示例如下。

    ```bash
    # 使用了RouterService注解的实现类，需要避免Proguard把构造方法、方法等成员移除(shrink)或混淆(obfuscate)，导致无法反射调用。实现类的类名可以混淆。
    -keepclassmembers @com.sankuai.waimai.router.annotation.RouterService class * { *; }
    ```


### 初始化

在`Application.onCreate`中初始化：

```java
// 创建RootHandler
DefaultRootUriHandler rootHandler = new DefaultRootUriHandler(context);

// 初始化
Router.init(rootHandler);
```


### Manifest与外部跳转配置

跳转的目标Activity不需要配置IntentFilter，也不需要配置exported。

```xml
<activity name = ".AccountActivity" />
```

所有的外部URI跳转建议由一个中转Activity接收，再调用Router跳转到目标页面。由于跳转过程中可能会触发定位、登录等各种异步逻辑，因此中转Activity应该有界面，并监听在跳转结束后关闭Activity。

```xml
<activity android:name=".UriProxyActivity" android:exported="true">
    <intent-filter>
        <!-- 接收所有scheme为demo的外部URI跳转，不区分host和path -->
        <data android:scheme="demo"/>
    </intent-filter>
</activity>
```

```java
public class UriProxyActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultUriRequest.startFromProxyActivity(this, new OnCompleteListener() {
            @Override
            public void onSuccess(@NonNull UriRequest request) {
                finish();
            }

            @Override
            public void onError(@NonNull UriRequest request, int resultCode) {
                finish();
            }
        });
    }
}
```


### 其他可选配置

请参考后文“高级配置”中的说明。


## 发起URI跳转

启动一个URI。

```java
// 直接传context和URI
Router.startUri(context, "/account");

// 或构造一个UriRequest
Router.startUri(new UriRequest(context, "/account"))
```

使用UriRequest的默认封装子类DefaultUriRequest，以Builder形式给本次跳转设置各种参数。

```java
new DefaultUriRequest(context, "/account")
        // startActivityForResult使用的RequestCode
        .activityRequestCode(100)
        // 设置跳转来源，默认为内部跳转，还可以是来自WebView、来自Push通知等。
        // 目标Activity可通过UriSourceTools区分跳转来源。
        .from(UriSourceTools.FROM_INTERNAL)
        // Intent加参数
        .putIntentExtra("test-int", 1)
        .putIntentExtra("test-string", "str")
        // 设置Activity跳转动画
        .overridePendingTransition(R.anim.enter_activity, R.anim.exit_activity)
        // 监听跳转完成事件
        .onComplete(new OnCompleteListener() {
            @Override
            public void onSuccess(@NonNull UriRequest request) {
                ToastUtils.showToast(request.getContext(), "跳转成功");
            }

            @Override
            public void onError(@NonNull UriRequest request, int resultCode) {

            }
        })
        // 这里的start实际也是调用了Router.startUri方法
        .start();
```


## URI分发流程与配置

### 概述

DefaultRootUriHandler在接收到UriRequest后，会依次尝试分发给PageAnnotationHandler、UriAnnotationHandler、RegexAnnotationHandler、StartUriHandler，如图所示。

1. PageAnnotationHandler处理所有`wm_router://page/*`形式的URI跳转，根据path匹配由`RouterPage`注解配置的节点。

2. UriAnnotationHandler根据URI的scheme+host，寻找并分发给对应的PathHandler，之后PathHandler再根据path匹配`RouterUri`注解配置的节点。

3. RegexAnnotationHandler根据优先级和正则匹配尝试将URI分发给`RouterRegex`配置的每个节点。

4. StartUriHandler尝试直接使用Android原生的隐式跳转启动URI，用于处理其他类型的URI，例如`tel:*`、`mailto:*`。

![](images/default-implements.png)


### RouterUri注解

RouterUri注解可用于Activity或UriHandler的非抽象子类。Activity也会被转化成UriHandler，在Activity中可以通过`Intent.getData()`获取到URI。

参数如下：

- path：跳转URI要用的path，必填。path应该以"/"开头，支持配置多个path。
- scheme、host：跳转URI的scheme和host，可选。
- exported：是否允许外部跳转，可选，默认为false。
- interceptors：要添加的Interceptor，可选，支持配置多个。

说明：

1. WMRouter支持多scheme+host+path的跳转，也支持只有path的跳转。如果RouterUri中配置了scheme、host、path，则跳转时应使用scheme+host+path的完整路径；如果RouterUri中只配置了path，则跳转应直接使用path。

2. 由于多数场景下往往只需要一个固定的scheme+host，不想在每个RouterUri注解上都写一遍scheme、host，这种场景可以在初始化时用`new DefaultRootUriHandler("scheme", "host")`指定默认的scheme、host，RouterUri没有配置的字段会使用这个默认值。


#### 举例

1、用户账户页面只配置path；跳转前要先登录，因此添加了一个LoginInterceptor。

```java
@RouterUri(path = "/account", interceptors = LoginInterceptor.class)
public class UserAccountActivity extends Activity {

}
```

```java
Router.startUri(context, "/account");
```

2、一个页面配置多个path。

```java
@RouterUri(scheme = "demo_scheme", host = "demo_host", path = {"/path1"， "/path2"})
public class TestActivity extends Activity {

}
```

```java
Router.startUri(context, "demo_scheme://demo_host/path1");
Router.startUri(context, "demo_scheme://demo_host/path2");
```

3、根据后台下发的ABTest策略，同一个链接跳转不同的Activity。其中AbsActivityHandler是WMRouter提供的用于跳转Activity的UriHandler通用基类。

```java
@RouterUri(path = "/home")
public class HomeABTestHandler extends AbsActivityHandler {

    @NonNull
    @Override
    protected Intent createIntent(@NonNull UriRequest request) {
        if (FakeABTestService.getHomeABStrategy().equals("A")) {
            return new Intent(request.getContext(), HomeActivityA.class);
        } else {
            return new Intent(request.getContext(), HomeActivityB.class);
        }
    }
}
```

```java
Router.startUri(context, "/home");
```


### RouterRegex注解

RouterRegex注解也可以用于Activity和UriHandler，通过正则进行URI匹配。

参数如下：

- regex：正则表达式，必填。用于匹配完整的URI字符串。
- priority：优先级，数字越大越先匹配，可选，默认为0。优先级相同时，不保证先后顺序。
- exported：是否允许外部跳转，可选，默认为false。
- interceptors：要添加的Interceptor，可选，支持配置多个。


#### 举例

1、对于指定域名的http(s)链接，使用特定的WebViewActivity打开。

```java
@RouterRegex(regex = "http(s)?://(.*\\.)?(meituan|sankuai|dianping)\\.(com|info|cn).*", priority = 2)
public class WebViewActivity extends BaseActivity {

}
```

2、对于其他http(s)链接，使用系统浏览器打开。

```java
@RouterRegex(regex = "http(s)?://.*", priority = 1)
public class SystemBrowserHandler extends UriHandler {

    @Override
    protected boolean shouldHandle(@NonNull UriRequest request) {
        return true;
    }

    @Override
    protected void handleInternal(@NonNull UriRequest request, @NonNull UriCallback callback) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(request.getUri());
            request.getContext().startActivity(intent);
            callback.onComplete(UriResult.CODE_SUCCESS);
        } catch (Exception e) {
            callback.onComplete(UriResult.CODE_ERROR);
        }
    }
}
```


### RouterPage注解

实际项目开发过程中，URI跳转常有两种需求：一种是运营后台配置和下发链接让客户端跳转，跳转协议需要和各端保持一致；另一种是App拆分多个工程，需要在工程之间跳转页面，使用路由组件代替显式跳转，实现解耦。

由于种种历史原因，这两套URI可能会出现不兼容的问题，因此需要对两套URI分别做实现。RouterPage注解就是用于实现内部页面跳转而设计的。

RouterPage注解用于指定内部页面跳转，和RouterUri注解相比，RouterPage注解对应的scheme和host为固定的`wm_router://page`，不可配置，exported为false也不可配置。

参数如下：

- path：跳转URI要用的path，必填。path应该以"/"开头，支持配置多个path。
- interceptors：要添加的Interceptor，可选，支持配置多个。


#### 举例

```java
// demo://demo/account
@RouterUri(path = "/account", scheme = "demo", host = "demo")
// wm_router://page/account
@RouterPage(path = "/account")
public class AccountActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PageAnnotationHandler.isPageJump(getIntent())) {
            // ...
        } else {
            // ...
        }
    }
}
```

```java
// 对应RouterUri的配置
Router.startUri(context, "demo://demo/account");
```

```java
// 对应RouterPage的配置
Router.startUri(context, PageAnnotationHandler.SCHEME_HOST + "/account");
// 或直接用常量的值
Router.startUri(context, "wm_router://page/account");
```


## ServiceLoader模块的使用

ServiceLoader模块使用主要分三步：

1. 定义Java接口
2. 注解声明实现类
3. 加载实现类

### 1、RouterService注解声明实现类

通过RouterService注解声明实现类所实现的接口（**或继承的父类，例如Activity、Fragment、Object等，后文不再重复说明**），一个接口可以有多个实现类，一个类也可以同时实现多个接口。RouterService注解的参数如下：

- interfaces：必选参数。声明实现的接口，可配置多个。
- key：可选参数。同一接口的不同实现类，通过唯一的key进行区分。
- singleton：可选参数。声明实现类是否为单例，默认为false。

示例如下：

```java
public interface IService {

}

@RouterService(interfaces = IService.class, key = 'key1')
public static class ServiceImpl1 implements IService {

}

@RouterService(interfaces = IService.class, key = 'key2', singleton = true)
public static class ServiceImpl2 implements IService {

}
```


### 2、获取实现类的Class

可以直接获取实现类的Class，例如获取Activity的Class进行页面跳转。

#### 2.1 指定接口和Key，获取某个实现类的Class（要求注解声明时指定了Key）

```java
Class<IService> clazz = Router.getServiceClass(IService.class, "key1");
```

#### 2.2 指定接口，获取注解声明的所有实现类的Class

```java
List<Class<IService>> classes = Router.getAllServiceClasses(IService.class);
```


### 3、获取实现类的实例

ServiceLoader更常见的使用场景，是获取实现类的实例而不是Class。实现类的构造在ServiceLoader中最终由Factory实现，构造失败会返回null或空数组。

#### 3.1 无参数构造

```java
// 使用无参构造函数
IService service = Router.getService(IService.class, "key1");
List<IService> list = Router.getAllServices(IService.class);
```

#### 3.2 Context参数构造

```java
// 使用Context参数构造
IService service = Router.getService(IService.class, "key1", context);
List<IService> list = Router.getAllServices(IService.class, context);
```

#### 3.3 自定义Factory通过反射构造

对于实现类有特殊构造函数的情况，可以通过Factory自行从class获取构造方法进行构造，示例如下：

```java
// 使用自定义Factory
IFactory factory = new IFactory() {
    public Object create(Class clazz) {
        return clazz.getConstructor().newInstance();
    }
};
IService service = Router.getService(IService.class, "key1", factory);
List<IService> list = Router.getAllServices(IService.class, factory);
```

#### 3.4 使用Provider提供实例

在声明实现类时，可以在类中定义一个返回值类型为该实现类且无参数的静态方法，并使用RouterProvider注解标注。当调用Router获取实例时，如果没有指定Factory，则优先调用Provider方法获取实例，找不到Provider再使用无参数构造。使用示例如下：

```java

@RouterService(interfaces = IService.class, key = 'key', singleton = true)
public static class ServiceImpl implements IService {

    public static final ServiceImpl INSTANCE = new ServiceImpl();

    // 使用注解声明该方法是一个Provider
    @RouterProvider
    public static ServiceImpl provideInstance() {
        return INSTANCE;
    }
}

// 调用时不传Factory，优先找Provider，找不到再使用无参数构造
IService service = Router.getService(IService.class, "key");
List<IService> list = Router.getAllServices(IService.class);
```

#### 3.5 singleton参数说明

注解声明为singleton的单例实现类，在调用`getService()/getAllServices()`方式获取实例时，实例会由单例缓存池管理，WMRouter中不会重复构造，且线程安全。

**注意：当通过ServiceLoader获取Class、直接调用等其他方式使用实现类时，应避免重复创建对象，否则会导致单例失效。可以结合Provider确保实例不会重复创建。**


### 4、方法调用

利用ServiceLoader可以实现方法调用。

WMRouter的method包中提供了通用的Function接口Func0 ~ Func9、FuncN，分别表示参数为0~9或可变参数的方法。声明方法时应该实现Function接口，示例如下。对于调用次数较多的方法，建议声明为singleton，避免每次调用时重复创建。

```java
@RouterService(interfaces = Func2.class, key = "/add", singleton = true)
public class AddMethod implements Func2<Integer, Integer, Integer> {
    @Override
    public Integer call(Integer a, Integer b) {
        return a + b;
    }
}
```

方法的调用示例如下：

```java
Func2<Integer, Integer, Integer> addMethod = Router.getService(Func2.class, "/add");
Integer result = addMethod.call(1, 2);
```

也可以直接通过`callMethod`调用，根据参数个数匹配对应的Function接口。

```java
Integer result = Router.callMethod("/add", 1, 2);
```


## 高级配置

WMRouter提供了一系列灵活的可选配置项，实现更好的性能、可靠性，满足更多定制化需求。


### 初始化性能与懒加载

WMRouter中，加载注解配置的页面、加载Service涉及到资源文件的读取解析、反射获取Class和创建实例，存在一定的性能消耗。为避免初始化时间过长影响App启动速度，一些组件会使用懒加载机制进行初始化（具体实现可参考`LazyInitHelper`）：

- 一些初始化任务可以在使用时在主线程按需初始化。
- 也可以在App启动时在后台线程提前初始化，使用时会先等待初始化完成。


因此为了提高App运行性能，初始化时除了在主线程调用init，还可以在后台线程调用`lazyInit()`提前启动一些懒加载的初始化操作，示例如下：

```java
void initRouter(Context context) {
    // 必选，需要在主线程执行
    Router.init(new DefaultRootUriHandler(context));
    // 其他各种配置
    // ...
    // 后台线程懒加载
    new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void[] objects) {
            Router.lazyInit();
            return null;
        }
    }.execute();
}
```


### 配置检查与Debugger配置

使用注解进行配置，注解往往分散在一个工程的不同代码文件甚至不同的工程中。如果没有很好的文档或代码约束，很容易出现多个页面配置了相同的URI或Service导致冲突的问题。

因此WMRouter在注解生成阶段、APK打包阶段，使用注解生成器和Gradle插件进行检查，检查到配置冲突或错误会抛异常，中断编译。

WMRouter中的Debugger用于调试和Log输出，运行时也会对一些配置进行检查，如果出现配置用法错误或其他严重问题会调用`Debugger.fatal()`抛出。

Debugger建议配置使用DefaultLogger：

- 测试环境下开启Debug模式，fatal错误会抛出异常及时暴漏问题；

- 线上环境关闭Debug模式，发生问题不抛异常；可以通过覆写DefaultLogger上报Error和Fatal级别的问题。

代码如下：

```java
// 自定义Logger
DefaultLogger logger = new DefaultLogger() {
    @Override
    protected void handleError(Throwable t) {
        super.handleError(t);
        // 此处上报Fatal级别的异常
    }
};

// 设置Logger
Debugger.setLogger(logger);

// Log开关，建议测试环境下开启，方便排查问题。
Debugger.setEnableLog(true);

// 调试开关，建议测试环境下开启。调试模式下，严重问题直接抛异常，及时暴漏出来。
Debugger.setEnableDebug(true);
```


### 跳转来源与Exported控制

WMRouter在URI跳转时可以通过`DefaultUriRequest.from()`设置跳转来源参数，包括内部跳转、外部跳转、来自WebView的跳转、来自Push通知的跳转等，也可以自定义跳转来源，具体实现参考UriSourceTools。

跳转来源可以用于实现Exported控制、埋点统计、特殊业务逻辑等。其中Exported控制类似Android中Activity原生的Exported属性，默认为false，表示不允许来自外部的跳转，从而避免一些安全问题或功能异常。外部跳转由UriProxyActivity统一接收，然后调用WMRouter跳转并设置from为`UriSourceTools.FROM_EXTERNAL`，之后UriHandler通过跳转来源和页面的Exported配置即可判断是否允许跳转。

通过`UriSourceTools.setDisableExportedControl`可以开启或关闭Exported控制。


### 降级策略的配置

WMRouter支持配置全局和局部降级策略。

使用`RootUriHandler.setGlobalOnCompleteListener()`设置全局跳转完成的监听，可以在其中跳转失败时执行全局降级逻辑。在DefaultRootUriHandler中默认配置的GlobalOnCompleteListener会在跳转失败时弹Toast提示用户。

局部降级策略可以通过组合或覆写UriHandler实现。例如PageAnnotationHandler中设置了NotFoundHandler，当所有RouterPage注解配置的页面都没有匹配时，则使用NotFoundHandler作为降级策略，返回`UriResult.CODE_NOT_FOUND`。


### 核心组件的扩展

根据实际情况，可以自定义具有各种功能的UriHandler和UriInterceptor，前面已经提到，不再赘述。一般使用DefaultRootHandler和DefaultUriRequest，以及少量自定义的UriHandler已经可以满足绝大多数需求。如果有更复杂的场景需要，WMRouter中的核心组件可以通过继承、组合等方式实现更灵活的定制。例如自定义RootUriHandler示例如下：

```java
// 自定义RootUriHandler
public class CustomRootUriHandler extends RootUriHandler {
    // ...
    public CustomRootUriHandler() {
        // 添加Uri注解支持
        addHandler(new UriAnnotationHandler());
        // 添加一个自定义的HttpHandler
        addHandler(new CustomHttpHandler());
    }
}

// 自定义UriRequest
public class CustomUriRequest extends UriRequest {
    // ...
    public CustomUriRequest setCustomProperties(String s) {
        putField("custom_properties", s);
        return this;
    }
}

// 初始化
Router.init(new CustomRootUriHandler());

// 启动Uri
CustomUriRequest request = new CustomUriRequest(mContext, url)
    .setCustomProperties("xxx");
Router.startUri(request);
```


## 注意事项

`wm_router`为保留scheme，用于实现RouterPage等的路由，自定义的URI请勿使用`wm_router://*`的形式。


## 常见问题排查

由于配置不正确、编译环境、兼容性等原因，有时WMRouter会出现无法跳转页面、ServiceLoader加载出错等问题，这里给出常见排查思路。


### 1、检查注解生成器是否配置

每个使用了注解的模块都需要配置注解生成器（annotationProcessor或kapt），包括Application和Library工程。


### 2、检查主工程Gradle插件是否配置


### 3、检查版本号

检查各个工程的注解生成器、Gradle插件、依赖的router模块版本是否一致。由于插件方案变动，各个模块版本应保持一致，配置了注解生成器的AAR建议重新打包，避免兼容问题。


### 4、尝试clean之后重新编译


### 5、检查注解生成器是否正常工作

#### 1.0.x版本

- 对于源码依赖的Library模块，检查`build/generated/source/apt`目录是否生成了Java初始化类，`build/intermediates/classes`目录是否生成Java资源文件，且内容指向Java初始化类，如图所示。
- 对于依赖的AAR模块，可以在Android Studio的External Libraries中查看AAR里是否包含这些文件。

![](images/debug-1.png)

![](images/debug-2.png)

#### 1.1.x及以上版本

- 对于源码依赖的Library模块，检查`build/generated/source/apt`目录中是否生成了Java初始化类，如图所示。
- 对于依赖的AAR模块，可以在Android Studio的External Libraries中查看AAR里是否包含Java初始化类。

![](images/debug-check-init-class.png)


### 6、检查Gradle插件是否正常工作

#### 1.0.x版本

assets是否正确生成。Gradle插件会将注解生成器生成的资源文件合并到`build/intermediates/assets/{buildVariant}/wm-router/services`目录，其内容指向所有初始化类。

![](images/debug-3.png)

#### 1.1.x及以上版本

查看Gradle编译输出的Log，是否正确找到了注解生成器生成的初始化类；反编译APK查看`com.sankuai.waimai.router.generated.ServiceLoaderInit`类中的内容是否正常。

![](images/debug-check-plugin-find-service.png)


### 7、查看运行时Log

如果还没有解决问题，可能是工程中的配置或用法不正确。通过`Debugger.setLogger(logger)`配置好Logger，在LogCat中过滤`WMRouter`标签查看Log。例如WMRouter在跳转过程中经过的UriHandler和UriInterceptor，如图。

![](images/debug-4.png)


### 8、断点调试

还可以打断点调试分析具体原因，例如最常用的注解RouterUri配置的节点，应该由`UriAnnotationHandler`根据scheme+host分发给 `PathHandler`，再由`PathHandler`根据path分发处理，断点可以打在`shouldHandle`、`handleInternal`等方法中。


### 9、提交Issue

如果还是没能解决问题，需要帮助或发现BUG，请在Github[提交Issue](https://github.com/meituan/WMRouter/issues)。
