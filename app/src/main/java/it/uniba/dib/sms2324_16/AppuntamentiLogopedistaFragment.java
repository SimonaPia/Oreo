package it.uniba.dib.sms2324_16;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class AppuntamentiLogopedistaFragment extends Fragment {
Appointment appointmentManager = new Appointment();
NewAppointmentActivity newAppointmentActivity = new NewAppointmentActivity();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String logopedistId;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ottieni le referenze alle viste dal layout
        CalendarView calendarView = view.findViewById(R.id.calendarView3);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        // Imposta il listener per la selezione della data
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                // Chiamato quando un giorno è selezionato

                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    showAppointmentDialog(userId, year, month, dayOfMonth);
                }
            }
        });
    }

    private void showAppointmentDialog(String userId, int year, int month, int dayOfMonth) {
        // Converti la data selezionata nel formato timestamp UNIX
        TimeZone timeZoneUTC = TimeZone.getTimeZone("UTC");
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(Calendar.YEAR, year);
        selectedCalendar.set(Calendar.MONTH, month);
        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        // Imposta l'orario a mezzanotte per ottenere la data corretta
        selectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
        selectedCalendar.set(Calendar.MINUTE, 0);
        selectedCalendar.set(Calendar.SECOND, 0);
        selectedCalendar.set(Calendar.MILLISECOND, 0);

// Converti la data selezionata nel formato timestamp UNIX
        long timestampMillis = selectedCalendar.getTimeInMillis();  // Ottieni il timestamp in millisecondi
        Log.d("ShowAppointmentDialog", "UserId: " + userId + "data selezionata" + timestampMillis);

        db.collection("appointments")
                .document(userId)
                .collection("logopedistAppointments")
                .whereEqualTo("date", timestampMillis)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firestore", "Query successful. Results: " + task.getResult().size());
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Resto del codice per ottenere i dati dell'appuntamento
                            String paziente = document.getString("patient");
                            String genitore = document.getString("genitore");
                            long timeNumber = document.getLong("time");

                            Date time = new Date(timeNumber);

                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                            String formattedTime = timeFormat.format(time);

                            Log.d("Firestore", "Paziente: " + paziente + ", Formatted Time: " + formattedTime);

                            showDialog(paziente, formattedTime, selectedCalendar,genitore);
                        }
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                        Toast.makeText(requireContext(), "Errore durante il recupero degli appuntamenti", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDialog(String paziente, String ora, Calendar selectedCalendar,String genitore) {
        // Puoi personalizzare il layout della finestra di dialogo come preferisci
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dettagli_appuntamenti, null);

        // Trova le viste nel layout della finestra di dialogo
        TextView pazienteTextView = dialogView.findViewById(R.id.pazienteTextView);
        TextView oraTextView = dialogView.findViewById(R.id.oraTextView);
        TextView genitoreTextView = dialogView.findViewById(R.id.genitoreTextView);
        // Imposta i dati nei campi appropriati
        genitoreTextView.setText("Genitore: " + genitore);
        pazienteTextView.setText("Paziente: " + paziente);
        oraTextView.setText("Ora: " + ora);


        // Inizializza e mostra la finestra di dialogo
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle("Appuntamento del " + formatDate(selectedCalendar))
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .create();

        alertDialog.show();
    }

    private String formatDate(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_appuntamenti_logopedista, container, false);
        Button bottoneModifica = rootView.findViewById(R.id.button3);
        ImageView buttonWithIcon = rootView.findViewById(R.id.buttonWithIcon);
        ImageView topLeftIcon = rootView.findViewById(R.id.topLeftIcon);

        bottoneModifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia l'ExerciseListActivity quando il pulsante viene premuto
                Intent intent = new Intent(getActivity(), NewAppointmentActivity.class);
                startActivity(intent);
            }
        });
        buttonWithIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia l'ExerciseListActivity quando il pulsante viene premuto
                Intent intent = new Intent(getActivity(), NewAppointmentActivity.class);
                startActivity(intent);
            }
        });
        topLeftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);


                    // Esegui la navigazione solo se non sei già nel fragment di destinazione
                    navController.navigate(R.id.homepage_logopedista_fragment);

            }
        });

        Log.d("FragmentLifecycle", "onCreateView called");
        Toast.makeText(requireContext(), "onCreateView called", Toast.LENGTH_SHORT).show();
        return rootView;
    }

}