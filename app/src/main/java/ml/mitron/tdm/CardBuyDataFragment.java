package ml.mitron.tdm;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardBuyDataFragment extends androidx.fragment.app.Fragment {

    TextInputEditText name;
    TextInputEditText surname;
    TextInputEditText balance;

    public CardBuyDataFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_card_buy_data, container, false);

        name = view.findViewById(R.id.nameEditText);
        surname = view.findViewById(R.id.surnameEditText);
        balance = view.findViewById(R.id.balanceEditText);

        return view;
    }

}
