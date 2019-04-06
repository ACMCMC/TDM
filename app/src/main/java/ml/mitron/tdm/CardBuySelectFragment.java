package ml.mitron.tdm;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardBuySelectFragment extends androidx.fragment.app.Fragment {

    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    public CardBuySelectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_card_buy_select, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager_buy_select);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        return view;
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return (new ElementCardBuySelectFragment());
                case 1:
                    return (new InfinityCardBuySelectFragment());
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
