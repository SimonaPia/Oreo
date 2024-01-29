package it.uniba.dib.sms2324_16;

public class Registrazione {

        private String filePath;

        // Costruttore vuoto richiesto per Firestore
        public Registrazione() {
        }

        public Registrazione(String filePath) {
            this.filePath = filePath;
        }

        // Getter e setter
        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }
