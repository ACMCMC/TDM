package ml.mitron.tdm.tdm;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static ml.mitron.tdm.tdm.R.id.textView;
import static ml.mitron.tdm.tdm.R.layout.ruta;

public class MainActivity extends AppCompatActivity {

    public Context contexto;
    public DBExtractor extractor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        contexto = getApplicationContext();

        DBReaderHelper myDbHelper = new DBReaderHelper(contexto);

        try {

            myDbHelper.createDataBase(contexto);

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        myDbHelper.close();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Spinner spinner = (Spinner) findViewById(R.id.spinnerOrigen);


        //vamos a obtener los nombres de las estaciones

        int count;
        try {
            extractor = new DBExtractor(contexto);
        } catch (Exception e) {
            count = 0;
        }
        try {
            count = extractor.GetEstacionCount();
        } catch (SQLiteException e) {
            extractor.CloseDB();
            count = 0;
        }
        ;
        String[] nombres = new String[count];
        for (int i = 1; i <= count && count != 0; i++) {
            nombres[i - 1] = (extractor.GetEstacion(i).getNombre());
        }
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nombres));

        Spinner spinner2 = (Spinner) findViewById(R.id.spinnerDestino);
        spinner2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nombres));

        //TextView valueTV = new TextView(this);

        Button botonBusqueda = (Button) findViewById(R.id.botonBusqueda);
        botonBusqueda.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!extractor.isOpen()) {
                    extractor.OpenDB();
                }

                Integer inicio = Integer.valueOf(0);
                Integer destino = Integer.valueOf(0);
                final Spinner spinnerOrigen = (Spinner) findViewById(R.id.spinnerOrigen);
                final Spinner spinnerDestino = (Spinner) findViewById(R.id.spinnerDestino);

                for (int i = extractor.GetEstacionCount(); i > 0; i--) {
                    if (spinnerOrigen.getSelectedItem().toString().equals(extractor.GetEstacion((Integer) i).getNombre())) {
                        inicio = i;
                        break;
                    }
                }

                for (int i = extractor.GetEstacionCount(); i > 0; i--) {
                    if (spinnerDestino.getSelectedItem().toString().equals(extractor.GetEstacion((Integer) i).getNombre())) {
                        destino = i;
                        break;
                    }
                }

                extractor.CloseDB();

                RutaActivity rutaActivity = new RutaActivity();

                Intent intent = new Intent(contexto,RutaActivity.class);

                intent.putExtra("inicio",inicio);
                intent.putExtra("destino",destino);

                startActivity(intent);
            }
        });
    }

}
