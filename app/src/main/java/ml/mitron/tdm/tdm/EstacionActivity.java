package ml.mitron.tdm.tdm;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EstacionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtenemos el nombre de la estación
        CharSequence charSequenceNombre = getIntent().getCharSequenceExtra("nombreEstacion");

        //Obtenemos los detalles de la estación
        DBExtractor extractor = new DBExtractor(this);
        Estacion estacion = extractor.getEstacion(charSequenceNombre.toString());

        //Ponemos el layout visible
        setContentView(R.layout.estacion);

        //Cambiamos los parámetros del layout
        LinearLayout contenedor = (LinearLayout) findViewById(R.id.contenedor);

        TextView nombreEstacion = (TextView) findViewById(R.id.nombreEstacion);
        nombreEstacion.setText(charSequenceNombre);

        for (Conexion conexion : estacion.getConexiones()) {
            LinearLayout layoutConexion = new LinearLayout(this);
            layoutConexion.setOrientation(LinearLayout.HORIZONTAL);

            for (String linea : conexion.getLineas()) {
                TextView idLinea = new TextView(this);

                ViewGroup.LayoutParams parametros = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

                idLinea.setLayoutParams(parametros);
                idLinea.setBackgroundColor(Color.parseColor("#000000"));

                idLinea.setText((CharSequence) linea);

                //ponemos el texto en negrita
                idLinea.setTypeface(Typeface.DEFAULT_BOLD);

                layoutConexion.addView(idLinea);
            }


            contenedor.addView(layoutConexion);
        }

    }

}
