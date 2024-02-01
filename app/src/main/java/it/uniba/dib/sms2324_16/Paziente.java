package it.uniba.dib.sms2324_16;
import android.util.Log;
public class Paziente {
    private String cognome;
    private int giornigiochi;
    private String nome;
    private int percentualeerrori;
    private int posizioneclassifica;
    private String id;


    public Paziente() {
        // Costruttore vuoto
    }
    public Paziente(String nome, String cognome) {
        this.nome = nome;
        this.cognome = cognome;
    }

    public Paziente(String id, String nome, String cognome) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        Log.d("PazienteConstructor", "Paziente ID: " + id);
    }


    public String getId() {
        return id;
    }
    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public int getGiornigiochi() {
        return giornigiochi;
    }

    public void setGiornigiochi(int giornigiochi) {
        this.giornigiochi = giornigiochi;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getPercentualeerrori() {
        return percentualeerrori;
    }

    public void setPercentualeerrori(int percentualeerrori) {
        this.percentualeerrori = percentualeerrori;
    }

    public int getPosizioneclassifica() {
        return posizioneclassifica;
    }

    public void setPosizioneclassifica(int posizioneclassifica) {
        this.posizioneclassifica = posizioneclassifica;
    }
}
