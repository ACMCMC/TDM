package ml.mitron.tdm;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.devs.vectorchildfinder.VectorChildFinder;

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


        //CAMBIAMOS EL COLOR DE LAS PARTES DEL ICONO
        VectorChildFinder iconoCard = new VectorChildFinder(this, R.drawable.ic_card_infinity, (ImageView) findViewById(R.id.ic_card));

        iconoCard.findPathByName("shadow").setFillColor(Color.RED);
        iconoCard.findPathByName("foreground").setFillColor(Color.BLUE);

        findViewById(R.id.ic_card).invalidate();


        adaptador = NfcAdapter.getDefaultAdapter(this);

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndefFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try {
            ndefFilter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException(e);
        }

        filters = new IntentFilter[]{
                ndefFilter,
        };

        techListsArray = new String[][]{new String[]{IsoDep.class.getName()}};

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
                    findViewById(R.id.frameLayout).invalidate();
                }
            });
        }


        if (getIntent().getAction() != null) {
            if (getIntent().getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
                onNewIntent(getIntent());
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.v(TAG, "DESCUBIERTO " + tag.toString());

        //

        Ndef ndefTag = Ndef.get(tag);

        TDMCard tarjeta = null;
        try {
            tarjeta = new TDMCard(ndefTag);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "La tarjeta no es válida.");
            startActivityForResult(new Intent(this, TDMCardErrorActivity.class), 1);
            tarjeta = new TDMCard(TDMCard.CARD_TYPE.STANDARD, Long.valueOf(0), "Try again", (float) 0);
        } catch (NullPointerException e) {
            tarjeta = new TDMCard(TDMCard.CARD_TYPE.STANDARD, Long.valueOf(0), "?", (float) 0);
            Toast.makeText(this, "Lectura incompleta", Toast.LENGTH_SHORT).show();
        }

        if (User.getUser().poseeTarjetaTDM(tarjeta)) {
            ((TextView) findViewById(R.id.nombreCard)).setText("EXISTING " + tarjeta.getCardHolderName());
        }

        ((TextView) findViewById(R.id.numeroCard)).setText(tarjeta.getHiddenCardNumber());


        //

        tarjeta = new TDMCard(TDMCard.CARD_TYPE.STANDARD, Long.valueOf(0), tarjeta.getCardHolderName(), (float) tarjeta.getBalance());
        tarjeta.writeToCard(ndefTag);
        //

        CardView tdmCard = (CardView) findViewById(R.id.tdm_card);

        Animator animationRise = AnimatorInflater.loadAnimator(this, R.animator.incoming_animator);
        animationRise.setTarget(tdmCard);
        animationRise.start();

        ConstraintLayout balanceTextView = (ConstraintLayout) findViewById(R.id.constraintLayoutCardData);
        TextView nfcPrompt = (TextView) findViewById(R.id.nfcPrompt);
        ImageView nfcIcon = (ImageView) findViewById(R.id.nfcIcon);

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(nfcPrompt, View.ALPHA, 0);
        animator1.setDuration(500);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(nfcIcon, View.ALPHA, 0);
        animator2.setDuration(500);

        ObjectAnimator animator3 = ObjectAnimator.ofFloat(balanceTextView, View.ALPHA, 1);
        animator3.setDuration(500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animator1).with(animator2).before(animator3);
        animatorSet.start();
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
