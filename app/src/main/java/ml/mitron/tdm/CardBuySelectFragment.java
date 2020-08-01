package ml.mitron.tdm;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
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

    private static final String TAG = CardBuySelectFragment.class.getName();

    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    public CardBuySelectFragment() {
        // Required empty public constructor
    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(),
                v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getWidth(),
                v.getHeight());
        v.draw(c);
        return b;
    }

    @Override
    public void onPause() {
        getView().setBackground(new BitmapDrawable(getResources(), loadBitmapFromView(getView())));
        super.onPause();
    }

    @Override
    public void onResume() {
        getView().setBackground(null);
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_card_buy_select, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager_buy_select);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        /*viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                Intent intent = new Intent(getActivity().getBaseContext(), CardBuyActivity.class);
                intent.putExtra("CARD_TYPE", viewPager.getCurrentItem());
                startActivity(intent);
            }
        });*/

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
