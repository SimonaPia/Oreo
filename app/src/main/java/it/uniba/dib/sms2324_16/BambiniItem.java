package it.uniba.dib.sms2324_16;

public class BambiniItem {
    private String childName;
    private String childSurname;
    private boolean isChecked;
    private String idDocumento;

    public BambiniItem(String childName, String childSurname, String idDocumento) {
        this.childName = childName;
        this.childSurname = childSurname;
        isChecked = false;
        this.idDocumento = idDocumento;
    }

    public String getChildName() {
        return childName;
    }

    public String getChildSurname() {
        return childSurname;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getIdDocumento() {
        return idDocumento;
    }
}
