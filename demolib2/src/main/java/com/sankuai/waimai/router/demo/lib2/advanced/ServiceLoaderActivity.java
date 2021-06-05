package com.sankuai.waimai.router.demo.lib2.advanced;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.TextView;

import com.sankuai.waimai.router.Router;
import com.sankuai.waimai.router.annotation.RouterUri;
import com.sankuai.waimai.router.demo.lib2.BaseActivity;
import com.sankuai.waimai.router.demo.lib2.DemoConstant;
import com.sankuai.waimai.router.demo.lib2.advanced.services.IAccountService;
import com.sankuai.waimai.router.demo.lib2.advanced.services.IFactoryService;
import com.sankuai.waimai.router.demo.lib2.advanced.services.LibraryModule;
import com.sankuai.waimai.router.service.EmptyArgsFactory;
import com.sankuai.waimai.router.service.IFactory;

import java.util.List;

/**
 * Created by jzj on 2018/3/29.
 */
@RouterUri(path = DemoConstant.SERVICE_LOADER)
public class ServiceLoaderActivity extends BaseActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTextView = new TextView(this);
        setContentView(mTextView);

        final StringBuilder s = new StringBuilder();

        // 通过接口（或父类）加载所有实现类
        List<Class<LibraryModule>> classList = Router.getAllServiceClasses(LibraryModule.class);
        s.append(LibraryModule.class.getSimpleName()).append("实现类:").append('\n');
        for (Class<LibraryModule> clazz : classList) {
            s.append(clazz).append('\n');
        }
        s.append('\n');

        // 通过接口（或父类）创建所有实现类的实例
        List<LibraryModule> list = Router.getAllServices(LibraryModule.class);
        s.append(LibraryModule.class.getSimpleName()).append("实例:").append('\n');
        for (LibraryModule service : list) {
            s.append(service).append('\n');
        }
        s.append('\n');

        // 通过path获取指定实现类，可以用来获取其他库中的Fragment、Activity等
        s.append(DemoConstant.TEST_FRAGMENT + "实现类:\n");
        Class<Fragment> fragmentClass = Router.getServiceClass(Fragment.class,
                DemoConstant.TEST_FRAGMENT);
        if (fragmentClass != null) {
            s.append(fragmentClass.getName()).append('\n');
        }
        s.append('\n');

        // 单例获取
        IAccountService accountService1 = Router.getService(IAccountService.class,
                DemoConstant.SINGLETON);
        IAccountService accountService2 = Router.getService(IAccountService.class,
                DemoConstant.SINGLETON);
        s.append(IAccountService.class.getSimpleName()).append("单例:\n");
        s.append("第一次获取: ").append(accountService1.hashCode()).append('\n');
        s.append("第二次获取: ").append(accountService2.hashCode()).append('\n');
        s.append('\n');

        // Factory, Provider

        // EmptyArgsFactory
        IFactoryService service1 = Router.getService(IFactoryService.class, "/factory", EmptyArgsFactory.INSTANCE);
        // Provider
        IFactoryService service2 = Router.getService(IFactoryService.class, "/factory");
        // ContextFactory
        IFactoryService service3 = Router.getService(IFactoryService.class, "/factory", this);
        // CustomFactory
        IFactoryService service4 = Router.getService(IFactoryService.class, "/factory", new IFactory() {
            @NonNull
            @Override
            public <T> T create(@NonNull Class<T> clazz) throws Exception {
                return clazz.getConstructor(String.class).newInstance("CreateByCustomFactory");
            }
        });
        s.append("EmptyArgsFactory: ").append(service1.name()).append('\n');
        s.append("Provider: ").append(service2.name()).append('\n');
        s.append("Context: ").append(service3.name()).append('\n');
        s.append("CustomFactory: ").append(service4.name()).append('\n');
        s.append('\n');

        // 方法调用
        s.append("MethodCall:\n");
        Integer result = Router.callMethod(DemoConstant.ADD_METHOD, 1, 2);
        s.append("1 + 2 = ").append(result).append('\n');
        Integer versionCode = Router.callMethod(DemoConstant.GET_VERSION_CODE);
        s.append("version = ").append(versionCode).append('\n');
        s.append('\n');

        // Kotlin
        Object service = Router.getService(Object.class, DemoConstant.KOTLIN_SERVICE);
        s.append("GetKotlinService:\n");
        s.append(service.toString()).append('\n').append('\n');

        mTextView.setText(s.toString());
    }
}
