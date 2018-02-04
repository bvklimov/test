package com.spb.kbv.sampleskbv;

import android.content.Context;
import android.support.annotation.NonNull;

import com.spb.kbv.sampleskbv.rxbus.RxBus;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private final BaseApp app;

    public AppModule(BaseApp app) {
        this.app = app;
    }

    @Provides
    @Singleton
    @NonNull
    Context provideContext() {
        return app.getBaseContext();
    }

    @Provides
    @Singleton
    @Named("globalBus")
    @NonNull
    RxBus provideRxBus() {
        return app.getRxBus();
    }
}
