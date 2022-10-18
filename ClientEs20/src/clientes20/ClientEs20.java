package clientes20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.Scanner;

/**
 *
 * @author TommasoBotta
 */
public class ClientEs20 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        try {
            int id;
            int scelta;
            String risposta;
            boolean loop = true;
            UDPClient client = new UDPClient("localhost", 12345);

            while (loop) {
                System.out.println("-------- BENVENUTO --------");
                System.out.println("1. Apri Cassonetto");
                System.out.println("2. Crea tessera");
                System.out.println("3. Elimina tessera");
                System.out.println("4. Esci");
                System.out.print("Effettuare una scelta: ");

                scelta = Integer.parseInt(input.readLine());
                client.sendInt(scelta);
                System.out.println("---------------------------\n");

                switch (scelta) {
                    case 1:
                        System.out.print("Per favore, inserisci id: ");
                        id = Integer.parseInt(input.readLine());
                        risposta = client.sendAndReceiveData(id);
                        if (risposta.contains("registrarti?")) {
                            System.out.print(risposta);
                            char sc = in.next().charAt(0);
                            risposta = client.elaboraRisposta(sc);
                            System.out.println(risposta);
                            break;
                        }
                        System.out.println(risposta);
                        break;

                    case 2:
                        System.out.print("Per favore, inserisci id: ");
                        id = Integer.parseInt(input.readLine());
                        risposta = client.sendAndReceiveData(id);
                        System.out.println(risposta);
                        break;

                    case 3:
                        System.out.print("Per favore, inserisci id: ");
                        id = Integer.parseInt(input.readLine());
                        risposta = client.sendAndReceiveData(id);
                        if (risposta.contains("registrarti?") || risposta.contains("Vuoi")) {
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
                        client.comandoNonValido();
                }
            }
            client.closeSocket();

        } catch (SocketException ex) {
            System.err.println("Errore all'avvio: " + ex);
        } catch (IOException ex) {
            System.err.println("Errore nell'elaborazione della risposta: " + ex);
        }
    }

}
