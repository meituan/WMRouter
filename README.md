![](docs/images/banner.png)

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://raw.githubusercontent.com/meituan/WMRouter/master/LICENSE)
[![Release Version](https://img.shields.io/badge/release-1.2.0-red.svg)](https://github.com/meituan/WMRouter/releases)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/meituan/WMRouter/pulls)

WMRouter是一款Android路由框架，基于组件化的设计思路，有功能灵活、使用简单的特点。

# 因为JCenter下线，从1.2.1开始，Group 从 com.sankuai.waimai.router 变更为 io.github.meituan-dianping

如果有多个组件仓库，可以使用下面的方案批量替换一下依赖，避免类重复

```
allprojects {
    configurations.all { Configuration c ->
      resolutionStrategy.eachDependency { DependencyResolveDetails details ->
                 if (details.requested.group == 'com.sankuai.waimai.router') {
                     details.useTarget group: 'io.github.meituan-dianping'
                 }
         }
    }
}
```

## 功能简介

WMRouter主要提供URI分发、ServiceLoader两大功能。

URI分发功能可用于多工程之间的页面跳转、动态下发URI链接的跳转等场景，特点如下：

1. 支持多scheme、host、path
2. 支持URI正则匹配
3. 页面配置支持Java代码动态注册，或注解配置自动注册
4. 支持配置全局和局部拦截器，可在跳转前执行同步/异步操作，例如定位、登录等
5. 支持单次跳转特殊操作：Intent设置Extra/Flags、设置跳转动画、自定义StartActivity操作等
6. 支持页面Exported控制，特定页面不允许外部跳转
7. 支持配置全局和局部降级策略
8. 支持配置单次和全局跳转监听
9. 完全组件化设计，核心组件均可扩展、按需组合，实现灵活强大的功能


基于[SPI (Service Provider Interfaces) ](https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html)的设计思想，WMRouter提供了ServiceLoader模块，类似Java中的`java.util.ServiceLoader`，但功能更加完善。通过ServiceLoader可以在一个App的多个模块之间通过接口调用代码，实现模块解耦，便于实现组件化、模块间通信，以及和依赖注入类似的功能等。其特点如下：

1. 使用注解自动配置
2. 支持获取接口的所有实现，或根据Key获取特定实现
3. 支持获取Class或获取实例
4. 支持无参构造、Context构造，或自定义Factory、Provider构造
5. 支持单例管理
6. 支持方法调用


其他特性：

1. 优化的Gradle插件，对编译耗时影响较小
2. 编译期和运行时配置检查，避免配置冲突和错误
3. 完善的调试功能，帮助及时发现问题


## 适用场景

WMRouter适用但不限于以下场景：

1. Native+H5混合开发模式，需要进行页面之间的互相跳转，或进行灵活的运营跳转链接下发。可以利用WMRouter统一页面跳转逻辑，根据不同的协议（HTTP、HTTPS、用于Native页面的自定义协议）跳转对应页面，且在跳转过程中可以使用UriInterceptor对跳转链接进行修改，例如跳转H5页面时在URL中加参数。

2. 统一管理来自App外部的URI跳转。来自App外部的URI跳转，如果使用Android原生的Manifest配置，会直接启动匹配的Activity，而很多时候希望先正常启动App打开首页，完成常规初始化流程（例如登录、定位等）后再跳转目标页面。此时可以使用统一的Activity接收所有外部URI跳转，到首页时再用WMRouter启动目标页面。

3. 页面跳转有复杂判断逻辑的场景。例如多个页面都需要先登录、先定位后才允许打开，如果使用常规方案，这些页面都需要处理相同的业务逻辑；而利用WMRouter，只需要开发好UriInterceptor并配置到各个页面即可。

4. 多工程、组件化、平台化开发。多工程开发要求各个工程之间能互相通信，也可能遇到和外卖App类似的代码复用、依赖注入、编译等问题，这些问题都可以利用WMRouter的URI分发和ServiceLoader模块解决。

5. 对业务埋点需求较强的场景。页面跳转作为最常见的业务逻辑之一，常常需要埋点。给每个页面配置好URI，使用WMRouter统一进行页面跳转，并在全局的OnCompleteListener中埋点即可。

6. 对App可用性要求较高的场景。一方面，可以对页面跳转失败进行埋点监控上报，及时发现线上问题；另一方面，页面跳转时可以执行判断逻辑，发现异常（例如服务端异常、客户端崩溃等）则自动打开降级后的页面，保证关键功能的正常工作，或给用户友好的提示。

6. 页面A/B测试、动态配置等场景。在WMRouter提供的接口基础上进行少量开发配置，就可以实现：根据下发的A/B测试策略跳转不同的页面实现；根据不同的需要动态下发一组路由表，相同的URI跳转到不同的一组页面（实现方面可以自定义UriInterceptor，对匹配的URI返回301的UriResult使跳转重定向）。


## 设计与使用文档

[设计与使用文档](docs/user-manual.md)


## 发展背景

关于WMRouter的发展背景和过程，可参考美团技术博客 [WMRouter：美团外卖Android开源路由框架](https://tech.meituan.com/meituan_waimai_android_open_source_routing_framework.html)。


## 更新日志

[更新日志](CHANGELOG.md)


## 使用了WMRouter的项目

<table>
  <!-- 注意格式：每行7个图标，即每个tr中7个td元素 -->
  <tr>
    <td align="center"><a href="https://waimai.meituan.com/"><img src="docs/logo/meituanwaimai.png" width="100px;" alt=""/><br /><sub><b>美团外卖</b></sub></a></td>
    <td align="center"><a href="https://kd.meituan.com/"><img src="docs/logo/meituanwaimaibusiness.png" width="100px;" alt=""/><br /><sub><b>美团外卖商家版</b></sub></a></td>
    <td align="center"><a href="https://www.meituan.com/"><img src="docs/logo/meituan.png" width="100px;" alt=""/><br /><sub><b>美团</b></sub></a></td>
    <td align="center"><a href="https://www.dianping.com/"><img src="docs/logo/dianping.png" width="100px;" alt=""/><br /><sub><b>大众点评</b></sub></a></td>
  </tr>
</table>

> 欢迎补充，并创建PullRequest。


## 贡献者 / Contributors ✨

<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-14-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

感谢所有参与贡献的人员：

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="http://www.paincker.com/"><img src="https://avatars.githubusercontent.com/u/2093721?v=4?s=100" width="100px;" alt=""/><br /><sub><b>江子健</b></sub></a><br /><a href="https://github.com/meituan/WMRouter/commits?author=jzj1993" title="Code">💻</a> <a href="https://github.com/meituan/WMRouter/commits?author=jzj1993" title="Documentation">📖</a> <a href="#ideas-jzj1993" title="Ideas, Planning, & Feedback">🤔</a> <a href="#blog-jzj1993" title="Blogposts">📝</a> <a href="#maintenance-jzj1993" title="Maintenance">🚧</a></td>
    <td align="center"><a href="https://s.joker.li/"><img src="https://avatars.githubusercontent.com/u/2122432?v=4?s=100" width="100px;" alt=""/><br /><sub><b>李少杰</b></sub></a><br /><a href="#ideas-mimers" title="Ideas, Planning, & Feedback">🤔</a> <a href="https://github.com/meituan/WMRouter/commits?author=mimers" title="Code">💻</a> <a href="https://github.com/meituan/WMRouter/pulls?q=is%3Apr+reviewed-by%3Amimers" title="Reviewed Pull Requests">👀</a> <a href="#maintenance-mimers" title="Maintenance">🚧</a></td>
    <td align="center"><a href="https://github.com/yuanboGeng"><img src="https://avatars.githubusercontent.com/u/13827644?v=4?s=100" width="100px;" alt=""/><br /><sub><b>yuanboGeng</b></sub></a><br /><a href="https://github.com/meituan/WMRouter/commits?author=yuanboGeng" title="Code">💻</a> <a href="#ideas-yuanboGeng" title="Ideas, Planning, & Feedback">🤔</a></td>
    <td align="center"><a href="https://github.com/laberat"><img src="https://avatars.githubusercontent.com/u/3307213?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Laberat Yi</b></sub></a><br /><a href="#ideas-laberat" title="Ideas, Planning, & Feedback">🤔</a></td>
    <td align="center"><a href="https://github.com/zhengjinguang"><img src="https://avatars.githubusercontent.com/u/9986394?v=4?s=100" width="100px;" alt=""/><br /><sub><b>郑金光</b></sub></a><br /><a href="https://github.com/meituan/WMRouter/issues?q=author%3Azhengjinguang" title="Bug reports">🐛</a> <a href="https://github.com/meituan/WMRouter/pulls?q=is%3Apr+reviewed-by%3Azhengjinguang" title="Reviewed Pull Requests">👀</a> <a href="#maintenance-zhengjinguang" title="Maintenance">🚧</a></td>
    <td align="center"><a href="https://juejin.im/user/5ccf8b8c6fb9a031f525d89f/posts"><img src="https://avatars.githubusercontent.com/u/5960467?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Ultra-Dejavu</b></sub></a><br /><a href="https://github.com/meituan/WMRouter/commits?author=Leifzhang" title="Code">💻</a></td>
    <td align="center"><a href="https://jeremyliao.github.io/"><img src="https://avatars.githubusercontent.com/u/23290617?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Jeremy Liao</b></sub></a><br /><a href="https://github.com/meituan/WMRouter/commits?author=JeremyLiao" title="Code">💻</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/kiminozo"><img src="https://avatars.githubusercontent.com/u/562132?v=4?s=100" width="100px;" alt=""/><br /><sub><b>kiminozo</b></sub></a><br /><a href="https://github.com/meituan/WMRouter/commits?author=kiminozo" title="Code">💻</a> <a href="https://github.com/meituan/WMRouter/issues?q=author%3Akiminozo" title="Bug reports">🐛</a></td>
    <td align="center"><a href="https://github.com/zjiecode"><img src="https://avatars.githubusercontent.com/u/38341983?v=4?s=100" width="100px;" alt=""/><br /><sub><b>zjiecode</b></sub></a><br /><a href="https://github.com/meituan/WMRouter/issues?q=author%3Azjiecode" title="Bug reports">🐛</a> <a href="#maintenance-zjiecode" title="Maintenance">🚧</a></td>
    <td align="center"><a href="https://github.com/hibate"><img src="https://avatars.githubusercontent.com/u/33896942?v=4?s=100" width="100px;" alt=""/><br /><sub><b>hibate</b></sub></a><br /><a href="https://github.com/meituan/WMRouter/issues?q=author%3Ahibate" title="Bug reports">🐛</a></td>
    <td align="center"><a href="https://github.com/Knight-ZXW"><img src="https://avatars.githubusercontent.com/u/11730925?v=4?s=100" width="100px;" alt=""/><br /><sub><b>卓修武</b></sub></a><br /><a href="https://github.com/meituan/WMRouter/issues?q=author%3AKnight-ZXW" title="Bug reports">🐛</a> <a href="https://github.com/meituan/WMRouter/commits?author=Knight-ZXW" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/zhaoya188"><img src="https://avatars.githubusercontent.com/u/9677761?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Vali</b></sub></a><br /><a href="https://github.com/meituan/WMRouter/commits?author=zhaoya188" title="Documentation">📖</a></td>
    <td align="center"><a href="https://fuckcode.xyz/"><img src="https://avatars.githubusercontent.com/u/8597839?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Caij</b></sub></a><br /><a href="https://github.com/meituan/WMRouter/issues?q=author%3ACaij" title="Bug reports">🐛</a></td>
    <td align="center"><a href="https://github.com/Yellow5A5"><img src="https://avatars.githubusercontent.com/u/12532305?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Acropolis</b></sub></a><br /><a href="https://github.com/meituan/WMRouter/issues?q=author%3AYellow5A5" title="Bug reports">🐛</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

> - 如有遗漏和需要补充的，可按照 [all-contributors](https://allcontributors.org/docs/en/cli/overview) 文档自行添加，并创建Pull Request。
> - 参与贡献者可加微信jzj2015，进入 WMRouter Contributors 微信群学习交流。
