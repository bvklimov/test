package com.spb.kbv.sampleskbv;

import com.spb.kbv.sampleskbv.network.NetworkModule;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {AppModule.class, NetworkModule.class})

@Singleton
public interface AppComponent {
}
