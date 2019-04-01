package ml.mitron.tdm;

import android.app.Application;

import com.google.firebase.FirebaseApp;

import java.io.IOException;

/**
 * Created by acmc on 21/08/2018.
 */

public class MyApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        /*

        ESTO ES PARA LA BASE DE DATOS SQLITE. PERO AHORA VAMOS A USAR REALTIME DATABASE DE FIREBASE.

*/

        DBReaderHelper myDbHelper = new DBReaderHelper(getApplicationContext());

        try {

            myDbHelper.createDataBase(getApplicationContext());

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        myDbHelper.close();
    }
}