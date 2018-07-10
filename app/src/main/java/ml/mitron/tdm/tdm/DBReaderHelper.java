package ml.mitron.tdm.tdm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;









public class DBReaderHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = ContratoSQL.NombreDatabase + ".db";
    private final Context myContext;
    private static String DB_PATH;



    public DBReaderHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        DB_PATH = context.getFilesDir().getPath();
    }
    public void onCreate(SQLiteDatabase db) {
        //Nunca vamos a crear la base de datos, así que no hace falta especificar nada.
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void createDataBase() throws IOException {

        if (!checkDataBase()) {
            //(comprobamos si la base de datos NO existe)

            //By calling this method an empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }

        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + "/" + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database doesn't yet exist.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + "/" + DATABASE_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

}













final class ContratoSQL {

    public static final String NombreDatabase = "Database";

private ContratoSQL(){

};

    public static class TablaEstaciones implements BaseColumns {
        public static final String NombreTabla = "estaciones";
        public static final String NombreColumnaID = "_id";
        public static final String NombreColumnaNombreEstacion = "nombre";
        public static final String NombreColumnaLineas = "lineas";
    }

    public static class TablaDistancias implements BaseColumns {
        public static final String NombreTabla = "distancias";
        public static final String NombreColumnaIDOrigen = "origen";
        public static final String NombreColumnaIDDestino = "destino";
        public static final String NombreColumnaDistancia = "distancia";
    }

}
