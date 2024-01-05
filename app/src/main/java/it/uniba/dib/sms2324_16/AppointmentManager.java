package it.uniba.dib.sms2324_16;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
public class AppointmentManager {

    private final List<Appointment> appointments;

    public AppointmentManager() {

        this.appointments = new ArrayList<>();
    }

    public void addOrUpdateAppointment(Appointment appointment) {
        if (appointments.contains(appointment)) {
            appointments.remove(appointment);  // Rimuovi l'appuntamento esistente
        }
        appointments.add(appointment);  // Aggiungi l'appuntamento aggiornato o nuovo
    }

    public List<Appointment> getAppointments() {
        // Ordina gli appuntamenti prima di restituirli (se necessario)
        Collections.sort(appointments, new Comparator<Appointment>() {
            @Override
            public int compare(Appointment a1, Appointment a2) {
                // Implementa la logica di confronto tra appuntamenti basata su data e ora
                // Restituisci un numero negativo se a1 precede a2, positivo se a1 segue a2, 0 se sono uguali
                // ...
                return 0;
            }
        });

        return appointments;
    }

    public boolean addAppointment(Appointment appointment) {
        if (!appointments.contains(appointment)) {
            appointments.add(appointment);
            return true;
        } else {
            // L'appuntamento è già presente
            return false;
        }
    }
}

