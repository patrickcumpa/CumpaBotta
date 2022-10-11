package serveres20;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 *
 * @author PatrickCumpa
 * @since 04/10/2022
 */
public class TesseraRFID implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int id;
    private boolean valida;
    private LocalDateTime ultimaApertura;

    public TesseraRFID(int id) {
        this.id = id;
        this.valida = true;
        this.ultimaApertura = LocalDateTime.now();
    }
    
    protected int getId() {
        return this.id;
    }
    
    private boolean getValida() {
        return this.valida;
    }
    
    protected void setValida(boolean valida) {
        this.valida = valida;
    }
    
    protected boolean isValida() {  

        Duration duration = Duration.between(getUltimaApertura(), 
                LocalDateTime.now());
        Duration scadenza = Duration.parse("PT72H");

        if (getValida())
            return true; // mai aperto
        if (duration.compareTo(scadenza) == 0 || 
                duration.compareTo(scadenza) >= 1) {
            setValida(true);
            return true; // valido di nuovo
        }
        return getValida(); // ritorna falso
    }

    protected LocalDateTime getUltimaApertura() {
        return this.ultimaApertura;
    }

    protected void setUltimaApertura(LocalDateTime ultimaApertura) {
        this.ultimaApertura = ultimaApertura;
    }

    @Override
    public String toString() {
        return "Tessere RFID{" + "id = " + id + ", valida = " + valida + ", ultimaApertura = " + 
                CassonettoSmart.dtf.format(ultimaApertura) + '}';
    }
    
}
