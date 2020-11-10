package fr.yncrea.m1_s1project_android.fragments;

import androidx.fragment.app.Fragment;

public interface FragmentSwitcher {
    /**
     * Trigger a navigation to the specified fragment, optionally adding a transaction to the back
     * stack to make this navigation reversible.
     */
    void loadFragment(Fragment fragment, boolean addToBackstack);
}
