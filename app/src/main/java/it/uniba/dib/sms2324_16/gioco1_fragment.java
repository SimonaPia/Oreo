package it.uniba.dib.sms2324_16;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class gioco1_fragment extends Fragment {

    private TextToSpeech textToSpeech;
    private int monete = 0;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = auth.getCurrentUser();
    private DatabaseReference pazientiRef = FirebaseDatabase.getInstance().getReference().child("Pazienti");

    public gioco1_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gioco1_fragment, container, false);

        Button buttonAudio = view.findViewById(R.id.buttonAudio);
        RadioButton radioButtonSordo = view.findViewById(R.id.radioButton_sordo);
        RadioButton radioButtonSoldo = view.findViewById(R.id.radioButton_soldo);
        Button buttonInvioRisposta = view.findViewById(R.id.buttonInvioRisposta);

        // Inizializza il TextToSpeech
        textToSpeech = new TextToSpeech(getActivity(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int langResult = textToSpeech.setLanguage(Locale.ITALIAN);

                if (langResult == TextToSpeech.LANG_MISSING_DATA ||
                        langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Handle language not supported error
                } else {
                    // TextToSpeech è pronto per l'uso
                    buttonAudio.setEnabled(true);
                }
            } else {
                // Handle TextToSpeech initialization error
            }
        });

        // Imposta il listener per il pulsante Ascolta Vocale
        buttonAudio.setOnClickListener(v -> {
            textToSpeech.speak("soldo", TextToSpeech.QUEUE_FLUSH, null, null);
        });

        // Imposta il listener per il pulsante Invia Risposta
        buttonInvioRisposta.setOnClickListener(v -> {
            if (radioButtonSordo.isChecked()) {
                inviaRisposta("Hai scelto l'immagine con la mano sorda");
                aggiornaMonete(0);

            } else if (radioButtonSoldo.isChecked()) {
                inviaRisposta("Hai scelto l'immagine con la mano soldo");
                aggiornaMonete(2);

            } else {
                Toast.makeText(getActivity(), "Seleziona un'immagine", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void inviaRisposta(String messaggio) {
        Toast.makeText(getActivity(), "Abbiamo inviato la tua risposta al tuo logopedista", Toast.LENGTH_SHORT).show();
        mostraDialog("Complimenti!", "Hai guadagnato 2 monkeys monete. Continua a giocare per guadagnare altri monkeys!");
        aggiornaMonete(2);
    }

    private void mostraDialog(String titolo, String messaggio) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(titolo)
                .setMessage(messaggio)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Chiudi la finestra di dialogo se necessario
                        dialog.dismiss();
                    }
                });
        // Crea e mostra la finestra di dialogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void aggiornaMonete(int incremento) {
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Recupera il valore attuale delle monete dal database
            pazientiRef.child(userId).child("monete").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Verifica se il valore delle monete è nullo
                    Integer moneteAttuali = task.getResult().getValue(Integer.class);

                    if (moneteAttuali == null) {
                        moneteAttuali = 0; // Imposta il valore predefinito se è nullo
                    }

                    // Incrementa le monete e salva nel database
                    moneteAttuali += incremento;
                    pazientiRef.child(userId).child("monete").setValue(moneteAttuali);
                } else {
                    // Gestisci eventuali errori nella lettura dei dati dal database
                    Toast.makeText(requireContext(), "Errore nel recupero delle monete.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // L'utente non è loggato, gestisci l'errore o richiedi il login
            Toast.makeText(requireContext(), "Utente non autenticato.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroy() {
        // Rilascia le risorse di TextToSpeech quando il fragment viene distrutto
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        super.onDestroy();
    }
}
