package ml.mitron.tdm;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TDMCardErrorActivity extends AppCompatActivity {

    NfcAdapter adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tdmcard_error);

        findViewById(R.id.reset_card_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                adaptador = NfcAdapter.getDefaultAdapter(TDMCardErrorActivity.this);

                PendingIntent pendingIntent = PendingIntent.getActivity(TDMCardErrorActivity.this, 0, new Intent(TDMCardErrorActivity.this,TDMCardErrorActivity.this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                IntentFilter ndefFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

                try {
                    ndefFilter.addDataType("*/*");
                } catch (IntentFilter.MalformedMimeTypeException e) {
                    throw new RuntimeException(e);
                }

                IntentFilter[] filters = new IntentFilter[]{
                        ndefFilter,
                };

                String[][] techListsArray = new String[][]{new String[]{IsoDep.class.getName()}};

                adaptador.enableForegroundDispatch(TDMCardErrorActivity.this, pendingIntent, filters, techListsArray);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Ndef ndefTag = Ndef.get(tag);
        TDMCard tarjeta = new TDMCard(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, "Trying", (float) (Math.random() * 100));
        tarjeta.writeToCard(ndefTag);

        finishAfterTransition();
    }
}