package ml.mitron.tdm.tdm;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;

public class TDMCardActivity extends AppCompatActivity {

    private static final String TAG = TDMCardActivity.class.getName();

    NfcAdapter adaptador;
    IntentFilter[] filters;
    String[][] techListsArray;

    PendingIntent pendingIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_tdm_card_activity);

        adaptador = NfcAdapter.getDefaultAdapter(this);

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndefFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try {
            ndefFilter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException(e);
        }

        filters = new IntentFilter[] {
                ndefFilter,
        };

        techListsArray = new String[][] { new String[] { IsoDep.class.getName() } };

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

    @Override
    protected void onNewIntent (Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.d(TAG, "TECNOLOGIAS: " + tag.getTechList()[0]);

        CardView tdmCard = (CardView) findViewById(R.id.tdm_card);

        ObjectAnimator animatorRise = ObjectAnimator.ofFloat(tdmCard, View.TRANSLATION_Z,tdmCard.getTranslationZ(),64);
        animatorRise.setDuration(2000);
        animatorRise.setRepeatCount(1);
        animatorRise.setRepeatMode(ValueAnimator.REVERSE);
        animatorRise.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorRise.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        adaptador.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        adaptador.enableForegroundDispatch(this, pendingIntent, filters, techListsArray);

    }
}
