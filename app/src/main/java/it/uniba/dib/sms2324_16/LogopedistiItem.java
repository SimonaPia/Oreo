package it.uniba.dib.sms2324_16;

public class LogopedistiItem {
    private String idLogopedista;
    private boolean isSelected;
    private String logopedistaName;

    public LogopedistiItem(String logopedistaName, String idLogopedista) {
        this.logopedistaName = logopedistaName;
        isSelected = false;
        this.idLogopedista = idLogopedista;
    }

    public String getLogopedistaName() {
        return logopedistaName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getIdLogopedista() {
        return idLogopedista;
    }
}
