package ml.mitron.tdm.tdm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner spinner = (Spinner) findViewById(R.id.spinnerOrigen);
        String[] letra = {"Avinguda Picasso Noord","Hautes Montàs","Mitron Centraal","Izbu Centraal HTF","Aeropórt T2"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));

        Spinner spinner2 = (Spinner) findViewById(R.id.spinnerDestino);
        spinner2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));

        Button botonBusqueda = (Button) findViewById(R.id.botonBusqueda);
        botonBusqueda.setOnClickListener(new View.OnClickListener() {public void onClick(View v) {
            setContentView(R.layout.ruta);

            TextView texto = (TextView) findViewById(R.id.estacionOrigen);

            String estaciones = "";

            for (Integer i : Busqueda.Busqueda(1,4)) {
                estaciones = estaciones + i.toString();
            }
            texto.setText((String) estaciones);
        }});
    }
}
