package it.uniba.dib.sms2324_16;


import com.google.firebase.firestore.PropertyName;

public class Users {
    private String nome;
    private String cognome;
    private String tipoUtente;

    public Users() {}
    public Users(String nome, String cognome, String tipoUtente) {
        this.nome = nome;
        this.cognome = cognome;
        this.tipoUtente = tipoUtente;
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


    public void setCognome(String cognome) {
        this.cognome = cognome;
    }


    public void setNome(String nome) {
        this.nome = nome;
    }


    public void setTipoUtente(String tipoUtente) {
        this.tipoUtente = tipoUtente;
    }
}
