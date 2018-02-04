package com.spb.kbv.sampleskbv;

import android.app.Application;
import android.content.Context;

import com.spb.kbv.sampleskbv.injections.DaggerUserComponent;
import com.spb.kbv.sampleskbv.injections.UserComponent;
import com.spb.kbv.sampleskbv.rxbus.RxBus;
import com.squareup.leakcanary.RefWatcher;

public class BaseApp extends Application {
    protected RxBus         rxBus;
    private   AppComponent  appComponent;
    private   UserComponent userComponent;
    RefWatcher refWatcher;

    protected void initComponent() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public UserComponent getUserComponent() {
        if (userComponent == null) {
            userComponent = DaggerUserComponent.builder()
                    .appComponent(appComponent)
                    .build();
        }
        return userComponent;
    }

    public void resetActivityComponent() {
        userComponent = null;
    }

    public RxBus getRxBus() {
        return rxBus;
    }

    public static RefWatcher getRefWatcher(Context context) {
        BaseApp application = (BaseApp) context.getApplicationContext();
        return application.refWatcher;
    }
}
