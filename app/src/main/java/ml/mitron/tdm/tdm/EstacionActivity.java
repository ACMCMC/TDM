package ml.mitron.tdm.tdm;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import static ml.mitron.tdm.tdm.R.string.C;

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
            LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutConexion.setPadding(
                    0,
                    (int) getResources().getDimension(R.dimen.marginLineaVertical),
                    0,
                    (int) getResources().getDimension(R.dimen.marginLineaVertical));
            layoutConexion.setLayoutParams(parametros);

            for (String linea : conexion.getLineas()) {
                TextView idLinea = new TextView(this);

                parametros = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                //parametros.width = (int) getResources().getDimension(R.dimen.paddingLinea);
                parametros.setMargins(
                        (int) getResources().getDimension(R.dimen.marginLineaHorizontal),
                        0,
                        (int) getResources().getDimension(R.dimen.marginLineaHorizontal),
                        0);

                idLinea.setLayoutParams(parametros);
                idLinea.setPadding(
                        (int) getResources().getDimension(R.dimen.paddingLineaHorizontal),
                        (int) getResources().getDimension(R.dimen.paddingLineaVertical),
                        (int) getResources().getDimension(R.dimen.paddingLineaHorizontal),
                        (int) getResources().getDimension(R.dimen.paddingLineaVertical));

                idLinea.setBackgroundColor(Color.parseColor("#000000"));
                idLinea.setTextColor(Color.parseColor("#FFFFFF"));

                idLinea.setText((CharSequence) linea);

                //ponemos el texto en negrita
                idLinea.setTypeface(Typeface.DEFAULT_BOLD);

                layoutConexion.addView(idLinea);
            }

            TextView idConexion = new TextView(this);
            idConexion.setText(getResources().getText(R.string.conexionCon) + " " + extractor.getEstacion(conexion.getIDDestino()).getNombre());

            layoutConexion.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutConexion.addView(idConexion);

            contenedor.addView(layoutConexion);
        }

    }

}
