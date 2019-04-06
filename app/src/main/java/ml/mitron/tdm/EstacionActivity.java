package ml.mitron.tdm;

import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.transition.Fade;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class EstacionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //activamos la transición
        getWindow().setEnterTransition(new Fade());

        //Obtenemos el nombre de la estación
        CharSequence charSequenceNombre = getIntent().getCharSequenceExtra("nombreEstacion");

        //Obtenemos los detalles de la estación

        SQLDBExtractor extractor = SQLDBExtractor.getExtractor(this);

        Estacion estacion;

        try {
            estacion = extractor.getEstacion(charSequenceNombre.toString());
        } catch (SQLiteException e) {
            extractor.OpenDB();
            estacion = extractor.getEstacion(charSequenceNombre.toString());
        }

        //Ponemos el layout visible
        setContentView(R.layout.estacion);

        //Cambiamos los parámetros del layout
        LinearLayout contenedor = (LinearLayout) findViewById(R.id.contenedor);

        TextView nombreEstacion = (TextView) findViewById(R.id.nombreEstacion);
        nombreEstacion.setText(charSequenceNombre);

        for (Conexion conexion : estacion.getConexiones()) {
            LinearLayout layoutConexion = new LinearLayout(this);
            layoutConexion.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutConexion.setPadding(
                    0,
                    (int) getResources().getDimension(R.dimen.marginLineaVertical),
                    0,
                    (int) getResources().getDimension(R.dimen.marginLineaVertical));
            layoutConexion.setLayoutParams(parametros);

            for (String idLinea : conexion.getLineas()) {

                Linea linea = new Linea(idLinea, this);

                TextView textViewIDLinea = new TextView(this);

                parametros = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                //parametros.width = (int) getResources().getDimension(R.dimen.paddingLinea);
                parametros.setMargins(
                        0,
                        //(int) getResources().getDimension(R.dimen.marginLineaHorizontal),
                        0,
                        (int) getResources().getDimension(R.dimen.marginLineaHorizontal),
                        0);

                textViewIDLinea.setLayoutParams(parametros);
                textViewIDLinea.setPadding(
                        (int) getResources().getDimension(R.dimen.paddingLineaHorizontal),
                        (int) getResources().getDimension(R.dimen.paddingLineaVertical),
                        (int) getResources().getDimension(R.dimen.paddingLineaHorizontal),
                        (int) getResources().getDimension(R.dimen.paddingLineaVertical));

                textViewIDLinea.setBackgroundColor(linea.getColor());
                textViewIDLinea.setTextColor(Color.parseColor("#FFFFFF"));

                textViewIDLinea.setText((CharSequence) idLinea);

                //ponemos el texto en negrita
                textViewIDLinea.setTypeface(Typeface.DEFAULT_BOLD);

                layoutConexion.addView(textViewIDLinea);
            }

            TextView idConexion = new TextView(this);
            idConexion.setText(getResources().getText(R.string.conexionCon) + " " + extractor.getEstacion(conexion.getIDDestino()).getNombre());

            layoutConexion.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutConexion.addView(idConexion);

            contenedor.addView(layoutConexion);
        }

    }

}
