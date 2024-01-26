package it.uniba.dib.sms2324_16;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class AppointmentManager {
    private CollectionReference appointmentsRef;

    public AppointmentManager() {
        // Inizializza la raccolta principale degli appuntamenti
        this.appointmentsRef = FirebaseFirestore.getInstance().collection("appointments");
    }

    public void addOrUpdateAppointment(FirebaseUser user, Appointment appointment) {
        // Verifica che l'utente e il riferimento agli appuntamenti siano stati inizializzati
        if (user != null && appointmentsRef != null) {
            // Converti l'oggetto Appointment in una mappa
            Map<String, Object> appointmentMap = appointment.toMap();

            // Aggiungi un nuovo appuntamento in Firestore senza specificare un identificatore
            appointmentsRef
                    .document(user.getUid())
                    .collection("logopedistAppointments")  // Usa il nome corretto della sottoraccolta
                    .add(appointmentMap)
                    .addOnSuccessListener(documentReference -> {
                        // L'appuntamento è stato aggiunto con successo
                        String appointmentId = documentReference.getId();
                        // Puoi eventualmente fare qualcosa con l'ID se necessario
                    })
                    .addOnFailureListener(e -> {
                        // Gestisci eventuali errori nell'aggiunta dell'appuntamento
                    });
        } else {
            // Gestione dell'errore o log quando l'utente o il riferimento agli appuntamenti
            // non sono stati inizializzati correttamente
        }
    }

    public void getAppointments(OnAppointmentsLoadedListener listener) {
        // Verifica che l'utente sia autenticato prima di recuperare gli appuntamenti
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && appointmentsRef != null) {
            // Recupera la lista degli appuntamenti da Firestore e notifica l'ascoltatore
            appointmentsRef
                    .document(currentUser.getUid())
                    .collection("logopedistAppointments")  // Usa il nome corretto della sottoraccolta
                    .get()
                    .addOnCompleteListener(task -> {
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
            // Gestione dell'errore o log quando l'utente non è autenticato
        }
    }
}




