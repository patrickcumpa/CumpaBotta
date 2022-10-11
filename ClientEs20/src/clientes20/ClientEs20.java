package clientes20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author TommasoBotta
 */
public class ClientEs20 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        try {
            int id;
            int scelta = 0;
            String risposta;
            String nonRegistrato = "\nSiamo spiacenti, ma non risulti essere registrato.\nVuoi registrarti? [Y/n] ";
            String vuoiEliminare = "\nVuoi eliminare la tua tessera? [Y/n] ";
            Scanner in = new Scanner(System.in);

            UDPClient client = new UDPClient();

            boolean loop = true;

            while (loop) {
                System.out.println("-------- BENVENUTO --------");
                System.out.println("1. Apri Cassonetto");
                System.out.println("2. Crea tessera");
                System.out.println("3. Elimina tessera");
                System.out.println("4. Esci");
                System.out.print("Effettuare una scelta: ");

                scelta = Integer.parseInt(input.readLine());
                System.out.println("---------------------------\n");

                switch (scelta) {
                    case 1:
                        System.out.print("Per favore, inserire codice: ");
                        id = Integer.parseInt(input.readLine());
                        risposta = client.sendAndReceiveData(scelta, id);
                        if (risposta.equals(nonRegistrato) || risposta.equals(vuoiEliminare)) {
                            System.out.print(risposta);
                            char sc = in.next().charAt(0);
                            risposta = client.elaboraRisposta(sc);
                            System.out.println(risposta);
                            break;
                        }
                        System.out.println(risposta);
                        break;

                    case 2:
                        System.out.print("Per favore, inserire codice da registrare: ");
                        id = Integer.parseInt(input.readLine());
                        risposta = client.sendAndReceiveData(scelta, id);
                        System.out.println(risposta);
                        break;

                    case 3:
                        System.out.print("Per favore, inserire codice da eliminare: ");
                        id = Integer.parseInt(input.readLine());
                        risposta = client.sendAndReceiveData(scelta, id);
                        if (risposta.equals(nonRegistrato) || risposta.equals(vuoiEliminare)) {
                            System.out.print(risposta);
                            char sc = in.next().charAt(0);
                            risposta = client.elaboraRisposta(sc);
                            System.out.println(risposta);
                            break;
                        }
                        System.out.println(risposta);
                        break;

                    case 4:
                        loop = false;
                        break;

                    default:
                        System.out.println("Per favore inserire una scelta valida!");
                }
            }
            client.chiudi(scelta);

        } catch (SocketException ex) {
            Logger.getLogger(ClientEs20.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(ClientEs20.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClientEs20.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
