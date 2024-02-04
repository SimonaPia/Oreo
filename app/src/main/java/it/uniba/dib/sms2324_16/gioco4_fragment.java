package it.uniba.dib.sms2324_16;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.media.MediaPlayer;

import java.util.Locale;
public class gioco4_fragment extends Fragment {

    private TextToSpeech textToSpeech;
    private int monete = 0;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = auth.getCurrentUser();
    private DatabaseReference pazientiRef = FirebaseDatabase.getInstance().getReference().child("Pazienti");

    MediaPlayer applausiMediaPlayer = MediaPlayer.create(getActivity(), R.raw.applausi);

    public gioco4_fragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gioco4_fragment, container, false);

        Button buttonAudio = view.findViewById(R.id.buttonAudio);
        RadioButton radioButtonmangia = view.findViewById(R.id.radioButton_mangia);
        RadioButton radioButtonmancia = view.findViewById(R.id.radioButton_mancia);
        Button buttonInvioRisposta = view.findViewById(R.id.buttonInvioRisposta);

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Gioco 4");
        }

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



        buttonAudio.setOnClickListener(v -> {
            // Converti la stringa in output vocale utilizzando TextToSpeech
            textToSpeech.speak("mancia", TextToSpeech.QUEUE_FLUSH, null, null);
        });

        buttonInvioRisposta.setOnClickListener(v -> {
            // Verifica quale RadioButton è selezionato
            if (radioButtonmangia.isChecked()) {
                // L'utente ha selezionato l'immagine con l'ID radioButton_sordo
                aggiornaMonete(2);
                inviaRisposta("Hai scelto l'immagine con la mano sorda");


            } else if (radioButtonmancia.isChecked()) {
                // L'utente ha selezionato l'immagine con l'ID radioButton_soldo
                inviaRisposta("Hai scelto l'immagine con la mano soldo");
                aggiornaMonete(2);


            } else {
                // Nessun RadioButton selezionato, gestisci l'errore
                Toast.makeText(getActivity(), "Seleziona un'immagine", Toast.LENGTH_SHORT).show();
            }

        });

        return view;
    }

    // Metodo per mostrare la finestra di dialogo
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

    private void inviaRisposta(String messaggio) {
        // Implementa la logica per inviare la risposta al logopedista
        // Esempio: ApiService.inviaRisposta(messaggio);
        // Implementa la logica per verificare la correttezza della risposta
        boolean isRispostaCorretta = verificaCorrettezzaRisposta(messaggio);

        // Invia il feedback vocale e gestisci gli applausi se la risposta è corretta
        inviaFeedback(isRispostaCorretta);

        // Mostra un messaggio all'utente
        mostraDialog("Complimenti!", "Hai guadagnato 2 monkeys monete. Continua a giocare per guadagnare altri monkeys!");
        aggiornaMonete(2);
        if (isRispostaCorretta) {
            Toast.makeText(getActivity(), "Ben fatto! Risposta corretta.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Riceverai la correzione dal Logopedista.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean verificaCorrettezzaRisposta(String rispostaUtente) {
        // Implementa la tua logica per verificare se la risposta dell'utente è corretta
        // Restituisce true se la risposta è corretta, altrimenti false
        // Esempio: return rispostaUtente.equalsIgnoreCase("risposta_corretta");
        return rispostaUtente.equalsIgnoreCase("Hai scelto l'immagine con la mano soldo");
    }

    private void inviaFeedback(boolean isRispostaCorretta) {
        // Output vocale in base alla correttezza della risposta
        String feedbackVocale;
        if (isRispostaCorretta) {
            feedbackVocale = "Ben fatto! Risposta corretta.";
            // Riproduci gli applausi quando la risposta è corretta
            playApplausi();
        } else {
            feedbackVocale = "Riceverai la correzione dal Logopedista.";
        }

        // Converti la stringa in output vocale utilizzando TextToSpeech
        textToSpeech.speak(feedbackVocale, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void playApplausi() {
        if (applausiMediaPlayer != null) {
            applausiMediaPlayer.start();
        } else {
            // Inizializza il MediaPlayer per gli applausi solo se è null
            applausiMediaPlayer = MediaPlayer.create(getActivity(), R.raw.applausi);
            applausiMediaPlayer.start();
        }
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
