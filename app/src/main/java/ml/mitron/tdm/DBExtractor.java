package ml.mitron.tdm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by acmc on 10/07/2018.
 */

//ESTA CLASE ES LA QUE VAMOS A USAR PARA LOS DATOS EN CONCRETO.

public final class DBExtractor {

    private final Context contexto;
    private DBReaderHelper miReaderHelper;
    private SQLiteDatabase database;

    private static DBExtractor instancia = null;

    DBExtractor(Context contexto) throws SQLiteException {
        miReaderHelper = new DBReaderHelper(contexto);
        try {
            database = miReaderHelper.getReadableDatabase();
        } catch (SQLiteException e) {
            throw (e);
        }
        this.contexto = contexto;
    }

    static DBExtractor getExtractor(Context contexto) {
        if (instancia == null) {
            instancia = new DBExtractor(contexto.getApplicationContext());
        }
        return instancia;
    }

    Boolean isOpen() {
        if (database != null) {
            return (database.isOpen());
        } else {
            return (false);
        }
    }

    void CloseDB() {
        database.close();
        miReaderHelper.close();
    }

    void OpenDB() throws SQLiteException {
        if (!isOpen()) {
            miReaderHelper = new DBReaderHelper(contexto);
            try {
                database = miReaderHelper.getReadableDatabase();
            } catch (SQLiteException e) {
                throw (e);
            }
        }
    }

    Integer getEstacionCount() throws SQLiteException {
        if (!database.isOpen()) {
            throw new SQLiteException("La conexión con la base de datos no está abierta");
        }

        Cursor cursor;

        cursor = database.query(true, ContratoSQL.TablaEstaciones.NombreTabla, null, null, null, null, null, null, null);

        return ((Integer) cursor.getCount());
    }


    List<Conexion> getConexiones(Integer IDEstacion) throws SQLiteException {
        if (!database.isOpen()) {
            throw new SQLiteException("La conexión con la base de datos no está abierta");
        }

        Cursor cursor;

        /*Vamos a obtener las conexiones.

        Las concexiones, en la base de datos, están definidas con
        - origen
        - destino
        - distancia entre ellos
        - líneas que operan la conexión

        Las conexiones son reversibles. Es decir, conexion(A,B) = conexion(B,A).

        Entonces, vamos a tener que buscar la estación cuando figura como origen pero también como destino.
        */

        List<Conexion> conexiones = new ArrayList<Conexion>();
        Set<Conexion> setConexiones = new HashSet<Conexion>();
        List<String> lineasConexion = new ArrayList<String>();

        cursor = database.query(true, ContratoSQL.TablaConexiones.NombreTabla, null, ContratoSQL.TablaConexiones.NombreColumnaIDOrigen + " = ? OR " + ContratoSQL.TablaConexiones.NombreColumnaIDDestino + " = ?", new String[]{IDEstacion.toString(), IDEstacion.toString()}, null, null, null, null);

        cursor.moveToFirst();


        while (cursor.getCount() != 0) {

            lineasConexion.addAll(Arrays.asList(cursor.getString(4).split(",")));
            /*for (String linea : Arrays.asList(cursor.getString(4).split(","))) {
                lineasConexion.add(new String(linea));
            }*/


            if (cursor.getInt(1) == IDEstacion) {
                setConexiones.add(new Conexion(IDEstacion, cursor.getInt(2), cursor.getInt(3), new ArrayList<>(lineasConexion)));
            } else if (cursor.getInt(2) == IDEstacion) {
                setConexiones.add(new Conexion(IDEstacion, cursor.getInt(1), cursor.getInt(3), new ArrayList<>(lineasConexion)));
            }

            if (cursor.isLast()) {
                break;
            }

            lineasConexion.clear();
            cursor.moveToNext();
        }

        conexiones.addAll(setConexiones);

        cursor.close();

        return (conexiones);
    }


    Estacion getEstacion(Integer IDEstacion) throws SQLiteException {
        if (!database.isOpen()) {
            throw new SQLiteException("La conexión con la base de datos no está abierta");
        }

        Cursor cursor;

        cursor = database.query(true, ContratoSQL.TablaEstaciones.NombreTabla, null, ContratoSQL.TablaEstaciones.NombreColumnaID + " = ?", new String[]{IDEstacion.toString()}, null, null, null, null);

        cursor.moveToFirst();

        /*Una estación tiene 4 cosas:

        - Integer id
        - String nombre
        - List<String> lineas
        - List<Distancia> distancias

        IMPORTANTE PARA ENTENDER LO DE LAS DISTANCIAS: cuando usamos distancia Nodo, es la distancia DE lA ESTACIÓN DE INICIO A LA ESTACIÓN ACTUAL.
         */

        //obtenemos el nombre de la estación

        String nombreEstacion = cursor.getString(1);

        //obtenemos las líneas de la estación

        List<String> lineasEstacion = new ArrayList<>();

        cursor.moveToFirst();

        lineasEstacion.addAll(Arrays.asList(cursor.getString(2).split(",")));

        /*for (String linea : Arrays.asList(cursor.getString(2).split(","))) {
            lineasEstacion.add(new String(linea));
        }*/

        /*Vamos a obtener las conexiones.

        Las concexiones, en la base de datos, están definidas con
        - origen
        - destino
        - distancia entre ellos
        - líneas que operan la conexión

        Las conexiones son reversibles. Es decir, conexion(A,B) = conexion(B,A).

        Entonces, vamos a tener que buscar la estación cuando figura como origen pero también como destino.
        */

        cursor.close();

        List<Conexion> conexiones = new ArrayList<Conexion>();
        Set<Conexion> setConexiones = new HashSet<Conexion>();
        List<String> lineasConexion = new ArrayList<String>();

        cursor = database.query(true, ContratoSQL.TablaConexiones.NombreTabla, null, ContratoSQL.TablaConexiones.NombreColumnaIDOrigen + " = ? OR " + ContratoSQL.TablaConexiones.NombreColumnaIDDestino + " = ?", new String[]{IDEstacion.toString(), IDEstacion.toString()}, null, null, null, null);

        cursor.moveToFirst();


        while (cursor.getCount() != 0) {

            lineasConexion.addAll(Arrays.asList(cursor.getString(4).split(",")));
            /*for (String linea : Arrays.asList(cursor.getString(4).split(","))) {
                lineasConexion.add(new String(linea));
            }*/

            if (cursor.getInt(1) == IDEstacion) {
                setConexiones.add(new Conexion(IDEstacion, cursor.getInt(2), cursor.getInt(3), new ArrayList<>(lineasConexion)));
            } else if (cursor.getInt(2) == IDEstacion) {
                setConexiones.add(new Conexion(IDEstacion, cursor.getInt(1), cursor.getInt(3), new ArrayList<>(lineasConexion)));
            }

            if (cursor.isLast()) {
                break;
            }


            lineasConexion.clear();
            cursor.moveToNext();
        }

        conexiones.addAll(setConexiones);

        cursor.close();

        return (new Estacion(IDEstacion, nombreEstacion, lineasEstacion, conexiones));
    }

    Estacion getEstacion(String nombreEstacion) throws SQLiteException {
        if (!database.isOpen()) {
            throw new SQLiteException("La conexión con la base de datos no está abierta");
        }

        Integer IDEstacion;

        Cursor cursor;

        cursor = database.query(true, ContratoSQL.TablaEstaciones.NombreTabla, null, ContratoSQL.TablaEstaciones.NombreColumnaNombreEstacion + " = ?", new String[]{nombreEstacion.toString()}, null, null, null, null);

        cursor.moveToFirst();

        /*Una estación tiene 4 cosas:

        - Integer id
        - String nombre
        - List<String> lineas
        - List<Distancia> distancias

        IMPORTANTE PARA ENTENDER LO DE LAS DISTANCIAS: cuando usamos distancia Nodo, es la distancia DE lA ESTACIÓN DE INICIO A LA ESTACIÓN ACTUAL.
         */

        //obtenemos el nombre de la estación

        String nombre = cursor.getString(1);

        IDEstacion = cursor.getInt(0);

        //obtenemos las líneas de la estación

        List<String> lineasEstacion = new ArrayList<>();

        cursor.moveToFirst();

        lineasEstacion.addAll(Arrays.asList(cursor.getString(2).split(",")));

        /*for (String linea : Arrays.asList(cursor.getString(2).split(","))) {
            lineasEstacion.add(new String(linea));
        }*/

        /*Vamos a obtener las conexiones.

        Las concexiones, en la base de datos, están definidas con
        - origen
        - destino
        - distancia entre ellos
        - líneas que operan la conexión

        Las conexiones son reversibles. Es decir, conexion(A,B) = conexion(B,A).

        Entonces, vamos a tener que buscar la estación cuando figura como origen pero también como destino.
        */

        cursor.close();

        List<Conexion> conexiones = new ArrayList<Conexion>();
        Set<Conexion> setConexiones = new HashSet<Conexion>();
        List<String> lineasConexion = new ArrayList<String>();

        cursor = database.query(true, ContratoSQL.TablaConexiones.NombreTabla, null, ContratoSQL.TablaConexiones.NombreColumnaIDOrigen + " = ? OR " + ContratoSQL.TablaConexiones.NombreColumnaIDDestino + " = ?", new String[]{IDEstacion.toString(), IDEstacion.toString()}, null, null, null, null);

        cursor.moveToFirst();


        while (cursor.getCount() != 0) {

            lineasConexion.addAll(Arrays.asList(cursor.getString(4).split(",")));
            /*for (String linea : Arrays.asList(cursor.getString(4).split(","))) {
                lineasConexion.add(new String(linea));
            }*/

            if (cursor.getInt(1) == IDEstacion) {
                setConexiones.add(new Conexion(IDEstacion, cursor.getInt(2), cursor.getInt(3), new ArrayList<>(lineasConexion)));
            } else if (cursor.getInt(2) == IDEstacion) {
                setConexiones.add(new Conexion(IDEstacion, cursor.getInt(1), cursor.getInt(3), new ArrayList<>(lineasConexion)));
            }

            if (cursor.isLast()) {
                break;
            }

            lineasConexion.clear();
            cursor.moveToNext();
        }

        conexiones.addAll(setConexiones);

        cursor.close();

        return (new Estacion(IDEstacion, nombre, lineasEstacion, conexiones));
    }

    List<Estacion> searchEstacion(String searchQuery) {
        List<Estacion> estaciones = new ArrayList<Estacion>();

        if (!database.isOpen()) {
            throw new SQLiteException("La conexión con la base de datos no está abierta");
        }

        Cursor cursor;
        Cursor cursorConexiones;
        Integer IDEstacion;
        String nombre;

        searchQuery = "'%" + searchQuery + "%'";

        cursor = database.rawQuery("SELECT DISTINCT * FROM " + ContratoSQL.TablaEstaciones.NombreTabla + " WHERE " + ContratoSQL.TablaEstaciones.NombreColumnaNombreEstacion + " LIKE " + searchQuery,
                null);

        cursor.moveToFirst();

        while (cursor.getCount() != 0) {

        /*Una estación tiene 4 cosas:

        - Integer id
        - String nombre
        - List<String> lineas
        - List<Distancia> distancias

        IMPORTANTE PARA ENTENDER LO DE LAS DISTANCIAS: cuando usamos distancia Nodo, es la distancia DE lA ESTACIÓN DE INICIO A LA ESTACIÓN ACTUAL.
         */

            //obtenemos el nombre de la estación

            nombre = cursor.getString(1);

            IDEstacion = cursor.getInt(0);

            //obtenemos las líneas de la estación

            List<String> lineasEstacion = new ArrayList<>();

            lineasEstacion.addAll(Arrays.asList(cursor.getString(2).split(",")));

        /*for (String linea : Arrays.asList(cursor.getString(2).split(","))) {
            lineasEstacion.add(new String(linea));
        }*/

        /*Vamos a obtener las conexiones.

        Las concexiones, en la base de datos, están definidas con
        - origen
        - destino
        - distancia entre ellos
        - líneas que operan la conexión

        Las conexiones son reversibles. Es decir, conexion(A,B) = conexion(B,A).

        Entonces, vamos a tener que buscar la estación cuando figura como origen pero también como destino.
        */


            List<Conexion> conexiones = new ArrayList<Conexion>();
            Set<Conexion> setConexiones = new HashSet<Conexion>();
            List<String> lineasConexion = new ArrayList<String>();

            cursorConexiones = database.query(true, ContratoSQL.TablaConexiones.NombreTabla, null, ContratoSQL.TablaConexiones.NombreColumnaIDOrigen + " = ? OR " + ContratoSQL.TablaConexiones.NombreColumnaIDDestino + " = ?", new String[]{IDEstacion.toString(), IDEstacion.toString()}, null, null, null, null);

            cursorConexiones.moveToFirst();


            while (cursorConexiones.getCount() != 0) {

                lineasConexion.addAll(Arrays.asList(cursorConexiones.getString(4).split(",")));
            /*for (String linea : Arrays.asList(cursor.getString(4).split(","))) {
                lineasConexion.add(new String(linea));
            }*/

                if (cursorConexiones.getInt(1) == IDEstacion) {
                    setConexiones.add(new Conexion(IDEstacion, cursorConexiones.getInt(2), cursorConexiones.getInt(3), new ArrayList<>(lineasConexion)));
                } else if (cursorConexiones.getInt(2) == IDEstacion) {
                    setConexiones.add(new Conexion(IDEstacion, cursorConexiones.getInt(1), cursorConexiones.getInt(3), new ArrayList<>(lineasConexion)));
                }

                if (cursorConexiones.isLast()) {
                    break;
                }

                lineasConexion.clear();
                cursorConexiones.moveToNext();
            }

            cursorConexiones.close();

            conexiones.addAll(setConexiones);

            estaciones.add(new Estacion(IDEstacion, nombre, lineasEstacion, conexiones));

            if (cursor.isLast()) {
                break;
            }

            cursor.moveToNext();
        }
        cursor.close();

        return estaciones;
    }
}