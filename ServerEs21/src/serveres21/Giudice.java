package serveres21;

import java.util.ArrayList;

/**
 *
 * @author PatrickCumpa
 * @since 10/10/2022
 */
public class Giudice {
    
    private String nome;
    private ArrayList<Integer> voti;
    
    public Giudice(String nome) {
        this.nome = nome;
        this.voti = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ArrayList<Integer> getVoti() {
        return voti;
    }

    public void setVoti(int voto) {
        this.voti.add(voto);
    }
}
