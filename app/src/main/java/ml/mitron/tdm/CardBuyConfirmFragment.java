package ml.mitron.tdm;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardBuyConfirmFragment extends Fragment {

    private TDMCard.CARD_TYPE cardType = TDMCard.CARD_TYPE.STANDARD;

    public CardBuyConfirmFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_card_buy_confirm, container, false);
    }

    void setCardType(TDMCard.CARD_TYPE cardType) {
        this.cardType = cardType;
    }

    @Override
    public void onResume() {
        super.onResume();

        int resId = 0;
        switch (cardType) {
            case ELEMENT:
                resId = R.drawable.ic_card_infinity;
                break;
            case INFINITY:
                resId = R.drawable.ic_card_element;
                break;
            case STANDARD:
                resId = R.drawable.ic_card_element;
                break;
            case DISCOUNT:
                resId = R.drawable.ic_card_discount;
                break;
        }
        ((ImageView) getView().findViewById(R.id.ic_card)).setImageDrawable(VectorDrawableCompat.create(getResources(), resId, null));
    }
}
