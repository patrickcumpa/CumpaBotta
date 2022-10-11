package es21;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author PatrickCumpa
 * @since 10/10/2022
 */
public class Giudice implements Serializable {

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

    @Override
    public String toString() {
        return nome + ", " + voti.get(0) + ", " + voti.get(1) + ", " + voti.get(2) + ", " + voti.get(3);
    }
}
