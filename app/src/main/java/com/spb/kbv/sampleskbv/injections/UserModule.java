package com.spb.kbv.sampleskbv.injections;

import android.support.annotation.NonNull;

import com.spb.kbv.sampleskbv.rxbus.RxBus;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
class UserModule {
    @UserScope
    @Provides
    @Named("localBus")
    @NonNull
    RxBus provideLocalBus() {
        return new RxBus();
    }
}
