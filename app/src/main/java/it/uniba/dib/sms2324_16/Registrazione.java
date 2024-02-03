package it.uniba.dib.sms2324_16;

public class Registrazione {

        private String risposta, id_bambino, id_genitore, id_logopedista, tipoEsercizio, filePath;

        // Costruttore vuoto richiesto per Firestore
        public Registrazione() {
        }

        public Registrazione(String filePath) {
            this.filePath = filePath;
        }

        public Registrazione(String id_bambino, String id_genitore, String id_logopedista, String tipoEsercizio, String filePath) {
            this.filePath = filePath;
            this.id_bambino = id_bambino;
            this.id_logopedista = id_logopedista;
            this.id_genitore = id_genitore;
            this.tipoEsercizio = tipoEsercizio;
    }

        // Getter e setter
        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }





    public String getRisposta() {
        return risposta;
    }

    public void setRisposta(String risposta) {
        this.risposta = risposta;
    }

    public String getTipoEsercizio() {
        return tipoEsercizio;
    }

    public void setTipoEsercizio(String tipoEsercizio) {
        this.tipoEsercizio = tipoEsercizio;
    }

    public String getId_bambino() {
        return id_bambino;
    }

    public void setId_bambino(String id_bambino) {
        this.id_bambino = id_bambino;
    }

    public String getId_genitore() {
        return id_genitore;
    }

    public void setId_genitore(String id_genitore) {
        this.id_genitore = id_genitore;
    }

    public String getId_logopedista() {
        return id_logopedista;
    }

    public void setId_logopedista(String id_logopedista) {
        this.id_logopedista = id_logopedista;
    }
}
