package ml.mitron.tdm;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class User {

    private List<TDMCard> tarjetas;

    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;

    public String nombre;

    User() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously();
        mDatabase = FirebaseDatabase.getInstance().getReference("usuarios").child(mAuth.getUid());
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                switch (key) {
                    case "nombre":
                        nombre = (String) dataSnapshot.getValue();
                        break;
                    case "tarjetas_tdm":
                        parseTDMCardList(dataSnapshot);
                        break;
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
        });
    }

    @Deprecated
    private User(List<TDMCard> listTarjetasTDM) {
        this();

        this.tarjetas = listTarjetasTDM;
    }

    void registerTDMCard(TDMCard tdmCard) {

    }

    private List<TDMCard> parseTDMCardList(DataSnapshot tarjetasKey) {
        List<TDMCard> tdmCardList = new ArrayList<>();

        Iterator cardIterator = tarjetasKey.getChildren().iterator();
        DataSnapshot currentCard;
        while (cardIterator.hasNext()) {
            currentCard = (DataSnapshot) cardIterator.next();
            byte[] cardNumber = new byte[16];
            for (int i = 0; i < 16; i++) {
                cardNumber[i] =  ((Long) currentCard.child("numero").child(Integer.toString(i)).getValue()).byteValue();
            }
            tdmCardList.add(new TDMCard(cardNumber, nombre, ((Long)currentCard.child("saldo").getValue()).floatValue()));
        }
        return tdmCardList;
    }
}
