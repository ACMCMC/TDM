package ml.mitron.tdm.tdm;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.*;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                }
            });
        }


        //tdmCard.setLayoutParams(new ViewGroup.LayoutParams(tdmCard.getMeasuredWidth(),tdmCard.getMeasuredWidth()*53/85));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.d(TAG, "DESCUBIERTO " + tag.toString());

        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        List<NdefMessage> messages = new ArrayList<>();

        for (Parcelable parcelable : rawMessages) {
            messages.add((NdefMessage) parcelable);
        }

        CardView tdmCard = (CardView) findViewById(R.id.tdm_card);

        Animator animationRise = AnimatorInflater.loadAnimator(this, R.animator.incoming_animator);
        animationRise.setTarget(tdmCard);
        animationRise.start();

        /*ObjectAnimator animatorRise = ObjectAnimator.ofFloat(tdmCard, View.TRANSLATION_Z, tdmCard.getTranslationZ(), 64);
        animatorRise.setDuration(2000);
        animatorRise.setRepeatCount(1);
        animatorRise.setRepeatMode(ValueAnimator.REVERSE);
        animatorRise.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorRise.start();*/
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

    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            /*
             * See NFC forum specification for "Text Record Type Definition" at 3.2.1
             *
             * http://www.nfc-forum.org/specs/
             *
             * bit_7 defines encoding
             * bit_6 reserved for future use, must be 0
             * bit_5..0 length of IANA language code
             */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }
    }
}
