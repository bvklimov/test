package com.spb.kbv.sampleskbv.ui.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.transition.TransitionSet;
import android.view.View;

import com.hannesdorfmann.mosby3.mvp.MvpNullObjectBasePresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.spb.kbv.sampleskbv.ui.BaseActivity;
import com.spb.kbv.sampleskbv.ui.fragments.BaseFragment;
import com.spb.kbv.sampleskbv.ui.fragments.FragmentsFactory;

import timber.log.Timber;

public abstract class FragActivity<V extends MvpView, P extends MvpNullObjectBasePresenter<V>>
        extends BaseActivity<V, P> {
    private final static String MAIN_FRAG_TAG = "MAIN_FRAG_TAG";

    protected BaseFragment activeFragment;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (activeFragment != null) {
            outState.putString(MAIN_FRAG_TAG, activeFragment.getClass().getName());
        }
        super.onSaveInstanceState(outState);
    }

    public void switchToFragment(Class<? extends BaseFragment> fragmentClass,
            int position,
            Bundle data,
            View sharedElement,
            String sharedAnchor,
            TransitionSet transitionSet) {
        String       tag = fragmentClass.getName();
        BaseFragment fragment;
        try {
            fragment = (data != null) ?
                       FragmentsFactory.buildFragment(this, tag, data) :
                       FragmentsFactory.buildFragment(this, tag);
            if (transitionSet != null) {
                fragment.setSharedElementEnterTransition(transitionSet);
            }

        } catch (IllegalAccessException |
                InstantiationException |
                ClassNotFoundException e) {
            Timber.e(e);
            return;
        }
        changeFragment(fragment, tag, position, sharedElement, sharedAnchor, transitionSet, true);
    }

    public void switchToFragment(Class<? extends BaseFragment> fragmentClass,
            int position,
            boolean newInstance,
            View sharedElement,
            String sharedAnchor,
            TransitionSet transitionSet) {
        if (newInstance) {
            switchToFragment(fragmentClass, position, null, sharedElement, sharedAnchor, transitionSet);
        }
        String   tag      = fragmentClass.getName();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            switchToFragment(fragmentClass, position, null, sharedElement, sharedAnchor, transitionSet);
        } else {
            changeFragment((BaseFragment) fragment, tag, position, sharedElement, sharedAnchor, transitionSet, false);
        }
    }

    public void switchToFragment(Class<? extends BaseFragment> fragmentClass,
            int position,
            View sharedElement,
            String sharedAnchor) {
        switchToFragment(fragmentClass, position, false, sharedElement, sharedAnchor, null);
    }

    public void switchToFragment(Class<? extends BaseFragment> fragmentClass,
            int position,
            View sharedElement,
            String sharedAnchor,
            TransitionSet transitionSet) {
        switchToFragment(fragmentClass, position, false, sharedElement, sharedAnchor, transitionSet);
    }

    public void switchToFragment(Class<? extends BaseFragment> fragmentClass, int position) {
        this.switchToFragment(fragmentClass, position, null, null);
    }

    public void switchToFragment(Class<? extends BaseFragment> fragmentClass,
            int position,
            boolean newInstance) {
        this.switchToFragment(fragmentClass, position, newInstance, null, null, null);
    }

    public void switchToFragment(Class<? extends BaseFragment> fragmentClass,
            int position,
            Bundle data) {
        this.switchToFragment(fragmentClass, position, data, null, null, null);
    }

    protected void restoreFragmentState(Bundle savedInstanceState, int position,
            Class<? extends BaseFragment> defaultFragmentClass) {
        if (!savedInstanceState.containsKey(MAIN_FRAG_TAG)) {
            switchToFragment(defaultFragmentClass, position);
            return;
        }
        String   tag      = savedInstanceState.getString(MAIN_FRAG_TAG);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null || !(fragment instanceof BaseFragment)) {
            switchToFragment(defaultFragmentClass, position);
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(position, fragment);
        transaction.commit();
        activeFragment = (BaseFragment) fragment;
    }

    private void clearFragments(FragmentManager fragmentManager) {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
            fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    private void changeFragment(BaseFragment fragment,
            String tag,
            int position,
            View sharedElement,
            String sharedAnchor,
            TransitionSet transitionSet,
            boolean addToBackStack) {
        FragmentManager     fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction     = fragmentManager.beginTransaction();
        if (sharedElement != null && sharedAnchor != null) {
            transaction.addSharedElement(sharedElement, sharedAnchor);
        }
        if (transitionSet != null) {
            fragment.setSharedElementEnterTransition(transitionSet);
        }
     /*   if (fragment instanceof DrawerFragment) {
            clearFragments(fragmentManager);
            transaction.add(position, fragment, null);
        } else {*/
            transaction.replace(position, fragment, tag);
//        }
        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commitAllowingStateLoss();
        activeFragment = fragment;
    }

    @Override
    public void backAction() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count <= 1) {
            super.backAction();
        } else {
            FragmentManager.BackStackEntry entry = getSupportFragmentManager()
                    .getBackStackEntryAt(count - 2);
            String   tag      = entry.getName();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment != null && activeFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove(activeFragment);
                transaction.commit();
            }
            getSupportFragmentManager().popBackStack();

            if (fragment != null && fragment instanceof BaseFragment) {
                activeFragment = (BaseFragment) fragment;
            }
        }
    }

    public void doubleBackAction() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count <= 2) {
            super.backAction();
        } else {
            FragmentManager.BackStackEntry previousEntry = getSupportFragmentManager()
                    .getBackStackEntryAt(count - 2);
            String previousTag = previousEntry.getName();
            Fragment previousFragment = getSupportFragmentManager().findFragmentByTag(previousTag);


            FragmentManager.BackStackEntry entry = getSupportFragmentManager()
                    .getBackStackEntryAt(count - 3);
            String tag = entry.getName();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);

            if (fragment != null && activeFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove(activeFragment);
                transaction.remove(previousFragment);
                transaction.commit();
            }
            getSupportFragmentManager().popBackStack(previousTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            if (fragment != null && fragment instanceof BaseFragment) {
                activeFragment = (BaseFragment) fragment;
            }
        }
    }

    public void backToFirst() {
        if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
            FragmentManager.BackStackEntry first = getSupportFragmentManager().getBackStackEntryAt(1);
            getSupportFragmentManager().popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void showSnackBar(int stringId) {
        Snackbar.make(findViewById(android.R.id.content),
                stringId, Snackbar.LENGTH_SHORT).show();
    }

    public void showSnackBar(int stringId, int actionStringId, View.OnClickListener onClickListener) {
        Snackbar.make(findViewById(android.R.id.content),
                getResources().getString(stringId), Snackbar.LENGTH_LONG)
                .setAction(actionStringId, onClickListener)
                .show();
    }

}

