package lk.javainstitute.mybookings;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new AttractionFragment();
            case 1: return new EatDrinkFragment();
            case 2: return new TransportFragment();
            default: return new AttractionFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

