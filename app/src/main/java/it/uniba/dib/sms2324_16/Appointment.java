package it.uniba.dib.sms2324_16;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Appointment {
    private Date date;
    private Date time;
    private String patient;

    public Appointment() {
        // Costruttore vuoto richiesto per Firestore
    }

    public Appointment(Date date, Date time, String patient) {
        this.date = date;
        this.time = time;
        this.patient = patient;
    }

    // Getter e setter per gli attributi

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        // Converto le date in timestamp per salvarle in Firestore
        if (date != null) map.put("date", date.getTime());
        if (time != null) map.put("time", time.getTime());
        map.put("patient", patient);
        return map;
    }

    // Rinominato da toObject a toAppointment
    public static Appointment toAppointment(Map<String, Object> map) {
        Appointment appointment = new Appointment();
        appointment.setDate(new Date((long) map.get("date")));
        appointment.setTime(new Date((long) map.get("time")));
        appointment.setPatient((String) map.get("patient"));
        return appointment;
    }

}


