package it.uniba.dib.sms2324_16;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class AppointmentManager {
    private CollectionReference appointmentsRef;
    private String logopedistId;

    public AppointmentManager() {
        // Inizializzazione degli attributi senza l'ID del logopedista
        this.logopedistId = null;
        this.appointmentsRef = null;
    }

    public void initialize(String logopedistId) {
        // Inizializzazione degli attributi con l'ID del logopedista fornito
        this.logopedistId = logopedistId;
        this.appointmentsRef = FirebaseFirestore.getInstance()
                .collection("InserimentoAppuntamento")
                .document(logopedistId)
                .collection("logopedistAppointments");
    }

    public void addOrUpdateAppointment(Appointment appointment) {
        // Verifica che l'ID del logopedista sia stato inizializzato prima di aggiungere un appuntamento
        if (logopedistId != null && appointmentsRef != null) {
            // Converti l'oggetto Appointment in una mappa
            Map<String, Object> appointmentMap = appointment.toMap();

            // Aggiungi o aggiorna l'appuntamento in Firestore
            appointmentsRef.add(appointmentMap);
        } else {
            // Gestione dell'errore o log quando l'ID del logopedista non è stato inizializzato correttamente
            // Potresti lanciare un'eccezione, stampare un messaggio di log, o gestire la situazione in altro modo.
        }
    }

    public void getAppointments(OnAppointmentsLoadedListener listener) {
        // Verifica che l'ID del logopedista sia stato inizializzato prima di recuperare gli appuntamenti
        if (logopedistId != null && appointmentsRef != null) {
            // Recupera la lista degli appuntamenti da Firestore e notifica l'ascoltatore
            appointmentsRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Appointment> appointments = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        // Converti la mappa in un oggetto Appointment
                        Appointment appointment = document.toObject(Appointment.class);
                        appointments.add(appointment);
                    }
                    listener.onAppointmentsLoaded(appointments);
                } else {
                    listener.onAppointmentsLoadError(task.getException());
                }
            });
        } else {
            // Gestione dell'errore o log quando l'ID del logopedista non è stato inizializzato correttamente
            // Potresti lanciare un'eccezione, stampare un messaggio di log, o gestire la situazione in altro modo.
        }
    }

    // Aggiungi una funzione per impostare dinamicamente l'ID del logopedista
    public void setLogopedistId(String logopedistId) {
        this.logopedistId = logopedistId;
        this.appointmentsRef = FirebaseFirestore.getInstance()
                .collection("appointments")
                .document(logopedistId)
                .collection("logopedistAppointments");
    }
}




