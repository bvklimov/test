package com.spb.kbv.sampleskbv;

import com.spb.kbv.sampleskbv.rxbus.RxBus;
import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

public class App extends BaseApp {
    @Override
    public void onCreate() {
        super.onCreate();
        initComponent();
        Timber.plant(new Timber.DebugTree());
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            refWatcher = LeakCanary.install(this);
        }
        rxBus = new RxBus();
    }
}
