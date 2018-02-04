package com.spb.kbv.sampleskbv.injections;

import com.spb.kbv.sampleskbv.AppComponent;

import dagger.Component;


@Component(dependencies = AppComponent.class, modules = UserModule.class)
@UserScope
public interface UserComponent {

}
