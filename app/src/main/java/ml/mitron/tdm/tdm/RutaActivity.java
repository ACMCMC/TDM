package ml.mitron.tdm.tdm;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static android.content.ContentValues.TAG;
import static ml.mitron.tdm.tdm.R.layout.estacion;

/**
 * Created by acmc on 11/07/2018.
 */

public class RutaActivity extends AppCompatActivity {

    private Ruta ruta;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;

    private DBExtractor extractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent incomingIntent = getIntent();
        Integer inicio = incomingIntent.getIntExtra("inicio", 1);
        Integer destino = incomingIntent.getIntExtra("destino", 1);

        extractor = DBExtractor.getExtractor(this);

        setContentView(R.layout.ruta);

        //setRuta(inicio, destino, this, extractor);

        Button boton = (Button) findViewById(R.id.nuevoCalculoButton);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });


        //OBTENEMOS LA RUTA
        if (!extractor.isOpen()) {
            extractor.OpenDB();
        }

        try {
            ruta = Busqueda.Busqueda(inicio, destino, extractor);
        } catch (IllegalArgumentException e) {
            Intent intent = new Intent(this, ErrorActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        extractor.CloseDB();


        //CREAMOS EL RECYCLERVIEW
        recyclerView = (RecyclerView) findViewById(R.id.layoutRuta);

        // use a linear layout manager
        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);

        // specify an adapter (see also next example)
        recyclerAdapter = new EstacionAdapter(ruta,this);
        recyclerView.setAdapter(recyclerAdapter);


        /*for (int i = 0; i < recyclerView.getChildCount(); i++) {
            final View view = recyclerView.getChildAt(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RutaActivity.this, EstacionActivity.class);
                    TextView textViewEstacion = ((TextView) ((LinearLayout) ((LinearLayout) v).getChildAt(0)).getChildAt(1));
                    textViewEstacion.setTransitionName("nombreEstacion");
                    intent.putExtra("nombreEstacion", textViewEstacion.getText());
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RutaActivity.this, textViewEstacion, "nombreEstacion");
                    startActivity(intent, options.toBundle());
                }
            });
        }*/
    }

    void setRuta(Integer inicio, Integer destino, Context contexto, DBExtractor extractor) {

        if (!extractor.isOpen()) {
            extractor.OpenDB();
        }

        Log.d(TAG, "setRuta: Iniciada la búsqueda de rutas");

        try {
            ruta = Busqueda.Busqueda(inicio, destino, extractor);
        } catch (IllegalArgumentException e) {
            Intent intent = new Intent(contexto, ErrorActivity.class);
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
        Integer segundos = ruta.getTiempo();
        //añadimos el 7% al tiempo de viaje estimado
        segundos = Double.valueOf(segundos.doubleValue() * 1.07).intValue();
        Integer minutos = segundos / 60;
        segundos = segundos % 60;
        texto.setText(texto.getText().toString() + ": " + minutos.toString() + " min " + segundos.toString() + " s");

        for (Estacion estacion : ruta.getEstacionesRuta()) {

            //El layoutInstruccion incluye el texto de la instrucción; y el nombre de la estación con el icono.

            LinearLayout layoutInstruccion = new LinearLayout(contexto);
            layoutInstruccion.setOrientation(LinearLayout.VERTICAL);

            LinearLayout layoutEstacion = new LinearLayout(contexto);
            layoutEstacion.setOrientation(LinearLayout.HORIZONTAL);

            layoutInstruccion.addView(layoutEstacion);
            layoutRuta.addView(layoutInstruccion);
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
                    } catch (NoSuchElementException | Resources.NotFoundException e) {
                        textoLinea.setText(getResources().getText(R.string.tomarLinea) + " " + linea.getNombre());
                    }
                    parametros = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    parametros.gravity = Gravity.CENTER_VERTICAL;
                    parametros.leftMargin = (int) getResources().getDimension(R.dimen.marginLineaLeft);
                    parametros.topMargin = (int) getResources().getDimension(R.dimen.marginLineaTop);
                    parametros.bottomMargin = (int) getResources().getDimension(R.dimen.marginLineaBottom);
                    textoLinea.setLayoutParams(parametros);
                    layoutInstruccion.addView(textoLinea);
                } else {

                }
            }

        }
    }
}

class EstacionAdapter extends RecyclerView.Adapter<EstacionAdapter.HolderEstacion> {

    private Ruta ruta;
    private Context contexto;

    public EstacionAdapter(Ruta ruta,Context contexto) {
        this.ruta = ruta;
        this.contexto = contexto;
    }

    @Override
    public HolderEstacion onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contexto).inflate(R.layout.fila_estacion,parent,false);
        HolderEstacion holder = new HolderEstacion(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(HolderEstacion holder, int position) {
        Log.d(TAG, "onBindViewHolder: creado.");


        //ponemos el icono
        if (position == 0) {
            holder.icono.setImageResource(R.drawable.ic_adjust_black_24dp);
        } else if (position == getItemCount() - 1) {
            holder.icono.setImageResource(R.drawable.ic_place_black_24dp);
        } else {
            holder.icono.setImageResource(R.drawable.ic_estacion);
        }

        //ponemos el nombre de la estación
        holder.nombre.setText(ruta.getEstacionesRuta().get(position).getNombre());

        //ponemos la instrucción, si hace falta.
        seccionLinea checkInstruccion = null;

        for (seccionLinea linea : ruta.getLineas()) {
            if (linea.checkInicioLinea(ruta.getEstacionesRuta().get(position))) {
                checkInstruccion = linea;
                break;
            }
        }

        if (checkInstruccion != null) {
            try {
                holder.instruccion.setText(contexto.getResources().getText(R.string.tomarLinea) + " " + checkInstruccion.getNombre() + " / " + checkInstruccion.getNombrePropio(contexto));
            } catch (NoSuchElementException | Resources.NotFoundException e) {
                holder.instruccion.setText(contexto.getResources().getText(R.string.tomarLinea) + " " + checkInstruccion.getNombre());
            }
        } else  {
            holder.instruccion.setVisibility(View.GONE);
        }

        //ponemos un OnClickListener
        holder.layoutEstacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EstacionActivity.class);
                TextView textViewEstacion = (TextView) ((RelativeLayout) v).getChildAt(1);
                textViewEstacion.setTransitionName("nombreEstacion");
                intent.putExtra("nombreEstacion", textViewEstacion.getText());
                intent.putExtra("textSize", textViewEstacion.getTextSize());

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext(), textViewEstacion, "nombreEstacion");
                v.getContext().startActivity(intent, options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return ruta.getEstacionesRuta().size();
    }

    static class HolderEstacion extends RecyclerView.ViewHolder {

        private ImageView icono;
        private TextView nombre;
        private TextView instruccion;
        private RelativeLayout layoutEstacion;

        public HolderEstacion(View itemView) {
            super(itemView);

            icono = (ImageView) itemView.findViewById(R.id.iconoEstacion);
            nombre = (TextView) itemView.findViewById(R.id.nombreEstacion);
            instruccion = (TextView) itemView.findViewById(R.id.instruccionRuta);
            layoutEstacion = (RelativeLayout) itemView.findViewById(R.id.layoutFilaEstacion);
        }
    }

}
