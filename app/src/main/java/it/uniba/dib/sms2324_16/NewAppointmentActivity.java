package it.uniba.dib.sms2324_16;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewAppointmentActivity extends AppCompatActivity {
    private static final int REQUEST_SELECT_GENITORE = 1;
    private EditText editTextDate;
    private EditText editTextTime;
    private EditText editTextPatient;
    private EditText editTextGenitore;

    private AppointmentManager appointmentManager;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inserimento_appuntamenti);

        // Inizializza le view
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextPatient = findViewById(R.id.editTextPatient);
        editTextGenitore = findViewById(R.id.editTextGenitore);
        Button buttonSave = findViewById(R.id.buttonSave);

        // Inizializza il gestore degli appuntamenti
        appointmentManager = new AppointmentManager();
        //appointmentManager.setLogopedistId("il_tuo_id_del_logopedista");  // Sostituisci con l'ID effettivo

        // Aggiungi un listener al pulsante Salva
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveOrUpdateAppointment();
            }
        });


    }


    private void saveOrUpdateAppointment() {
        // Recupera i dati inseriti dall'utente
        String dateString = editTextDate.getText().toString();
        String timeString = editTextTime.getText().toString();  // Stringa di tempo nel formato "HH:mm"
        String patient = editTextPatient.getText().toString();
        String genitore = editTextGenitore.getText().toString();

        // Converti le stringhe di data e tempo in oggetti Date
        Date date = convertStringToDate(dateString);
        Date time = convertStringToTime(timeString);

        // Crea un nuovo oggetto appuntamento senza il campo "location"
        Appointment appointment = new Appointment(date, time, patient,genitore);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Imposta l'ID del logopedista se necessario
            // appointmentManager.setLogopedistId("il_tuo_id_del_logopedista");

            // Aggiungi o aggiorna l'appuntamento utilizzando il nuovo metodo
            appointmentManager.addOrUpdateAppointment(currentUser, appointment);

            // Stampa un messaggio di conferma
            Toast.makeText(this, "Appuntamento salvato o aggiornato: " + appointment.toString(), Toast.LENGTH_SHORT).show();
        } else {
            // Gestione caso in cui l'utente non è autenticato
        }
    }


    // Funzione per convertire una stringa di data nel formato "dd/MM/yyyy" in un oggetto Date
    public static Date convertStringToDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Funzione per convertire una stringa di tempo nel formato "HH:mm" in un oggetto Date
    public static Date convertStringToTime(String timeString) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            return format.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}





