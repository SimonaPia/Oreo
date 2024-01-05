package it.uniba.dib.sms2324_16;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
public class NewAppointmentActivity extends AppCompatActivity {

    private EditText editTextDate;
    private EditText editTextTime;
    private EditText editTextPatient;
    private Spinner spinnerLocation;

    private AppointmentManager appointmentManager;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inserimento_appuntamenti);

        // Inizializza le view
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextPatient = findViewById(R.id.editTextPatient);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        Button buttonSave = findViewById(R.id.buttonSave);

        // Inizializza il gestore degli appuntamenti
        appointmentManager = new AppointmentManager();

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
        String date = editTextDate.getText().toString();
        String time = editTextTime.getText().toString();
        String patient = editTextPatient.getText().toString();
        String location = spinnerLocation.getSelectedItem().toString();

        // Crea un nuovo oggetto appuntamento
        Appointment appointment = new Appointment(date, time, patient, location);

        // Aggiungi o aggiorna l'appuntamento
        appointmentManager.addOrUpdateAppointment(appointment);

        // Stampa un messaggio di conferma
        Toast.makeText(this, "Appuntamento salvato o aggiornato: " + appointment.toString(), Toast.LENGTH_SHORT).show();
    }
}


