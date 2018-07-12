package ml.mitron.tdm.tdm;

import android.content.Context;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static android.R.attr.id;

public class Busqueda {
    //este código se ejecuta al pulsar el botón
    public static Ruta Busqueda(int inicio, int fin, DBExtractor extractor) throws IllegalArgumentException {
        Mapa mapa = new Mapa();

        return (mapa.obtenerRuta(inicio, fin, extractor));
    }
}

//la clase Estacion representa una estacion completa
class Estacion {
    private Integer id;
    private String nombre;
    private List<String> lineas;
    private List<Distancia> distancias;

    public Estacion(Integer id, String nombre, List<String> lineas, List<Distancia> distancias) {
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

    List<String> getLineas() {
        return (lineas);
    }
}

//la clase Distancia solo sirve para representar distancias, como un vector
class Distancia implements Comparable<Distancia> {
    private Integer origen;
    private Integer destino;
    private Integer distancia;

    Distancia(Integer IDDestino, Integer distancia) {
        this.destino = IDDestino;
        this.distancia = distancia;
    }

    Distancia(Integer IDOrigen, Integer IDDestino, Integer distancia) {
        this.origen = IDOrigen;
        this.destino = IDDestino;
        this.distancia = distancia;
    }

    public Integer getIDDestino() {
        return (destino);
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
            return (1);
        } else if (distancia < o.getDistancia()) {
            return (-1);
        } else {
            return (0);
        }
    }
}


class seccionLinea {
    Estacion origen;
    Estacion fin;
    String nombre;
    String nombrePropio;

    seccionLinea(Estacion origen, Estacion fin, String nombreLinea) {
        this.origen = origen;
        this.fin = fin;
        this.nombre = nombreLinea;
        this.nombrePropio = "";
    }

    seccionLinea(Estacion origen, Estacion fin, String nombreLinea, String nombrePropio) {
        this.origen = origen;
        this.fin = fin;
        this.nombre = nombreLinea;
        this.nombrePropio = nombrePropio;
    }

    String getNombre() {
        return nombre;
    }

    String getNombrePropio(Context contexto) throws NoSuchElementException {
        if (nombrePropio.isEmpty()) {
            switch (nombre) {
                case "1": {
                    nombrePropio = (String) contexto.getResources().getText(R.string.l1);
                    break;
                }
                case "2": {
                    nombrePropio = (String) contexto.getResources().getText(R.string.l2);
                    break;
                }
                case "3": {
                    nombrePropio = (String) contexto.getResources().getText(R.string.l3);
                    break;
                }
                case "4": {
                    nombrePropio = (String) contexto.getResources().getText(R.string.l4);
                    break;
                }
                case "5": {
                    nombrePropio = (String) contexto.getResources().getText(R.string.l5);
                    break;
                }
                case "6": {
                    nombrePropio = (String) contexto.getResources().getText(R.string.l6);
                    break;
                }
                case "7": {
                    nombrePropio = (String) contexto.getResources().getText(R.string.l7);
                    break;
                }
                case "7A": {
                    nombrePropio = (String) contexto.getResources().getText(R.string.l7A);
                    break;
                }
                case "7B": {
                    nombrePropio = (String) contexto.getResources().getText(R.string.l7B);
                    break;
                }
                case "8": {
                    nombrePropio = (String) contexto.getResources().getText(R.string.l8);
                    break;
                }
                case "T": {
                    nombrePropio = (String) contexto.getResources().getText(R.string.T);
                    break;
                }
                case "C": {
                    nombrePropio = (String) contexto.getResources().getText(R.string.C);
                    break;
                }
                case "F": {
                    nombrePropio = (String) contexto.getResources().getText(R.string.F);
                    break;
                }
                default: {
                    nombrePropio = null;
                    throw new NoSuchElementException();
                }
            }
        }
        return nombrePropio;
    }

    Boolean checkInicioLinea(Estacion inicio) {
        return (this.origen.equals(inicio));
    }

    Boolean checkFinLinea(Estacion fin) {
        return (this.fin.equals(fin));
    }
}


class Ruta {
    List<Estacion> estaciones;
    List<seccionLinea> lineas;
    Integer tiempo;

    Ruta() {
        estaciones = new ArrayList<Estacion>();
        lineas = new ArrayList<seccionLinea>();
    }

    Ruta(List<Estacion> estaciones, List<seccionLinea> lineas) {
        this.estaciones = estaciones;
        this.lineas = lineas;
    }

    void anadirEstacion(Estacion estacion) {
        estaciones.add(estacion);
    }

    void anadirSeccionLinea(seccionLinea linea) {
        lineas.add(linea);
    }

    List<Estacion> getEstacionesRuta() {
        return (estaciones);
    }

    List<String> getNombresRuta() {
        List<String> nombres = new ArrayList<String>();
        for (Estacion estacion : estaciones) {
            nombres.add(estacion.getNombre());
        }
        return (nombres);
    }

    List<Integer> getIDsRuta() {
        List<Integer> ids = new ArrayList<Integer>();
        for (Estacion estacion : estaciones) {
            ids.add(estacion.getId());
        }
        return (ids);
    }

    List<seccionLinea> getLineas() {
        if (lineas.isEmpty()) {
            calcularLineas();
        }
        return lineas;
    }

    void calcularLineas() {
        Boolean contenido;
        Estacion lastEstacion = estaciones.get(0);

        //Usamos un Set para las líneas disponibles
        Set<String> lineasDisponibles = new HashSet<String>();

        //debemos usar el removeStack porque si no, estamos editando el Set mientras estamos en un bucle for.
        List<String> removeStack = new ArrayList<String>();
        lineasDisponibles.addAll(estaciones.get(0).getLineas());
        for (Estacion estacionActual : estaciones) {

            contenido = false;

            for (String linea : lineasDisponibles) {
                if (estacionActual.getLineas().contains(linea)) {
                    contenido = true;
                    break;
                }
            }
            if (!contenido) {
                lineas.add(new seccionLinea(lastEstacion, estaciones.get(estaciones.indexOf(estacionActual) - 1), lineasDisponibles.toArray()[0].toString()));
                lineasDisponibles.clear();
                lineasDisponibles.addAll(estaciones.get(estaciones.indexOf(estacionActual) - 1).getLineas());
                for (String linea : lineasDisponibles) {
                    if (!estacionActual.getLineas().contains(linea)) {
                        removeStack.add(linea);
                    }
                }
                lineasDisponibles.removeAll(removeStack);
                removeStack.clear();
                lastEstacion = estaciones.get(estaciones.indexOf(estacionActual) - 1);
            } else {

                //es aquí donde estaríamos editando el Set mientras estamos en un bucle for
                for (String linea : lineasDisponibles) {
                    if (!estacionActual.getLineas().contains(linea)) {
                        removeStack.add(linea);
                    }
                }
                lineasDisponibles.removeAll(removeStack);
                removeStack.clear();
            }
        }
        for (String linea : lineasDisponibles) {
            if (!estaciones.get(estaciones.size() - 1).getLineas().contains(linea)) {
                removeStack.add(linea);
            }
        }
        lineasDisponibles.removeAll(removeStack);
        removeStack.clear();
        lineas.add(new seccionLinea(lastEstacion, estaciones.get(estaciones.size() - 1), lineasDisponibles.toArray()[0].toString()));
    }

    void setTiempo(Integer tiempo) {
        this.tiempo = tiempo;
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
        return (estaciones.get(id));
    }

    public Ruta obtenerRuta(Integer inicio, Integer fin, DBExtractor extractor) throws IllegalArgumentException {

        //esta Pila es la que analiza las distancias a cada estación colindante
        PriorityQueue<Distancia> pilaNodos = new PriorityQueue<>();

        //aquí, guardamos las distancias a cada estación
        HashMap<Integer, Integer> distancias = new HashMap<>();

        //esto nos sirve para relacionar cada estación con la que la precede
        HashMap<Integer, Integer> nodoPrevio = new HashMap<>();


        //VAMOS A COMENZAR EL PROCESO DE BÚSQUEDA

        //La estación de origen está a 0 de la estación de origen
        pilaNodos.add(new Distancia(inicio, inicio, 0));
        distancias.put(inicio, 0);

        //mientras haya nodos en la pila...
        while (!pilaNodos.isEmpty()) {
            Distancia distanciaNodo = pilaNodos.poll();
            if (distanciaNodo.getIDDestino().equals(fin)) {
                break;
            }

            //hacemos un List de las distancias
            List<Distancia> distsColindantes = extractor.GetEstacion(distanciaNodo.getIDDestino()).getDistancias();

            //vamos a borrar la distancia que apunta al nodo del que venimos, ya que no tiene sentido analizarla.
            for (Distancia distanciaPrevia : distsColindantes) {
                if (distanciaPrevia.getIDDestino().equals(nodoPrevio.get(distanciaNodo.getIDDestino()))) {
                    distsColindantes.remove(distanciaPrevia);
                    break;
                }
            }


            for (Distancia distanciaColindante : distsColindantes) {
                if (!distancias.containsKey(distanciaColindante.getIDDestino())) {

                    //Si el registro de distancias no sabe que distancia hay al nodo colindante, entonces simplemente la añadimos
                    distancias.put(distanciaColindante.getIDDestino(), distanciaNodo.getDistancia() + distanciaColindante.getDistancia());

                    //añadimos este nodo a la Priority Queue
                    pilaNodos.add(new Distancia(inicio, distanciaColindante.getIDDestino(), distanciaNodo.getDistancia() + distanciaColindante.getDistancia()));

                    //registramos cuál era el nodo previo
                    nodoPrevio.put(distanciaColindante.getIDDestino(), distanciaNodo.getIDDestino());


                    //puede pasar que ya llegásemos al nodo desde otro sitio, entonces solo vamos a registrar este nuevo camino si es más corto.
                } else if (distanciaNodo.getDistancia() + distanciaColindante.getDistancia() < distancias.get(distanciaColindante.getIDDestino())) {
                    distancias.remove(distanciaColindante.getIDDestino());
                    distancias.put(distanciaColindante.getIDDestino(), distanciaNodo.getDistancia() + distanciaColindante.getDistancia());
                    pilaNodos.add(new Distancia(inicio, distanciaColindante.getIDDestino(), distanciaNodo.getDistancia() + distanciaColindante.getDistancia()));
                    nodoPrevio.remove(distanciaColindante.getIDDestino());
                    nodoPrevio.put(distanciaColindante.getIDDestino(), distanciaNodo.getIDDestino());
                }
            }
        }

        //formamos la ruta...
        Ruta ruta = new Ruta();

        List<Integer> orden = new ArrayList<Integer>();

        if(!nodoPrevio.containsKey(fin)) {
            throw new IllegalArgumentException();
        }

        for (Integer i = fin; !i.equals(inicio); i = nodoPrevio.get(i)) {
            orden.add(i);
        }

        orden.add(inicio);

        while (!orden.isEmpty()) {
            ruta.anadirEstacion(extractor.GetEstacion(orden.get(orden.size() - 1)));
            orden.remove(orden.size() - 1);
        }

        ruta.setTiempo(distancias.get(fin));

        return (ruta);
    }
}
