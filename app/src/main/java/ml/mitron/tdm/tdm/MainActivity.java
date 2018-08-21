package ml.mitron.tdm.tdm;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.transition.Scene;
import android.transition.Transition;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.database.sqlite.SQLiteException;
import android.widget.TextView;

import java.io.IOException;

import static android.icu.lang.UCharacter.JoiningGroup.E;
import static ml.mitron.tdm.tdm.R.id.searchDestino;
import static ml.mitron.tdm.tdm.R.id.searchOrigen;


public class MainActivity extends AppCompatActivity {

    static final int SEARCH_REQUEST_CODE = 1;


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

        TextView searchOrigen = (TextView) findViewById(R.id.searchOrigen);
        TextView searchDestino = (TextView) findViewById(R.id.searchDestino);

        LinearLayout seleccionDestino = (LinearLayout) findViewById(R.id.seleccionDestino);
        seleccionDestino.setVisibility(View.GONE);

        //SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //searchOrigen.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchDestino.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchOrigen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),SearchableActivity.class);
                intent.putExtra("seleccionInicio",true);
                TextView searchField = (TextView) v;
                v.setTransitionName("searchField");
                intent.putExtra("searchValue",searchField.getText());
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext(), searchField, "searchField");
                ((Activity) v.getContext()).startActivityForResult(intent,SEARCH_REQUEST_CODE,options.toBundle());
            }
        });

        searchDestino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),SearchableActivity.class);
                intent.putExtra("seleccionInicio",false);
                TextView searchField = (TextView) v;
                v.setTransitionName("searchField");
                intent.putExtra("searchValue",searchField.getText());
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext(), searchField, "searchField");
                ((Activity) v.getContext()).startActivityForResult(intent,SEARCH_REQUEST_CODE,options.toBundle());
            }
        });


        Button botonBusqueda = (Button) findViewById(R.id.botonBusqueda);
        botonBusqueda.setVisibility(View.GONE);

        botonBusqueda.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (extractor == null) {
                    extractor = new DBExtractor(MainActivity.this);
                }

                if (!extractor.isOpen()) {
                    extractor.OpenDB();
                }

                Integer inicio = Integer.valueOf(0);
                Integer destino = Integer.valueOf(0);
                final TextView searchOrigen = (TextView) findViewById(R.id.searchOrigen);
                final TextView searchDestino = (TextView) findViewById(R.id.searchDestino);

                for (int i = extractor.getEstacionCount(); i > 0; i--) {
                    if (searchOrigen.getText().toString().equals(extractor.getEstacion((Integer) i).getNombre())) {
                        inicio = i;
                        break;
                    }
                }

                for (int i = extractor.getEstacionCount(); i > 0; i--) {
                    if (searchDestino.getText().toString().equals(extractor.getEstacion((Integer) i).getNombre())) {
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

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                if(data.getBooleanExtra("seleccionInicio",false)) {
                    ((TextView) findViewById(R.id.searchOrigen)).setText(data.getStringExtra("seleccion"));
                    if (findViewById(R.id.seleccionDestino).getVisibility() == View.GONE) {
                        findViewById(R.id.seleccionDestino).setVisibility(View.VISIBLE);
                    }
                } else {
                    ((TextView) findViewById(R.id.searchDestino)).setText(data.getStringExtra("seleccion"));
                    if(findViewById(R.id.botonBusqueda).getVisibility() == View.GONE) {
                        findViewById(R.id.botonBusqueda).setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

}
