package ml.mitron.tdm.tdm;

import android.content.Context;
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

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static ml.mitron.tdm.tdm.R.id.textView;

public class MainActivity extends AppCompatActivity {

    public Context contexto;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        contexto = getApplicationContext();

        DBReaderHelper myDbHelper = new DBReaderHelper(contexto);

        try {

            myDbHelper.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        myDbHelper.close();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner spinner = (Spinner) findViewById(R.id.spinnerOrigen);
        String[] letra = {"Seleccionar...", "DA", "IGUAL", ";", "}"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));

        Spinner spinner2 = (Spinner) findViewById(R.id.spinnerDestino);
        spinner2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));

        //TextView valueTV = new TextView(this);

        Button botonBusqueda = (Button) findViewById(R.id.botonBusqueda);
        botonBusqueda.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setContentView(R.layout.ruta);

                setRuta();


            }
        });
    }

    private void setRuta() {

        ArrayList<String> placeEstaciones = Busqueda.Busqueda(2,5);

        TextView texto = (TextView) findViewById(R.id.estacionOrigen);
        texto.setText(placeEstaciones.get(0));
        //colocamos la primera estación

        LinearLayout layoutRuta = (LinearLayout) findViewById(R.id.layoutRuta);
        for (int i = 1; i < (placeEstaciones.size()); i++) {
            LinearLayout estacion = new LinearLayout(this);
            estacion.setOrientation(LinearLayout.HORIZONTAL);
            layoutRuta.addView((View) estacion);
            TextView textView = new TextView(this);
            ImageView icono = new ImageView(this);

            if (i == (placeEstaciones.size() - 1)) {
                icono.setImageResource(R.drawable.ic_place_black_24dp);
            } else {
                icono.setImageResource(R.drawable.ic_estacion);
            }

            icono.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.estacionHeight),(int) getResources().getDimension(R.dimen.estacionWidth)));
            icono.setLayoutParams(new LinearLayout.MarginLayoutParams((ViewGroup.MarginLayoutParams) findViewById(R.id.iconoEstacion).getLayoutParams()));
            /*la de arriba es una solución cutrilla. Solo copia los parámetros de margen del primer icono (iconoEstacion), que ya está definido en el XML.
            Pero no los genera en el propio código, porque no sé cómo. Lo intenté con la línea de abajo, pero da error:*/
            //icono.setLayoutParams(new LinearLayout.MarginLayoutParams((int) getResources().getDimension(R.dimen.marginEstacion),(int) getResources().getDimension(R.dimen.marginEstacion)));
            LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(icono.getLayoutParams());
            parametros.leftMargin = (int) getResources().getDimension(R.dimen.marginEstacion);
            parametros.rightMargin = (int) getResources().getDimension(R.dimen.marginEstacion);
            parametros.topMargin = (int) getResources().getDimension(R.dimen.marginEstacion);
            parametros.bottomMargin = (int) getResources().getDimension(R.dimen.marginEstacion);
            icono.setLayoutParams(parametros);
            textView.setTextAppearance(android.R.style.TextAppearance_Material_Subhead);
            parametros = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            parametros.gravity = Gravity.CENTER_VERTICAL;
            textView.setLayoutParams(parametros);
            textView.setText(placeEstaciones.get(i));
            estacion.addView((View) icono);
            estacion.addView((View) textView);
        }
    }
}
