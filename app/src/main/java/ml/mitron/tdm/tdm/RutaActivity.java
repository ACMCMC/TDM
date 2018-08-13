package ml.mitron.tdm.tdm;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.NoSuchElementException;

import static ml.mitron.tdm.tdm.R.layout.ruta;

/**
 * Created by acmc on 11/07/2018.
 */

public class RutaActivity extends AppCompatActivity {

    DBExtractor extractor;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Integer inicio = intent.getIntExtra("inicio",1);
        Integer destino = intent.getIntExtra("destino",1);

        extractor = new DBExtractor(this);

        setContentView(ruta);

        setRuta(inicio,destino,this,extractor);

        Button boton = (Button) findViewById(R.id.nuevoCalculoButton);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void setRuta(Integer inicio, Integer destino, Context contexto, DBExtractor extractor) {

        if (!extractor.isOpen()) {
            extractor.OpenDB();
        }

        Ruta ruta;

        try {
            ruta = Busqueda.Busqueda(inicio, destino, extractor);
        } catch (IllegalArgumentException e) {
            Intent intent = new Intent(contexto,ErrorActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        extractor.CloseDB();

        LinearLayout layoutRuta = (LinearLayout) findViewById(R.id.layoutRuta);

        /*View.OnClickListener listenerEstacion = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check if we're running on Android 5.0 or higher
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Apply activity transition
                    view.setTransitionName("nombreEstacion");
                    // inside your activity (if you did not enable transitions in your theme)
                    getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
                    Intent intent = new Intent(, EstacionActivity.class);
                } else {
                    // Swap without transition

                }


            }
        };*/

        //colocamos el tiempo de ruta
        TextView texto = (TextView) findViewById(R.id.tiempoRuta);
        Integer segundos = ruta.tiempo;
        //añadimos el 7% al tiempo de viaje estimado
        segundos = Double.valueOf(segundos.doubleValue() * 1.07).intValue();
        Integer minutos = segundos / 60;
        segundos = segundos % 60;
        texto.setText(texto.getText().toString() + ": " + minutos.toString() + " min " + segundos.toString() + " s");

        for (Estacion estacion : ruta.getEstacionesRuta()) {
            /*if (ruta.getEstacionesRuta().indexOf(estacion) == 0) {


                //colocamos la primera estación
                texto = (TextView) findViewById(R.id.estacionOrigen);
                texto.setText(ruta.getNombresRuta().get(0));

                //colocamos la primera línea
                texto = (TextView) findViewById(R.id.textViewLineas1);
                texto.setText(texto.getText().toString() + " " + ruta.getLineas().get(0).nombre);
                try {
                    texto.setText(getResources().getText(R.string.tomarLinea) + " " + ruta.getLineas().get(0).getNombre() + " / " + ruta.getLineas().get(0).getNombrePropio(contexto));
                } catch (NoSuchElementException e) {
                    texto.setText(getResources().getText(R.string.tomarLinea) + " " + ruta.getLineas().get(0).getNombre());
                }
            } else {*/

                LinearLayout layoutEstacion = new LinearLayout(contexto);
                layoutEstacion.setOrientation(LinearLayout.HORIZONTAL);
                layoutRuta.addView(layoutEstacion);
                LinearLayout.LayoutParams parametros;

                TextView nombreEstacion = new TextView(this);
                nombreEstacion.setText(estacion.getNombre());
                parametros = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                parametros.gravity = Gravity.CENTER_VERTICAL;
                nombreEstacion.setLayoutParams(parametros);
                nombreEstacion.setTextAppearance(android.R.style.TextAppearance_Material_Subhead);


                ImageView icono = new ImageView(this);
                if (ruta.getEstacionesRuta().indexOf(estacion) == ruta.getEstacionesRuta().size() - 1) {
                    icono.setImageResource(R.drawable.ic_place_black_24dp);
                } else if (ruta.getEstacionesRuta().indexOf(estacion) == 0) {
                    icono.setImageResource(R.drawable.ic_adjust_black_24dp);
                } else {
                    icono.setImageResource(R.drawable.ic_estacion);
                }

            //icono.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.estacionHeight), (int) getResources().getDimension(R.dimen.estacionWidth)));
            //icono.setLayoutParams(new LinearLayout.MarginLayoutParams((ViewGroup.MarginLayoutParams) findViewById(R.id.iconoEstacion).getLayoutParams()));
            /*la de arriba es una solución cutrilla. Solo copia los parámetros de margen del primer icono (iconoEstacion), que ya está definido en el XML.
            Pero no los genera en el propio código, porque no sé cómo. Lo intenté con la línea de abajo, pero da error:*/
                //icono.setLayoutParams(new LinearLayout.MarginLayoutParams((int) getResources().getDimension(R.dimen.marginEstacion),(int) getResources().getDimension(R.dimen.marginEstacion)));

            parametros = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.estacionWidth), (int) getResources().getDimension(R.dimen.estacionHeight));
                parametros.leftMargin = (int) getResources().getDimension(R.dimen.marginEstacion);
                parametros.rightMargin = (int) getResources().getDimension(R.dimen.marginEstacion);
                parametros.topMargin = (int) getResources().getDimension(R.dimen.marginEstacion);
                parametros.bottomMargin = (int) getResources().getDimension(R.dimen.marginEstacion);
                icono.setLayoutParams(parametros);

                layoutEstacion.addView(icono);
                layoutEstacion.addView(nombreEstacion);

                //Ponemos el texto de las líneas

                for (seccionLinea linea : ruta.getLineas()) {
                    if (linea.checkInicioLinea(estacion)) {
                        TextView textoLinea = new TextView(this);
                        try {
                            textoLinea.setText(getResources().getText(R.string.tomarLinea) + " " + linea.getNombre() + " / " + linea.getNombrePropio(contexto));
                        } catch (NoSuchElementException e) {
                            textoLinea.setText(getResources().getText(R.string.tomarLinea) + " " + linea.getNombre());
                        }
                        parametros = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        parametros.gravity = Gravity.CENTER_VERTICAL;
                        parametros.leftMargin = (int) getResources().getDimension(R.dimen.marginLineaLeft);
                        parametros.topMargin = (int) getResources().getDimension(R.dimen.marginLineaTop);
                        parametros.bottomMargin = (int) getResources().getDimension(R.dimen.marginLineaBottom);
                        textoLinea.setLayoutParams(parametros);
                        layoutRuta.addView(textoLinea);
                    } else {

                    }
                }
            //}

        }
    }
}
