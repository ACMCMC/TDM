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
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class User {

    private static User user;
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
                mDatabase = FirebaseDatabase.getInstance().getReference("usuarios").child(authResult.getUser().getUid());
                DatabaseReference ownershipReference = FirebaseDatabase.getInstance().getReference("ownership").child(authResult.getUser().getUid());

                ChildEventListener ownershipChildEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Log.v("TRIAL", dataSnapshot.toString());
                        DatabaseReference cardReference = FirebaseDatabase.getInstance().getReference("tarjetas_tdm").child(String.valueOf(dataSnapshot.getKey()));
                        if (dataSnapshot.getValue(Boolean.class)) {
                            cardReference.addValueEventListener(new TDMCardValueEventListener());
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

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

    private TDMCard parseTDMCard(DataSnapshot tarjetaSnapshot) {
        byte[] cardNumber = new byte[16];
        for (int i = 0; i < 16; i++) {
            cardNumber[i] = ((Long) tarjetaSnapshot.child("numero").child(Integer.toString(i)).getValue(Long.class)).byteValue();
        }
        return new TDMCard(TDMCard.CARD_TYPE.of(tarjetaSnapshot.child("tipo").getValue(Long.class).intValue()), cardNumber, nombre, tarjetaSnapshot.child("balance").getValue(Float.class));
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

    void registerTDMCard(TDMCard tdmCard) {

    }

    private class TDMCardValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            tarjetas.put(parseTDMCard(dataSnapshot).getCardNumber(), parseTDMCard(dataSnapshot));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }

    boolean poseeTarjetaTDM(TDMCard tarjeta) {
        return tarjetas.containsKey(tarjeta.getCardNumber());
    }
}
