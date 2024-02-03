package it.uniba.dib.sms2324_16;

public class BambiniItem {
    private String childName;
    private String childSurname;
    private boolean isSelected;
    private String idDocumento;

    public BambiniItem(String childName, String childSurname, String idDocumento) {
        this.childName = childName;
        this.childSurname = childSurname;
        isSelected = false;
        this.idDocumento = idDocumento;
    }

    public String getChildName() {
        return childName;
    }

    public String getChildSurname() {
        return childSurname;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getIdDocumento() {
        return idDocumento;
    }
}
