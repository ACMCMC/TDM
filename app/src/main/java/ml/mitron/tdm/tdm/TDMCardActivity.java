package ml.mitron.tdm.tdm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.util.logging.Logger;

public class TDMCardActivity extends AppCompatActivity {

    private static final String TAG = "TDMCardActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_tdm_card);

        final CardView tdmCard = (CardView) findViewById(R.id.tdm_card);

        tdmCard.setTransitionName("card");

        ViewTreeObserver viewTreeObserver = tdmCard.getViewTreeObserver();

        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    tdmCard.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    Log.d(TAG, "Width de la tarjeta: " + Integer.valueOf(tdmCard.getMeasuredWidth()).toString());
                    Log.d(TAG, "Height de la tarjeta: " + Integer.valueOf(tdmCard.getMeasuredWidth() * 53 / 85).toString());
                    tdmCard.setLayoutParams(new CardView.LayoutParams(tdmCard.getLayoutParams().width, tdmCard.getMeasuredWidth() * 53 / 85));
                }
            });
        }


        //tdmCard.setLayoutParams(new ViewGroup.LayoutParams(tdmCard.getMeasuredWidth(),tdmCard.getMeasuredWidth()*53/85));
    }
}
