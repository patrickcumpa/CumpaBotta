package serveres20;

import serveres20.server.UDPServer;
import serveres20.salva_dati.SalvaDati;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import serveres20.configuration.Configurazione;

/**
 *
 * @author PatrickCumpa
 * @since 04/10/2022
 */
public class CassonettoSmart {

    private TesseraRFID utente = null;
    private LocalDateTime oraApertura;
    public static ArrayList<TesseraRFID> tessereRegistrate = new ArrayList<>();
    protected static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    protected void apriCassonetto(int id) throws IOException {

        utente = trovaUtente(id);

        if (utente == null) {
            UDPServer.answerAndReceiveData(Configurazione.REGISTRARE);
            registraTessera(id);
        } else if (!utente.isValida()) {
            String risposta = "\nSiamo spiacenti, ma non puoi utilizzare il cassonetto fino al " 
                    + dtf.format(utente.getUltimaApertura().plusHours(72)) + "\n";
            UDPServer.answerData(risposta);
        } else {
            oraApertura = LocalDateTime.now();
            utente.setValida(false);
            utente.setUltimaApertura(oraApertura);
            SalvaDati.saveToFile();
            UDPServer.answerData(Configurazione.APERTO);
        }
    }

    protected void registraTessera(int id) throws IOException {
        TesseraRFID tessera = trovaUtente(id);

        if (tessera != null) {
            UDPServer.answerData(Configurazione.ESISTE);
        } else {
            tessera = new TesseraRFID(id);
            tessereRegistrate.add(tessera);
            SalvaDati.saveToFile();
            mostraTessereRegistrate();
            UDPServer.answerData(Configurazione.REGISTRATO);
        }
    }

    protected void eliminaTessera(int id) throws IOException {

        utente = trovaUtente(id);

        if (utente == null) {
            UDPServer.answerData(Configurazione.NON_REGISTRATO);
        } else if (UDPServer.answerAndReceiveData(Configurazione.ELIMINARE)) {
            tessereRegistrate.remove(utente);
            SalvaDati.saveToFile();
            UDPServer.answerData(Configurazione.ELIMINATO);
        }
        UDPServer.answerData("Errore!\n");
    }

    private TesseraRFID trovaUtente(int id) {
        for (TesseraRFID tessera : tessereRegistrate) {
            if (tessera.getId() == id) {
                return tessera;
            }
        }
        return null;
    }
    
    private void mostraTessereRegistrate() {
        System.out.println("--------------------------- TESSERE REGISTRATE ---------------------------");
        for (TesseraRFID t : CassonettoSmart.tessereRegistrate) {
            System.out.println(t);
        }
        System.out.println("--------------------------------------------------------------------------\n");
    }
}
