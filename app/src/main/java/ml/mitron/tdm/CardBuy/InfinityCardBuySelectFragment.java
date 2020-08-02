package ml.mitron.tdm.CardBuy;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ml.mitron.tdm.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfinityCardBuySelectFragment extends androidx.fragment.app.Fragment {


    public InfinityCardBuySelectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_infinity_card_buy_select, container, false);
    }

}
