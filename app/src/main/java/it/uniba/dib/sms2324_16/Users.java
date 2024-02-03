package it.uniba.dib.sms2324_16;


import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;

public class Users {
    private String nome;
    private String cognome;
    private String tipoUtente;
    private ArrayList<String> bambini;

    public Users() {}
    public Users(String nome, String cognome, String tipoUtente) {
        this.nome = nome;
        this.cognome = cognome;
        this.tipoUtente = tipoUtente;
    }
    public Users(String nome, String cognome) {
        this.nome = nome;
        this.cognome = cognome;
    }

    public String getCognome() {
        return cognome;
    }
    public String getNome() {
        return nome;
    }
    public String getTipoUtente() {
        return tipoUtente;
    }
    public ArrayList<String> getBambini() {
        return bambini;
    }
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setTipoUtente(String tipoUtente) {
        this.tipoUtente = tipoUtente;
    }
    public void setBambini(ArrayList<String> bambini){
        this.bambini = bambini;
    }
}
