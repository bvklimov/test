package com.spb.kbv.sampleskbv.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.SimpleArrayMap;

public final class FragmentsFactory {
    private static final SimpleArrayMap<String, Class<?>> FragClassesMap = new SimpleArrayMap<>();

    public static BaseFragment buildFragment(Context context, String fragmentName)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> fragmentClazz = getFragmentClass(context, fragmentName);
        return (BaseFragment) fragmentClazz.newInstance();
    }

    public static BaseFragment buildFragment(Context context, String fragmentName, Bundle data)
            throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        BaseFragment fragment = buildFragment(context, fragmentName);
        if (data != null && !data.isEmpty()) {
            fragment.setArguments(data);
        }
        return fragment;
    }

    public static Class<?> getFragmentClass(Context context, String fragmentName)
            throws ClassNotFoundException {
        Class<?> fragmentClazz = FragClassesMap.get(fragmentName);
        if (fragmentClazz == null) {
            fragmentClazz = context.getClassLoader().loadClass(fragmentName);
            FragClassesMap.put(fragmentName, fragmentClazz);
        }
        return fragmentClazz;
    }
}

