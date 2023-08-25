package com.example.qr;

import android.support.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPageAdapter extends FragmentStateAdapter {
    public ViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new storeFragment();
            case 1: return new homeFragment();
            case 2: return new analysisFragment();
            default: return new homeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
