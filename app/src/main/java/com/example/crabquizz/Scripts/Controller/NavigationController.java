package com.example.crabquizz.Scripts.Controller;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.crabquizz.R;

public class NavigationController {
    private final FragmentActivity fragmentActivity;
    private final FragmentManager fragmentManager;

    public NavigationController(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
        this.fragmentManager = fragmentActivity.getSupportFragmentManager();
    }

    public void navigateTo(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    public void navigateTo(Fragment fragment) {
        navigateTo(fragment, true);
    }

    public void navigateToWithAnimation(Fragment fragment, boolean addToBackStack,
                                        int enterAnim, int exitAnim) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(enterAnim, exitAnim);
        transaction.replace(R.id.fragment_container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    public void navigateToWithAnimation(Fragment fragment) {
        navigateToWithAnimation(fragment, true,
                android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    public boolean goBack() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            return true;
        }
        return false;
    }

    public void clearBackStack() {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public Fragment getCurrentFragment() {
        return fragmentManager.findFragmentById(R.id.fragment_container);
    }
}
