package serveres20.salva_dati;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import serveres20.CassonettoSmart;
import serveres20.TesseraRFID;

/**
 *
 * @author PatrickCumpa
 * @since 04/10/2022
 */
public class SalvaDati implements Serializable {
    
    public static void saveToFile() throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream("src\\serveres20\\salva_dati\\registro_tessere.ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(CassonettoSmart.tessereRegistrate);
        oos.writeObject(TesseraRFID.numeriGenerati);
        oos.close();
        fos.close();
    }

    public static void readToFile() throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream("src\\serveres20\\salva_dati\\registro_tessere.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);
        CassonettoSmart.tessereRegistrate = (ArrayList<TesseraRFID>) ois.readObject();
        TesseraRFID.numeriGenerati = (ArrayList<Integer>) ois.readObject();
        ois.close();
        fis.close();
    }
    
}
