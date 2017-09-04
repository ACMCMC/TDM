package ml.mitron.tdm.tdm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Busqueda {
    //este código se ejecuta al pulsar el botón
    public static ArrayList<String> Busqueda(int inicio, int fin) {
        Mapa mapa = new Mapa();
        mapa.anadirEstacion(new Estacion(1, "Mitron Centraal", Arrays.asList(2, 4, 5, 6, 7, 8), Arrays.asList(new Distancia(2, 4), new Distancia(3, 3), new Distancia(8, 7))));
        mapa.anadirEstacion(new Estacion(2, "Mitron Industriel", Arrays.asList(5, 6), Arrays.asList(new Distancia(1, 4))));
        mapa.anadirEstacion(new Estacion(3, "Avinguda América", Arrays.asList(5), Arrays.asList(new Distancia(1, 3), new Distancia(4, 7))));
        mapa.anadirEstacion(new Estacion(4, "Av. Félix Iglesias", Arrays.asList(5), Arrays.asList(new Distancia(3, 7), new Distancia(5, 4))));
        mapa.anadirEstacion(new Estacion(5, "Teatre", Arrays.asList(5), Arrays.asList(new Distancia(4, 4), new Distancia(6, 3))));
        mapa.anadirEstacion(new Estacion(6, "Av. Picasso Noord", Arrays.asList(5, 8), Arrays.asList(new Distancia(5, 3), new Distancia(7, 2))));
        mapa.anadirEstacion(new Estacion(7, "Centraalen Clinice Hospitaal", Arrays.asList(8), Arrays.asList(new Distancia(6, 2), new Distancia(8, 3))));
        mapa.anadirEstacion(new Estacion(8, "Urgències", Arrays.asList(5), Arrays.asList(new Distancia(7, 3), new Distancia(1, 7))));
        /*for (Integer resultado : mapa.obtenerRuta((Integer)inicio,(Integer)fin)) {
            System.out.println(mapa.getEstacion(resultado).getNombre());
        }*/

        //devolvemos el resultado

        ArrayList<String> listaEstaciones = new ArrayList<String>();

        for (Integer i : mapa.obtenerRuta((Integer)inicio,(Integer)fin)) {
            listaEstaciones.add(mapa.getEstacion(i).getNombre());
        }
        return(listaEstaciones);
    }
}

//la clase Estacion representa una estacion completa
class Estacion {
    private Integer id;
    private String nombre;
    private List<Integer> lineas;
    private List<Distancia> distancias;

    public Estacion(Integer id, String nombre, List lineas, List distancias) {
        this.id = id;
        this.nombre = nombre;
        this.lineas = lineas;
        this.distancias = distancias;
    }

    public String getNombre() {
        return (nombre);
    }

    public Integer getId() {
        return id;
    }

    /*public ArrayList<Integer> getLineas {
        return(lineas);
    }*/

    public List<Distancia> getDistancias() {
        return (distancias);
    }
}

//la clase Distancia solo sirve para representar distancias, como un vector
class Distancia implements Comparable<Distancia> {
    private Integer id;
    private Integer distancia;

    public Distancia(Integer id, Integer distancia) {
        this.id = id;
        this.distancia = distancia;
    }

    public Integer getId() {
        return (id);
    }

    public Integer getDistancia() {
        return (distancia);
    }

    public void setDistancia(Integer distancia) {
        this.distancia = distancia;
    }

    @Override
    public int compareTo(Distancia o) {
        if (distancia > o.getDistancia()) {
            return -1;
        } else if (distancia < o.getDistancia()) {
            return 1;
        } else {
            return 0;
        }
    }
}

class Mapa {
    private final Map<Integer, Estacion> estaciones;

    Mapa() {
        this.estaciones = new HashMap<Integer, Estacion>();
    }

    public void anadirEstacion(Estacion estacion) {
        estaciones.put(estacion.getId(), estacion);
        //se extraen el key (id) y su valor
    }

    public Estacion getEstacion(Integer id) {
        return(estaciones.get(id));
    }

    public ArrayList<Integer> obtenerRuta(Integer inicio, Integer fin) {
        PriorityQueue<Distancia> pilaNodos = new PriorityQueue<>();
        HashMap<Integer, Integer> distancias = new HashMap<>();
        HashMap<Integer, Integer> nodoPrevio = new HashMap<>();
        //inicializamos las distancias
        for (Map.Entry<Integer, Estacion> registro : estaciones.entrySet()) {
            //registro está formado por el key en el HashMap y su valor (Estacion)
            if (registro.getKey() == inicio) {
                pilaNodos.add(new Distancia(inicio, 0));
                distancias.put(inicio, 0);
            } else {
                //pilaNodos.add(new Distancia(registro.getValue().getId(), Integer.MAX_VALUE));
                distancias.put(registro.getKey(), Integer.MAX_VALUE);
            }
        }
        //fin de la inicialización de la búsqueda
        //búsqueda de ruta
        while (!pilaNodos.isEmpty()) {
            //mientras quedan nodos en la pila...
            Distancia nodo = pilaNodos.poll();
            //obtenemos el nodo de la pila
            if (nodo.getId().equals(fin)) {
                break;
            }
            for (Distancia distancia : estaciones.get(nodo.getId()).getDistancias()) {
                //para cada distancia de la estación asociada al nodo...
                if ((distancia.getDistancia() + nodo.getDistancia()) < distancias.get(distancia.getId())) {
                    distancias.remove(distancia.getId());
                    distancias.put(distancia.getId(), (distancia.getDistancia() + nodo.getDistancia()));
                    pilaNodos.add(new Distancia(distancia.getId(), (distancia.getDistancia() + nodo.getDistancia())));
                    nodoPrevio.put(distancia.getId(), nodo.getId());
                }
            }
        }
        ArrayList<Integer> ruta = new ArrayList<>();
        for (Integer i = fin; !i.equals(inicio); i = nodoPrevio.get(i)) {
            ruta.add(i);
        }

        ruta.add(inicio);

        //creamos una ruta, pero está invertida

        Integer tamañoRuta = ruta.size() - 1;

        for (Integer indice = 0 ; indice < ruta.size() ; indice++) {
            ruta.add(ruta.get(tamañoRuta - indice));
            ruta.remove(tamañoRuta - indice);
        }

        //le damos la vuelta a la ruta

        return (ruta);
    }
    /*public ArrayList<Integer> encontrarLineas (ArrayList<Integer> ruta) {
        for (Integer numero = 0;numero < ruta.size();numero++) {
            for (estaciones.get(ruta.get(numero)).) {

            }
        }
return(new ArrayList<>());
    }*/
}