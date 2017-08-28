package ml.mitron.tdm.tdm;

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

import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static ml.mitron.tdm.tdm.R.id.textView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner spinner = (Spinner) findViewById(R.id.spinnerOrigen);
        String[] letra = {"Avinguda Picasso Noord", "Hautes Montàs", "Mitron Centraal", "Izbu Centraal HTF", "Aeropórt T2"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));

        Spinner spinner2 = (Spinner) findViewById(R.id.spinnerDestino);
        spinner2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));

        TextView valueTV = new TextView(this);

        Button botonBusqueda = (Button) findViewById(R.id.botonBusqueda);
        botonBusqueda.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setContentView(R.layout.ruta);

                TextView texto = (TextView) findViewById(R.id.estacionOrigen);

                String estaciones = "";

                setRuta();

                for (Integer i : Busqueda.Busqueda(1, 4)) {
                    estaciones = estaciones + i.toString();
                }
                texto.setText((String) estaciones);
            }
        });
    }

    private void setRuta() {
        String[] textArray = {"Mitron Centraal", "Av. Picasso Noord", "Mitron Zuid", "Mitron Haute"};
        LinearLayout layoutRuta = (LinearLayout) findViewById(R.id.layoutRuta);
        for (int i = 0; i < textArray.length; i++) {
            LinearLayout estacion = new LinearLayout(this);
            estacion.setOrientation(LinearLayout.HORIZONTAL);
            layoutRuta.addView((View) estacion);
            TextView textView = new TextView(this);
            ImageView icono = new ImageView(this);
            icono.setImageResource(R.drawable.ic_adjust_black_24dp);
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
            textView.setText(textArray[i]);
            estacion.addView((View) icono);
            estacion.addView((View) textView);
        }
    }
}
