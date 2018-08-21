package ml.mitron.tdm.tdm;

import android.app.Application;

import java.io.IOException;

/**
 * Created by acmc on 21/08/2018.
 */

public class MyApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DBReaderHelper myDbHelper = new DBReaderHelper(getApplicationContext());

        try {

            myDbHelper.createDataBase(getApplicationContext());

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        myDbHelper.close();
    }
}