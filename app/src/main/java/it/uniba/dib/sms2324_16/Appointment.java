package it.uniba.dib.sms2324_16;
public class Appointment {
    private String date;
    private String time;
    private String patient;
    private String location;

    public Appointment(String date, String time, String patient, String location) {
        this.date = date;
        this.time = time;
        this.patient = patient;
        this.location = location;
    }

    // Getter e setter per 'date'
    public String getDate() {

        return date;
    }

    public void setDate(String date) {

        this.date = date;
    }

    // Getter e setter per 'time'
    public String getTime() {

        return time;
    }

    public void setTime(String time) {

        this.time = time;
    }

    // Getter e setter per 'patient'
    public String getPatient() {

        return patient;
    }

    public void setPatient(String patient) {

        this.patient = patient;
    }

    // Getter e setter per 'location'
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", patient='" + patient + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
