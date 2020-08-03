package ml.mitron.tdm.CardBuy;


import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import ml.mitron.tdm.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardBuyDataFragment extends androidx.fragment.app.Fragment implements TextWatcher {

    TextInputEditText name;
    TextInputEditText surname;
    TextInputEditText balance;
    TextInputLayout nameLayout;
    TextInputLayout surnameLayout;
    TextInputLayout balanceLayout;

    public CardBuyDataFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card_buy_data, container, false);

        name = view.findViewById(R.id.nameEditText);
        surname = view.findViewById(R.id.surnameEditText);
        balance = view.findViewById(R.id.balanceEditText);
        nameLayout = view.findViewById(R.id.nameTextInputLayout);
        surnameLayout = view.findViewById(R.id.surnameTextInputLayout);
        balanceLayout = view.findViewById(R.id.balanceTextInputLayout);
        balance.addTextChangedListener(this);
        name.addTextChangedListener(this);
        surname.addTextChangedListener(this);

        return view;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

        boolean correcto = true; //checkea si el formulario es correcto en conjunto

        if ((balance.getText().toString().equals("")) || Float.valueOf(balance.getText().toString()) <= 0) {
            balanceLayout.setError(getResources().getText(R.string.debe_ser_mayor_que_cero));
            correcto = false;
        } else {
            balanceLayout.setError(null);
        }
        balanceLayout.invalidate();

        if (name.getText().toString().equals("")) {
            nameLayout.setError(getResources().getText(R.string.introduzca_nombre));
            correcto = false;
        } else {
            nameLayout.setError(null);
        }
        nameLayout.invalidate();

        if (surname.getText().toString().equals("")) {
            surnameLayout.setError(getResources().getText(R.string.introduzca_apellidos));
            correcto = false;
        } else {
            surnameLayout.setError(null);
        }
        surnameLayout.invalidate();


        ((CardBuyActivity) this.getActivity()).buttonNext.setEnabled(correcto);

    }
}
