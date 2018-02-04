package com.spb.kbv.sampleskbv.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hannesdorfmann.mosby3.mvp.MvpNullObjectBasePresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.hannesdorfmann.mosby3.mvp.viewstate.MvpViewStateFragment;
import com.hannesdorfmann.mosby3.mvp.viewstate.RestorableViewState;
import com.spb.kbv.sampleskbv.App;
import com.spb.kbv.sampleskbv.R;
import com.spb.kbv.sampleskbv.injections.UserComponent;
import com.spb.kbv.sampleskbv.presenters.NetworkRequestView;
import com.spb.kbv.sampleskbv.rxbus.RxBus;
import com.spb.kbv.sampleskbv.rxbus.events.NavigationEvents;
import com.spb.kbv.sampleskbv.rxbus.events.RxBusEvent;
import com.spb.kbv.sampleskbv.ui.activities.FragActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseFragment<V extends MvpView,
        P extends MvpNullObjectBasePresenter<V>,
        VS extends RestorableViewState<V>>
        extends MvpViewStateFragment<V, P, VS>
        implements NetworkRequestView {
    private FragActivity  currentActivity;
    private UserComponent appComponent;
    private RxBus         rxBus;
    private Disposable    subscription;

    protected boolean isLoading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appComponent = ((App) getContext().getApplicationContext()).getUserComponent();
        currentActivity = (FragActivity) getActivity();
    }

    protected abstract void backPressedAction();

    protected FragActivity getCurrentActivity() {
        return currentActivity;
    }

    protected UserComponent getComponent() {
        return appComponent;
    }

    protected void setRxBus(RxBus rxBus) {
        this.rxBus = rxBus;
    }

    @Override
    public void onResume() {
        super.onResume();
        subscription = rxBus.toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::localBusAction);
    }

    @Override
    public void onPause() {
        super.onPause();
        subscription.dispose();
        subscription = null;
        hideKeyboard();
    }

    protected void localBusAction(RxBusEvent rxBusEvent) {
        if (rxBusEvent instanceof NavigationEvents.BackPressedEvent) {
            BaseFragment.this.backPressedAction();
        }
    }

    protected void hideKeyboard() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        try {
            InputMethodManager inputManager =
                    (InputMethodManager) getActivity()
                            .getBaseContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
            View focus = getCurrentActivity().getCurrentFocus();
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
    public void showConnectionError() {
        stopLoading();
    }

    @Override
    public void showServerError() {
        stopLoading();
    }

    @Override
    public void showLoading() {
        isLoading = true;
    }

    @Override
    public void showAirplaneError() {
        stopLoading();
        getCurrentActivity().showSnackBar(R.string.ErrorAirplaneMode,
                R.string.ErrorAirplaneModeAction,
                v -> {
                    Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                    startActivity(intent);
                });
    }

    @Override
    public void showUnauthorizedError() {
        stopLoading();
    }

    @Override
    public void showBadRequestError() {
        stopLoading();
    }


    @Override
    public void stopLoading() {
        isLoading = false;
    }
}
