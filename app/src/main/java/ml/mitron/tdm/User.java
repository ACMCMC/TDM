package ml.mitron.tdm;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class User {
    private static final String TAG = User.class.getName();

    private static User user;
    private static Set<DataSetChangeListener> dataSetChangeListeners;
    private static Map<String, DatabaseReference> TDMCardDatabaseReferences;
    private static Map<DatabaseReference, ValueEventListener> TDMCardValueEventListenersMap;
    private static Map<String, TDMCard.CardNumber> TDMDatabaseKeyAndCardNumberMap;
    /*Esto lo necesitamos porque en la base de datos de Firebase, la referencia de una tarjeta (p. ej, /tarjetas/34),
    no tiene por qué ser su número. La tarjeta 34 podría tener el número 72391934712, o lo que sea,
    y la referencia en sí no se guarda en el Map de las tarjetas (solo se guarda el número de la tarjeta en sí),
    por eso necesitamos una forma de saber qué tarjeta se corresponde a qué referencia, y eso es lo que guardamos aquí.*/

    static {
        dataSetChangeListeners = new HashSet<>();
    }

    static {
        TDMCardDatabaseReferences = new HashMap<>();
    }

    static {
        TDMCardValueEventListenersMap = new HashMap<>();
    }

    static {
        TDMDatabaseKeyAndCardNumberMap = new HashMap<>();
    }

    public String nombre;
    private Map<TDMCard.CardNumber, TDMCard> tarjetas;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private User() {
        tarjetas = new HashMap<>();
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.v(TAG, "Usuario: " + authResult.getUser().getUid());
                mDatabase = FirebaseDatabase.getInstance().getReference("usuarios").child(authResult.getUser().getUid());
                DatabaseReference ownershipReference = FirebaseDatabase.getInstance().getReference("ownership").child(authResult.getUser().getUid());

                ChildEventListener ownershipChildEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        DatabaseReference cardReference = FirebaseDatabase.getInstance().getReference("tarjetas_tdm").child(String.valueOf(dataSnapshot.getKey()));
                        if (dataSnapshot.getValue(Boolean.class)) {
                            TDMCardValueEventListener tdmCardValueEventListener = new TDMCardValueEventListener();
                            cardReference.addValueEventListener(tdmCardValueEventListener);
                            TDMCardValueEventListenersMap.put(cardReference, tdmCardValueEventListener);
                            TDMCardDatabaseReferences.put(cardReference.getKey(), cardReference);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        DatabaseReference cardReference = FirebaseDatabase.getInstance().getReference("tarjetas_tdm").child(String.valueOf(dataSnapshot.getKey()));
                        String ref = dataSnapshot.getRef().getKey();
                        if (dataSnapshot.getValue(Boolean.class)) {
                            TDMCardValueEventListener tdmCardValueEventListener = new TDMCardValueEventListener();
                            cardReference.addValueEventListener(tdmCardValueEventListener);
                            TDMCardValueEventListenersMap.put(cardReference, tdmCardValueEventListener);
                            TDMCardDatabaseReferences.put(ref, cardReference);
                        } else if (TDMCardDatabaseReferences.containsKey(cardReference.getKey())) {
                            TDMCardDatabaseReferences.get(ref).removeEventListener(TDMCardValueEventListenersMap.get(TDMCardDatabaseReferences.get(ref)));
                            tarjetas.remove(TDMDatabaseKeyAndCardNumberMap.get(ref));
                            TDMDatabaseKeyAndCardNumberMap.remove(ref);
                            TDMCardValueEventListenersMap.remove(TDMCardDatabaseReferences.get(ref));
                            TDMCardDatabaseReferences.remove(ref);
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        String ref = dataSnapshot.getRef().getKey();
                        if (TDMCardDatabaseReferences.containsKey(ref)) {
                            TDMCardDatabaseReferences.get(ref).removeEventListener(TDMCardValueEventListenersMap.get(TDMCardDatabaseReferences.get(ref)));
                            tarjetas.remove(TDMDatabaseKeyAndCardNumberMap.get(ref));
                            TDMDatabaseKeyAndCardNumberMap.remove(ref);
                            TDMCardValueEventListenersMap.remove(TDMCardDatabaseReferences.get(ref));
                            TDMCardDatabaseReferences.remove(ref);
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };

                ownershipReference.orderByKey().addChildEventListener(ownershipChildEventListener);
            }
        });


    }

    @Deprecated
    private User(Map<TDMCard.CardNumber, TDMCard> listTarjetasTDM) {
        this();

        this.tarjetas = listTarjetasTDM;
    }

    static User getUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    static void addDataSetChangeListener(DataSetChangeListener listener) {
        dataSetChangeListeners.add(listener);
    }

    private TDMCard parseTDMCard(DataSnapshot tarjetaSnapshot) {
        return new TDMCard(TDMCard.CARD_TYPE.of(tarjetaSnapshot.child("tipo").getValue(Long.class).intValue()), (Long) tarjetaSnapshot.child("numero").getValue(Long.class), nombre, tarjetaSnapshot.child("balance").getValue(Float.class));
    }

    void registerTDMCard(TDMCard tdmCard) {

    }

    private void notifyDataSetChanged() {
        for (DataSetChangeListener dataSetChangeListener :
                dataSetChangeListeners) {
            dataSetChangeListener.onDataSetChange();
        }
    }

    public Map<TDMCard.CardNumber, TDMCard> getTarjetas() {
        return tarjetas;
    }

    boolean poseeTarjetaTDM(TDMCard tarjeta) {
        return tarjetas.containsKey(tarjeta.getCardNumber());
    }

    interface DataSetChangeListener {
        void onDataSetChange();
    }

    private class TDMCardValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (tarjetas.containsKey(parseTDMCard(dataSnapshot).getCardNumber())) {
                tarjetas.remove(parseTDMCard(dataSnapshot).getCardNumber());
            }
            tarjetas.put(parseTDMCard(dataSnapshot).getCardNumber(), parseTDMCard(dataSnapshot));
            TDMDatabaseKeyAndCardNumberMap.put(dataSnapshot.getKey(), parseTDMCard(dataSnapshot).getCardNumber());
            notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }
}
