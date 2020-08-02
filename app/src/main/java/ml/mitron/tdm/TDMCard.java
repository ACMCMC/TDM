package ml.mitron.tdm;

import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.tech.Ndef;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

public class TDMCard {

    static final String CARD_HOST_ID = BuildConfig.APPLICATION_ID;
    private static final String TAG = TDMCard.class.getName();
    private static final String CARD_SCHEME_ID = "card";

    private static final String CARD_FIELD_TYPE_ID = "card_type";
    private static final String CARD_FIELD_ID = "card_id";
    private static final int CARD_FIELD_ID_LENGTH = 16;
    private static final String CARD_FIELD_HOLDER_NAME_ID = "card_holder";
    private static final String CARD_FIELD_BALANCE_ID = "card_balance";

    private CardNumber cardNumber;

    public String getHiddenCardNumber() {
        String hiddenNumber = new String();
        byte[] cardByteArray = cardNumber.getByteArray();
        for (int i = 0; i < 4; i++) {
            hiddenNumber = hiddenNumber.concat(Integer.toString((int) cardByteArray[i]));
        }
        hiddenNumber = hiddenNumber.concat(" ····");

        return hiddenNumber;
    }

    private CARD_TYPE cardType;
    private String cardHolderName;
    private Float balance;

    TDMCard(CARD_TYPE cardType, Long cardNumber, String cardHolderName, Float balance) {
        if (String.valueOf(cardNumber).length() == CARD_FIELD_ID_LENGTH) {
            this.cardNumber = new CardNumber(cardNumber);
        } else {
            throw new IllegalArgumentException("El largo del número de la tarjeta no es el adecuado. Debería ser de " + CARD_FIELD_ID_LENGTH + " bytes.");
        }
        this.cardType = cardType;
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
            Log.d(TAG, "Error de lectura");
        } catch (FormatException e) {

        }

        NdefRecord[] records;

        try {
            records = message.getRecords();
        } catch (NullPointerException e) {
            if (!ndefTag.isConnected()) {
                Log.e(TAG, "La tarjeta no está conectada");
                throw new NullPointerException("Lectura de la tarjeta incompleta");
            } else {
                throw e;
            }
        }

        //VAMOS A COMPROBAR SI ESTA ES UNA TARJETA DE TDM

        Iterator<NdefRecord> iterator = Arrays.asList(records).iterator();

        NdefRecord record;

        record = iterator.next();

        if ((record.getTnf() == NdefRecord.TNF_WELL_KNOWN) && (Arrays.equals(record.getType(), NdefRecord.RTD_URI))) {
            Log.d(TAG, "El primer registro del mensaje 1 contiene una URI");
            if ((record.toUri().getScheme().equals(CARD_SCHEME_ID)) && (record.toUri().getHost().equals(CARD_HOST_ID))) {
                Log.d(TAG, "Esta parece ser una tarjeta de transporte TDM");
                if (records.length == 5) {
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

        record = iterator.next();

        if ((record.getTnf() == NdefRecord.TNF_EXTERNAL_TYPE) && (new String(record.getType()).equals(CARD_HOST_ID + ":" + CARD_FIELD_TYPE_ID))) {
            cardType = CARD_TYPE.of(record.getPayload()[0]);
        } else {
            throw new IllegalArgumentException("El segundo registro no está formateado adecuadamente");
        }

        record = iterator.next();

        if ((record.getTnf() == NdefRecord.TNF_EXTERNAL_TYPE) && (new String(record.getType()).equals(CARD_HOST_ID + ":" + CARD_FIELD_ID)) && (record.getPayload().length == CARD_FIELD_ID_LENGTH)) {
            cardNumber = new CardNumber(record.getPayload());
        } else {
            throw new IllegalArgumentException("El tercer registro no está formateado adecuadamente");
        }

        record = iterator.next();

        if ((record.getTnf() == NdefRecord.TNF_EXTERNAL_TYPE) && (new String(record.getType()).equals(CARD_HOST_ID + ":" + CARD_FIELD_HOLDER_NAME_ID))) {
            cardHolderName = new String(record.getPayload());
        } else {
            throw new IllegalArgumentException("El cuarto registro no está formateado adecuadamente");
        }

        record = iterator.next();

        if ((record.getTnf() == NdefRecord.TNF_EXTERNAL_TYPE) && (new String(record.getType()).equals(CARD_HOST_ID + ":" + CARD_FIELD_BALANCE_ID))) {
            ByteArrayInputStream byteArray = new ByteArrayInputStream(record.getPayload());
            balance = Float.intBitsToFloat(byteArray.read());

        } else {
            throw new IllegalArgumentException("El quinto registro no está formateado adecuadamente");
        }

    }

    public TDMCard.CardNumber getCardNumber() {
        return cardNumber;
    }

    static class CardNumber {

        //Implementación con un Long. Antes estaba con un array de bytes.

        private Long number;

        CardNumber(Long number) {
            this.number = number;
        }

        CardNumber(byte[] numberList) {
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.put(numberList);
            buffer.flip();
            number = buffer.getLong();
        }

        byte[] getByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.putLong(number);
            return buffer.array();
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof CardNumber) {
                return this.number.equals(((CardNumber) obj).number);
            }
            return false;
        }

    /*static class CardNumber {

        private List<Byte> numberList;

        CardNumber(byte[] numberList) {
            this.numberList = new ArrayList<>();
            for (byte numero : numberList) {
                this.numberList.add(new Byte(numero));
            }
        }

        byte[] getByteArray() {
            byte[] numberByteArray = new byte[numberList.size()];
            for (int i = 0; i < numberList.size(); i++) {
                numberByteArray[i] = numberList.get(i);
            }
            return numberByteArray;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj.getClass() == TDMCard.CardNumber.class) {
                return Arrays.deepEquals(Arrays.asList(this.getByteArray()).toArray(), Arrays.asList(((TDMCard.CardNumber) obj).getByteArray()).toArray());
            } else {
                return super.equals(obj);
            }
        }

        @Override
        public int hashCode() {
            return ByteBuffer.wrap(getByteArray()).getInt();
        }
    */
    }

        public String getCardHolderName() {
            return cardHolderName;
        }

        public Float getBalance() {
            return balance;
        }

        public void writeToCard(Ndef ndefTag) {
            NdefRecord[] records = new NdefRecord[5];

            records[0] = NdefRecord.createUri("card://" + CARD_HOST_ID);
            records[1] = NdefRecord.createExternal(TDMCard.CARD_HOST_ID, CARD_FIELD_TYPE_ID, new byte[]{0});
            records[2] = NdefRecord.createExternal(TDMCard.CARD_HOST_ID, CARD_FIELD_ID, cardNumber.getByteArray());
            records[3] = NdefRecord.createExternal(TDMCard.CARD_HOST_ID, CARD_FIELD_HOLDER_NAME_ID, cardHolderName.getBytes());

            ByteArrayOutputStream balanceArray = new ByteArrayOutputStream();
            balanceArray.write(Float.floatToIntBits(balance));

            records[4] = NdefRecord.createExternal(TDMCard.CARD_HOST_ID, CARD_FIELD_BALANCE_ID, balanceArray.toByteArray());

            NdefMessage message = new NdefMessage(records);

            try {
                ndefTag.connect();
                ndefTag.writeNdefMessage(message);
                ndefTag.close();
            } catch (IOException e) {

            } catch (FormatException e) {

            }
        }

        VectorDrawableCompat getIconoCardDrawable(Context context) {
            VectorDrawableCompat mVector;
            switch (cardType.typeId) {
                case 1:
                default:
                    mVector = VectorDrawableCompat.create(context.getResources(), R.drawable.ic_card_element, null);
                    break;
                case 2:
                    mVector = VectorDrawableCompat.create(context.getResources(), R.drawable.ic_card_infinity, null);
                    break;
            }
            return mVector;

        }

        enum CARD_TYPE {
            STANDARD(0),
            ELEMENT(1),
            INFINITY(2),
            DISCOUNT(3);

            private static final Map<Integer, CARD_TYPE> map = new HashMap<>();

            static {
                for (CARD_TYPE type : CARD_TYPE.values()) {
                    map.put(type.getCardTypeId(), type);
                }
            }

            private final int typeId;

            CARD_TYPE(int i) {
                typeId = i;
            }

            static CARD_TYPE of(int typeId) {
                return map.get(typeId);
            }

            int getCardTypeId() {
                return typeId;
            }
        }
    }

