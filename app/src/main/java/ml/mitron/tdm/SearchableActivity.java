package ml.mitron.tdm;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

/**
 * Created by acmc on 20/08/2018.
 */

public class SearchableActivity extends AppCompatActivity {

    private String searchQuery;
    private SQLDBExtractor extractor;
    private ListView listaEstaciones;
    private SearchAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        Intent intent = getIntent();

        extractor = SQLDBExtractor.getExtractor(this);

        extractor.OpenDB();

        final EditText searchField = (EditText) findViewById(R.id.search);


        //LA SIGUIENTE SECCIÓN DE CÓDIGO PONDRÍA EL NOMBRE DE LA ESTACIÓN SI YA ESTABA SELECCIONADA.
        //PERO LO HE DESAHILITADO, PORQUE ¿PARA QUÉ LO QUEREMOS?

        /*if (intent.getStringExtra("searchValue") != null) {
            if (!intent.getStringExtra("searchValue").contentEquals(getResources().getString(R.string.estacionOrigen)) && !intent.getStringExtra("searchValue").contentEquals(getResources().getString(R.string.estacionDestino))) {
                searchField.setText(intent.getStringExtra("searchValue"));
                searchField.setSelection(searchField.getText().length());
            }
        }*/

        searchQuery = searchField.getText().toString();

        adapter = new SearchAdapter(extractor.searchEstacion(searchQuery), this);

        listaEstaciones = (ListView) findViewById(R.id.listaEstaciones);
        listaEstaciones.setAdapter(adapter);

        searchField.requestFocus();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //searchField.setText(searchQuery);

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString();

                adapter = new SearchAdapter(extractor.searchEstacion(searchQuery), SearchableActivity.this);

                listaEstaciones = (ListView) findViewById(R.id.listaEstaciones);
                listaEstaciones.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchQuery = intent.getStringExtra(SearchManager.QUERY);

            adapter = new SearchAdapter(extractor.searchEstacion(searchQuery), this);

            listaEstaciones = (ListView) findViewById(R.id.listaEstaciones);
            listaEstaciones.setAdapter(adapter);

            searchField.setText(searchQuery);
        }
    }
}

class SearchAdapter extends BaseAdapter {

    private List<Estacion> estaciones;
    private Context contexto;

    SearchAdapter(List<Estacion> estaciones, Context contexto) {
        super();
        this.estaciones = estaciones;
        this.contexto = contexto;
    }

    @Override
    public int getCount() {
        return estaciones.size();
    }

    @Override
    public Object getItem(int position) {
        return estaciones.get(position);
    }

    @Override
    public long getItemId(int position) {
        return estaciones.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ResultadoViewHolder viewHolder = new ResultadoViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(contexto).inflate(R.layout.fila_busqueda, parent, false);
        }
        viewHolder.nombreEstacion = (TextView) convertView.findViewById(R.id.nombreEstacion);
        //viewHolder.descripcionTexto = (TextView) convertView.findViewById(R.id.descripcionTexto);

        viewHolder.nombreEstacion.setText(estaciones.get(position).getNombre());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchField = (EditText) ((Activity) v.getContext()).findViewById(R.id.search);

                searchField.setText(viewHolder.nombreEstacion.getText());

                Intent resultado = new Intent();
                resultado.putExtra("seleccion", viewHolder.nombreEstacion.getText());
                resultado.putExtra("seleccionInicio", ((Activity) v.getContext()).getIntent().getBooleanExtra("seleccionInicio", false));
                //((Activity) v.getContext()).setResult(MainActivity.SEARCH_REQUEST_CODE,resultado);
                ((Activity) v.getContext()).setResult(Activity.RESULT_OK, resultado);
                ((EditText) ((Activity) v.getContext()).findViewById(R.id.search)).setText(viewHolder.nombreEstacion.getText());
                ((Activity) v.getContext()).finishAfterTransition();
            }
        });

        return convertView;
    }

    static class ResultadoViewHolder {
        TextView nombreEstacion;
        RelativeLayout parentLayout;
        TextView descripcionTexto;
        ImageView icono;
        int position;
    }
}