package ml.mitron.tdm.tdm;

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

    DBExtractor(Context contexto) throws SQLiteException {
        miReaderHelper = new DBReaderHelper(contexto);
        try {
            database = miReaderHelper.getReadableDatabase();
        } catch (SQLiteException e) {
            throw (e);
        }
        this.contexto = contexto;
    }

    Boolean isOpen() {
        if (database != null){
            return(database.isOpen());
        } else {
            return(false);
        }
    }

    void CloseDB() {
        database.close();
        database = null;
        miReaderHelper.close();
        miReaderHelper = null;
    }

    void OpenDB() throws SQLiteException {
        miReaderHelper = new DBReaderHelper(contexto);
        try {
            database = miReaderHelper.getReadableDatabase();
        } catch (SQLiteException e) {
            throw (e);
        }
    }

    Integer GetEstacionCount() throws SQLiteException {
        if (!database.isOpen()) {
            throw new SQLiteException("La conexión con la base de datos no está abierta");
        }

        Cursor cursor;

        cursor = database.query(true,ContratoSQL.TablaEstaciones.NombreTabla, null,null,null, null, null, null, null);

        return((Integer) cursor.getCount());
    }



    Estacion GetEstacion(Integer IDEstacion) throws SQLiteException {
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

        List<String> lineas;

        cursor.moveToFirst();

        lineas = Arrays.asList(cursor.getString(2).split(","));

        /*Vamos a obtener las distancias.

        Las distancias, en la base de datos, están definidas con
        - origen
        - destino
        - distancia entre ellos

        Las distancias son reversibles. Es decir, distancia(A,B) = distancia(B,A).

        Entonces, vamos a tener que buscar la estación cuando figura como origen pero también como destino.
        */

        cursor.close();

        List<Distancia> distancias = new ArrayList<Distancia>();
        Set<Distancia> setDistancias = new HashSet<Distancia>();

        cursor = database.query(true, ContratoSQL.TablaDistancias.NombreTabla, null, ContratoSQL.TablaDistancias.NombreColumnaIDOrigen + " = ? OR " + ContratoSQL.TablaDistancias.NombreColumnaIDDestino + " = ?", new String[]{IDEstacion.toString(), IDEstacion.toString()}, null, null, null, null);

        cursor.moveToFirst();

        while(cursor.getCount() != 0) {
            if (cursor.getInt(1) == IDEstacion) {
                setDistancias.add(new Distancia(IDEstacion, cursor.getInt(2), cursor.getInt(3)));
            } else if (cursor.getInt(2) == IDEstacion) {
                setDistancias.add(new Distancia(IDEstacion, cursor.getInt(1), cursor.getInt(3)));
            }
            if (cursor.isLast()) {
                break;
            }
            cursor.moveToNext();
        }

        distancias.addAll(setDistancias);

        cursor.close();

        return (new Estacion(IDEstacion,nombreEstacion,lineas,distancias));
    }

}