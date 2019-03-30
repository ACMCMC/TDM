package ml.mitron.tdm;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.tech.Ndef;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class TDMCard {

    private static final String TAG = TDMCard.class.getName();

    static final String CARD_HOST_ID = BuildConfig.APPLICATION_ID;
    private static final String CARD_SCHEME_ID = "card";

    private static final String CARD_FIELD_ID = "card_id";
    private static final int CARD_FIELD_ID_LENGTH = 16;

    private static final String CARD_HOLDER_NAME_ID = "card_holder";

    private static final String CARD_BALANCE_ID = "card_balance";

    private byte[] cardNumber;
    private String cardHolderName;
    private Float balance;

    TDMCard(byte[] cardNumber, String cardHolderName, Float balance) {
        if (cardNumber.length == CARD_FIELD_ID_LENGTH) {
            this.cardNumber = cardNumber;
        } else {
            throw new IllegalArgumentException("El largo del número de la tarjeta no es el adecuado. Debería ser de " + CARD_FIELD_ID_LENGTH + " bytes.");
        }
        this.cardHolderName = cardHolderName;
        this.balance = balance;
    }

    TDMCard(Ndef ndefTag) {
        NdefMessage message = null;
        try {
            ndefTag.connect();
            message = ndefTag.getNdefMessage();
            //Log.v(TAG, message.toString());
            ndefTag.close();

        } catch (IOException e) {

        } catch (FormatException e) {

        }

        NdefRecord[] records = message.getRecords();

        //VAMOS A COMPROBAR SI ESTA ES UNA TARJETA DE TDM

        if ((records[0].getTnf() == NdefRecord.TNF_WELL_KNOWN) && (Arrays.equals(records[0].getType(), NdefRecord.RTD_URI))) {
            Log.d(TAG, "El primer registro del mensaje 1 contiene una URI");
            if ((records[0].toUri().getScheme().equals(CARD_SCHEME_ID)) && (records[0].toUri().getHost().equals(CARD_HOST_ID))) {
                Log.d(TAG, "Esta parece ser una tarjeta de transporte TDM");
                if (records.length == 4) {
                    Log.d(TAG, "La tarjeta contiene el número de registros correcto");
                } else {
                    throw new IllegalArgumentException("La tarjeta está corrupta; no incluye el número correcto de registros");
                }
            } else {
                throw new IllegalArgumentException("La tarjeta no parece ser una tarjeta de transporte TDM");
            }

        } else {
            throw new IllegalArgumentException("La tarjeta no contiene un URI como primer registro");
        }

        //ESTA DEBERÍA SER UNA TARJETA TDM. VAMOS A LEER LOS DATOS DE LA TARJETA AHORA.

        if ((records[1].getTnf() == NdefRecord.TNF_EXTERNAL_TYPE) && (new String(records[1].getType()).equals(CARD_HOST_ID + ":" + CARD_FIELD_ID)) && (records[1].getPayload().length == CARD_FIELD_ID_LENGTH)) {
            cardNumber = records[1].getPayload();
        } else {
            throw new IllegalArgumentException("El segundo registro no está formateado adecuadamente");
        }

        if ((records[2].getTnf() == NdefRecord.TNF_EXTERNAL_TYPE) && (new String(records[2].getType()).equals(CARD_HOST_ID + ":" + CARD_HOLDER_NAME_ID))) {
            cardHolderName = new String(records[2].getPayload());
        } else {
            throw new IllegalArgumentException("El tercer registro no está formateado adecuadamente");
        }

        if ((records[3].getTnf() == NdefRecord.TNF_EXTERNAL_TYPE) && (new String(records[3].getType()).equals(CARD_HOST_ID + ":" + CARD_BALANCE_ID))) {
            ByteArrayInputStream byteArray = new ByteArrayInputStream(records[3].getPayload());
            balance = Float.intBitsToFloat(byteArray.read());

        } else {
            throw new IllegalArgumentException("El tercer registro no está formateado adecuadamente");
        }

    }

    public byte[] getCardNumber() {
        return cardNumber;
    }

    public String getHiddenCardNumber() {
        String hiddenNumber = new String();
        for (int i = 0; i < 4; i++) {
            hiddenNumber = hiddenNumber.concat(Integer.toString((int) cardNumber[i]));
        }
        hiddenNumber = hiddenNumber.concat(" ····");

        return hiddenNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public Float getBalance() {
        return balance;
    }

    public void writeToCard(Ndef ndefTag) {
        NdefRecord[] records = new NdefRecord[4];

        records[0] = NdefRecord.createUri("card://" + CARD_HOST_ID);
        records[1] = NdefRecord.createExternal(TDMCard.CARD_HOST_ID, CARD_FIELD_ID, cardNumber);
        records[2] = NdefRecord.createExternal(TDMCard.CARD_HOST_ID, CARD_HOLDER_NAME_ID, cardHolderName.getBytes());

        ByteArrayOutputStream balanceArray = new ByteArrayOutputStream();
        balanceArray.write(Float.floatToIntBits(balance));

        records[3] = NdefRecord.createExternal(TDMCard.CARD_HOST_ID, CARD_BALANCE_ID, balanceArray.toByteArray());

        NdefMessage message = new NdefMessage(records);

        try {
            ndefTag.connect();
            ndefTag.writeNdefMessage(message);
            ndefTag.close();
        } catch (IOException e) {

        } catch (FormatException e) {

        }
    }
}
