package ml.mitron.tdm.CardBuy;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.spectrum.SpectrumPalette;

import org.jetbrains.annotations.NotNull;

import androidx.fragment.app.Fragment;

import ml.mitron.tdm.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardBuyDataColorSelectFragment extends Fragment {

    SpectrumPalette spectrumPalette;
    int selectedColor;

    public CardBuyDataColorSelectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card_buy_data_color_select, container, false);

        spectrumPalette = view.findViewById(R.id.sprectrum_palette);
        spectrumPalette.setOnColorSelectedListener(new SpectrumPalette.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                selectedColor = color;
                ((CardBuyActivity) getActivity()).buttonNext.setEnabled(true);
            }
        });

        /*ChipGroup chipGroup = ((ChipGroup) ((ConstraintLayout) view.getRootView()).getChildAt(0));

        List<ColorDrawable> colors = new ArrayList<>();

        colors.add(new ColorDrawable(Color.parseColor("#FFFF0000")));
        colors.add(new ColorDrawable(Color.parseColor("#FF0F0000")));
        colors.add(new ColorDrawable(Color.parseColor("#FFFF0F00")));
        colors.add(new ColorDrawable(Color.parseColor("#FFFF00F0")));

        for (ColorDrawable color : colors) {
            Chip chip = new Chip(new ContextThemeWrapper(getContext(), R.style.Chip), null, 0);
            chip.setChipBackgroundColor(ColorStateList.valueOf(color.getColor()));
            chip.setChipCornerRadius(0);
            chip.setMinWidth(chip.getMinHeight());
            chipGroup.addView(chip);
        }*/

        return view;
    }

}
