package ml.mitron.tdm.tdm;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SearchView;
import android.transition.Scene;
import android.transition.Transition;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.database.sqlite.SQLiteException;
import java.io.IOException;

import static ml.mitron.tdm.tdm.R.id.searchDestino;

public class MainActivity extends AppCompatActivity {

    Context contexto;
    DBExtractor extractor;

    private Scene escena1, escena2;

    private Transition transicion;


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

        //TextView valueTV = new TextView(this);

        SearchView searchOrigen = (SearchView) findViewById(R.id.searchOrigen);
        SearchView searchDestino = (SearchView) findViewById(R.id.searchDestino);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchOrigen.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchDestino.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchOrigen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),SearchableActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext(), (SearchView) ((Activity) v.getContext()).findViewById(R.id.searchOrigen), "searchField");
                v.getContext().startActivity(intent,options.toBundle());
            }
        });


        Button botonBusqueda = (Button) findViewById(R.id.botonBusqueda);
        botonBusqueda.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!extractor.isOpen()) {
                    extractor.OpenDB();
                }

                Integer inicio = Integer.valueOf(0);
                Integer destino = Integer.valueOf(0);
                final SearchView searchOrigen = (SearchView) findViewById(R.id.searchOrigen);
                final SearchView searchDestino = (SearchView) findViewById(R.id.searchDestino);

                for (int i = extractor.getEstacionCount(); i > 0; i--) {
                    if (searchOrigen.getQuery().toString().equals(extractor.getEstacion((Integer) i).getNombre())) {
                        inicio = i;
                        break;
                    }
                }

                for (int i = extractor.getEstacionCount(); i > 0; i--) {
                    if (searchDestino.getQuery().toString().equals(extractor.getEstacion((Integer) i).getNombre())) {
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
