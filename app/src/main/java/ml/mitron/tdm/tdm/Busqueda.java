package ml.mitron.tdm.tdm;

import android.content.Context;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Set;

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
    private List<Conexion> conexiones;

    public Estacion(Integer id, String nombre, List<String> lineas, List<Conexion> conexiones) {
        this.id = id;
        this.nombre = nombre;
        this.lineas = lineas;
        this.conexiones = conexiones;
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

    public List<Conexion> getConexiones() {
        return (conexiones);
    }

    List<String> getLineas() {
        return (lineas);
    }
}

//la clase Distancia solo sirve para representar conexiones, como un vector
class Conexion implements Comparable<Conexion> {
    private Integer origen;
    private Integer destino;
    private Integer distancia;
    private List<String> lineas;

    Conexion(Integer IDDestino, Integer distancia, List<String> lineas) {
        this.destino = IDDestino;
        this.distancia = distancia;
        this.lineas = lineas;
    }

    Conexion(Integer IDOrigen, Integer IDDestino, Integer distancia, List<String> lineas) {
        this.origen = IDOrigen;
        this.destino = IDDestino;
        this.distancia = distancia;
        this.lineas = lineas;
    }

    List<String> getLineas() {
        return(lineas);
    }

    Integer getIDDestino() {
        return (destino);
    }

    Integer getIDOrigen() {
        return (origen);
    }

    Integer getDistancia() {
        return (distancia);
    }

    public void setDistancia(Integer distancia) {
        this.distancia = distancia;
    }

    @Override
    public int compareTo(Conexion o) {
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
        return (this.origen.getId().equals(inicio.getId()));
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

    @Deprecated
    /*
    Lo guardo por si hiciera falta, pero las líneas ya se calculan al crear la ruta en la clase Mapa.
    Este método puede ser inexacto, porque se basa en las estaciones y no en las conexiones.
     */
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
    final static Integer tiempoCambioLinea = 10;
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

        //esta Pila es la que analiza las conexiones a cada estación colindante
        PriorityQueue<Conexion> pilaConexiones = new PriorityQueue<>();

        //aquí, guardamos las conexiones a cada estación
        HashMap<Integer, Integer> distancias = new HashMap<>();

        //esto nos sirve para relacionar cada estación con la que la precede
        HashMap<Integer, Conexion> nodoPrevio = new HashMap<>();

        /*
        Hasta ahí nos sirve para analizar las ESTACIONES por las que pasa la ruta.
        PERO también queremos tener en cuenta los cambios de línea; de esa forma se tendrán en cuenta para el cómputo final.
        Así que para eso vamos a mantener un registro de las líneas también.
        */

        Set<String> lineasCoincidentes = new HashSet<String>();

        //VAMOS A COMENZAR EL PROCESO DE BÚSQUEDA

        //La estación de origen está a 0 de la estación de origen
        pilaConexiones.add(new Conexion(inicio, inicio, 0, extractor.getEstacion(inicio).getLineas()));
        distancias.put(inicio, 0);

        //Repetimos este bucle mientras haya conexiones en la pila para analizar. Si nos quedamos sin estaciones, no hay ruta posible.
        while (!pilaConexiones.isEmpty()) {
            Conexion conexionANodo = pilaConexiones.poll();

            //aquí vamos a comprobar si hemos llegado a la estación de destino.
            if (conexionANodo.getIDDestino().equals(fin)) {
                break;
            }

            //hacemos un List de las conexiones
            List<Conexion> conexiones = extractor.getEstacion(conexionANodo.getIDDestino()).getConexiones();

            //vamos a borrar la distancia que apunta al nodo del que venimos, ya que no tiene sentido analizarla.
            for (Conexion conexionRedundante : conexiones) {
                if (conexionRedundante.getIDDestino().equals(conexionANodo.getIDDestino())) {
                    conexiones.remove(conexionRedundante);
                    break;
                }
            }

            /*analizamos todas las conexiones de la estación que está conectada

            esto va más o menos así:

            |ESTACION ORIGEN|

                \/
            (Conexión)            Las conexiones se almacenan en una PriorityQueue, que se llama pilaConexiones.

                \/
            |ESTACIÓN CONECTADA|

                \/
            (Conexión)

                \/
            |ESTACIÓN DESTINO|

             */

            for (Conexion conexionFutura : conexiones) {

                lineasCoincidentes.clear();

                Integer distanciaAEstacion;

                for (String linea : conexionANodo.getLineas()) {
                    if (conexionFutura.getLineas().contains(linea)) {
                        lineasCoincidentes.add(linea);
                    }
                }

                if (lineasCoincidentes.isEmpty()) {
                    distanciaAEstacion = conexionANodo.getDistancia() + conexionFutura.getDistancia() + tiempoCambioLinea;
                } else {
                    distanciaAEstacion = conexionANodo.getDistancia() + conexionFutura.getDistancia();
                }

                //ya hemos comprobado las líneas que hay en común entre los dos nodos; vamos ahora a ver las distancias

                if (!distancias.containsKey(conexionFutura.getIDDestino())) {

                    //Dado que el registro de conexiones no sabe qué distancia hay al nodo colindante, entonces simplemente la añadimos
                    distancias.put(conexionFutura.getIDDestino(), distanciaAEstacion);

                    //añadimos este nodo a la Priority Queue
                    pilaConexiones.add(new Conexion(inicio, conexionFutura.getIDDestino(), distanciaAEstacion, conexionFutura.getLineas()));

                    //registramos cuál era el nodo previo
                    nodoPrevio.put(conexionFutura.getIDDestino(), conexionFutura);


                    //puede pasar que ya llegásemos al nodo desde otro sitio, entonces solo vamos a registrar este nuevo camino si es más corto.
                } else if (distanciaAEstacion < distancias.get(conexionFutura.getIDDestino())) {
                    distancias.remove(conexionFutura.getIDDestino());
                    distancias.put(conexionFutura.getIDDestino(), conexionANodo.getDistancia() + conexionFutura.getDistancia());

                    pilaConexiones.add(new Conexion(inicio, conexionFutura.getIDDestino(), distanciaAEstacion, conexionFutura.getLineas()));

                    nodoPrevio.remove(conexionFutura.getIDDestino());
                    nodoPrevio.put(conexionFutura.getIDDestino(), conexionFutura);
                }
            }
        }

        //formamos la ruta...
        Ruta ruta = new Ruta();

        List<Integer> orden = new ArrayList<Integer>();

        if(!nodoPrevio.containsKey(fin)) {
            throw new IllegalArgumentException();
        }

        for (Integer i = fin; !i.equals(inicio); i = nodoPrevio.get(i).getIDOrigen()) {
            orden.add(i);
        }

        orden.add(inicio);

        for (Integer i = orden.size() - 1; i >= 0; i--) {
            ruta.anadirEstacion(extractor.getEstacion(orden.get(i)));
        }

        //VAMOS A CALCULAR LOS TRAMOS DE LÍNEA

        /*Esto lo calculamos del inicio al final:

         el ArrayList "orden" está del revés; la estación 0 es el final
         por eso empezamos en orden.size() - 1
         pero podríamos calcularlo al revés también, aunque sería más rollo porque al final habría que darle la vuelta*/

        List<seccionLinea> listaLineas = new ArrayList<seccionLinea>();

        Set<String> lineasDisponibles = new HashSet<String>();

        Integer idEstacionOrigenLinea = orden.get(orden.size() - 1);

        lineasDisponibles.addAll(nodoPrevio.get(orden.get(0)).getLineas());

        for (Integer i = 0; i < orden.size() - 1; i++) {

            //Vamos a copiar las líneas disponibles para ver si hay en común.
            //Si no hay, entonces tenemos la copia de las que había para añadirlas.

            Set<String> pruebaLineasDisponibles = new HashSet<String>(lineasDisponibles);

            //el método retainAll es equivalente a comprobar los elementos que son comunes.
            //Es decir, devuelve sólo un Set de los elementos comunes.

            pruebaLineasDisponibles.retainAll(nodoPrevio.get(orden.get(i)).getLineas());

            if (pruebaLineasDisponibles.isEmpty()) {
                listaLineas.add(new seccionLinea(extractor.getEstacion(orden.get(i)), extractor.getEstacion(idEstacionOrigenLinea), (String) lineasDisponibles.toArray()[0]));
                lineasDisponibles.clear();
                lineasDisponibles.addAll(nodoPrevio.get(orden.get(i)).getLineas());

                idEstacionOrigenLinea = orden.get(i);
            } else {
                lineasDisponibles = pruebaLineasDisponibles;
            }

        }

        //cuando llegamos a la estación inicial, tenemos que añadir con ella una nueva línea hasta ella.

        listaLineas.add(new seccionLinea(extractor.getEstacion(orden.get(orden.size() - 1)), extractor.getEstacion(idEstacionOrigenLinea), (String) lineasDisponibles.toArray()[0]));

        for (Integer i = listaLineas.size() - 1; i >= 0; i--) {
            ruta.anadirSeccionLinea(listaLineas.get(i));
        }

        //HEMOS ACABADO DE CALCULAR LOS TRAMOS DE LÍNEA

        ruta.setTiempo(distancias.get(fin));

        return (ruta);
    }
}
