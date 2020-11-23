package fr.yncrea.m1_s1project_android.fragments;

import androidx.fragment.app.Fragment;

import java.util.Stack;

/**
 * needs to implement onBackPressed()
 */
public interface FragmentSwitcher {

    Stack<Fragment> mFragmentStack = new Stack<>();

    /**
     * Trigger a navigation to the specified fragment, optionally adding a transaction to the back
     * stack to make this navigation reversible.
     */

    void loadFragment(Fragment fragment, boolean addToBackstack);
}
