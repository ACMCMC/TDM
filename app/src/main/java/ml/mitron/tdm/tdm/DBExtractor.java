package ml.mitron.tdm.tdm;
import android.content.Context;

/**
 * Created by acmc on 10/07/2018.
 */

//ESTA CLASE ES LA QUE VAMOS A USAR PARA LOS DATOS EN CONCRETO.

public final class DBExtractor {

    DBExtractor(Context contexto){
        DBReaderHelper miReaderHelper = new DBReaderHelper(contexto);
        miReaderHelper.getReadableDatabase();
    }

}