package ml.mitron.tdm.tdm;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by acmc on 20/08/2018.
 */

public class SearchableActivity extends AppCompatActivity {

    private String searchQuery;
    private DBExtractor extractor;
    private ListView listaEstaciones;
    private SearchAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        Intent intent = getIntent();
        if (intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchQuery = intent.getStringExtra(SearchManager.QUERY);
            extractor = new DBExtractor(this);

            adapter = new SearchAdapter(extractor.searchEstacion(searchQuery), this);

            listaEstaciones = (ListView) findViewById(R.id.listaEstaciones);
            listaEstaciones.setAdapter(adapter);

            SearchView searchField = (SearchView) findViewById(R.id.search);
            searchField.setQuery(searchQuery,false);
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
        ResultadoViewHolder viewHolder = new ResultadoViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(contexto).inflate(R.layout.fila_busqueda, parent, false);
        }
        viewHolder.nombreEstacion = (TextView) convertView.findViewById(R.id.nombreEstacion);
        //viewHolder.descripcionTexto = (TextView) convertView.findViewById(R.id.descripcionTexto);

        viewHolder.nombreEstacion.setText(estaciones.get(position).getNombre());
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