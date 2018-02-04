package com.spb.kbv.sampleskbv.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.spb.kbv.sampleskbv.App;
import com.spb.kbv.sampleskbv.injections.UserComponent;
import com.spb.kbv.sampleskbv.rxbus.RxBus;
import com.spb.kbv.sampleskbv.rxbus.events.NavigationEvents;
import com.spb.kbv.sampleskbv.rxbus.events.PermissionsEvents;
import com.squareup.leakcanary.RefWatcher;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseActivity<V extends MvpView, P extends MvpPresenter<V>>
        extends MvpActivity<V, P> {
    private UserComponent userComponent;
    private App           app;
    private RxBus         rxBus;

    @Override
    protected void onDestroy() {
        RefWatcher refWatcher = App.getRefWatcher(this);
        if (refWatcher != null) {
            refWatcher.watch(this);
        }
        super.onDestroy();
    }

    protected void setRxBus(RxBus rxBus) {
        this.rxBus = rxBus;
    }

    @NonNull
    protected UserComponent getComponent() {
        if (userComponent == null) {
            addComponent();
        }
        return userComponent;
    }

    protected void addComponent() {
        app = (App) getApplication();
        userComponent = app.getUserComponent();
    }

    protected void resetComponent() {
        app.resetActivityComponent();
    }

    public void backAction() {
        hideApp();
    }

    public void hideApp() {
        moveTaskToBack(true);
    }

    protected void hideKeyboard() {
        if (isFinishing()) {
            return;
        }
        try {
            InputMethodManager inputManager =
                    (InputMethodManager) getBaseContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
            View focus = getCurrentFocus();
            if (focus == null) {
                return;
            }
            inputManager.hideSoftInputFromWindow(focus.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (rxBus.hasObservers()) {
            rxBus.send(new NavigationEvents.BackPressedEvent());
        } else {
            hideApp();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (rxBus.hasObservers()) {
            Map<String, Integer> resultsMap = new HashMap<>();
            for (int i = 0; i < permissions.length; i++) {
                resultsMap.put(permissions[i], grantResults[i]);
            }
            rxBus.send(new PermissionsEvents.GrantPermissionEvent(requestCode, resultsMap));
        }
    }

    protected void finishActivity() {
        resetComponent();
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    protected void finishActivity(Bundle bundleToSave) {
        resetComponent();
        Intent intent = new Intent(this, SplashActivity.class);
        if (bundleToSave != null) {
            intent.putExtras(bundleToSave);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    protected void changeActivity(Class<? extends Activity> cls) {
        changeActivity(cls, -1);
    }

    protected void changeActivity(Class<? extends Activity> cls, int flags) {
        Intent intent = new Intent(this, cls);
        if (flags != -1) {
            intent.setFlags(flags);
        }
        startActivity(intent);
    }

    protected void changeActivity(Class<? extends Activity> cls, Bundle extras, int flags) {
        Intent intent = new Intent(this, cls);
        if (extras != null) {
            intent.putExtras(extras);
        }
        if (flags != -1) {
            intent.setFlags(flags);
        }
        startActivity(intent);
    }

    protected void changeActivity(Class<? extends Activity> cls, int flags, Bundle extras, boolean reset) {
        if (reset) {
            resetComponent();
        }
        Intent intent = new Intent(this, cls);
        if (flags != -1) {
            intent.setFlags(flags);
        }
        if (extras != null) {
            intent.putExtras(extras);
        }
        startActivity(intent);
    }

    protected void changeActivityForResult(Class<? extends Activity> cls, int requestCode, int flags, Bundle extras) {
        Intent intent = new Intent(this, cls);
        if (flags != -1) {
            intent.setFlags(flags);
        }
        if (extras != null) {
            intent.putExtras(extras);
        }
        startActivityForResult(intent, requestCode);
    }
}
