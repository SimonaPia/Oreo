package it.uniba.dib.sms2324_16;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;
import android.media.MediaPlayer;

import androidx.fragment.app.Fragment;

import java.util.Locale;

public class gioco2_fragment extends Fragment {

    private TextToSpeech textToSpeech;
    private MediaPlayer applausiMediaPlayer;  // Aggiungi la dichiarazione di MediaPlayer per gli applausi

    public gioco2_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gioco2_fragment, container, false);

        Button buttonAudio = view.findViewById(R.id.buttonAudio);
        RadioButton radioButtonosso = view.findViewById(R.id.radioButton_osso);
        RadioButton radioButtonorso = view.findViewById(R.id.radioButton_orso);
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

        // Inizializza il MediaPlayer per gli applausi
        applausiMediaPlayer = MediaPlayer.create(getActivity(), R.raw.applausi);

        buttonAudio.setOnClickListener(v -> {
            // Converti la stringa in output vocale utilizzando TextToSpeech
            textToSpeech.speak("osso", TextToSpeech.QUEUE_FLUSH, null, null);
        });

        buttonInvioRisposta.setOnClickListener(v -> {
            // Verifica quale RadioButton è selezionato
            if (radioButtonosso.isChecked()) {
                // L'utente ha selezionato l'immagine con l'ID radioButton_sordo
                inviaRisposta("Hai scelto l'immagine osso", true);
                gioco3_fragment gioco3Fragment = new gioco3_fragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.gioco2_fragment, gioco3Fragment)
                        .addToBackStack(null)
                        .commit();

            } else if (radioButtonorso.isChecked()) {
                // L'utente ha selezionato l'immagine con l'ID radioButton_soldo
                inviaRisposta("Hai scelto l'immagine orso", false);
                gioco3_fragment gioco3Fragment = new gioco3_fragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.gioco2_fragment, gioco3Fragment)
                        .addToBackStack(null)
                        .commit();

            } else {
                // Nessun RadioButton selezionato, gestisci l'errore
                Toast.makeText(getActivity(), "Seleziona un'immagine", Toast.LENGTH_SHORT).show();
            }

        });

        return view;
    }

    private void inviaRisposta(String messaggio, boolean isRispostaCorretta) {
        // Invia il feedback vocale e gestisci gli applausi se la risposta è corretta
        inviaFeedback(isRispostaCorretta);

        // Mostra un messaggio all'utente
        Toast.makeText(getActivity(), "Abbiamo inviato la tua risposta al tuo logopedista", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDestroy() {
        // Rilascia le risorse quando il fragment viene distrutto
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        // Rilascia le risorse del MediaPlayer degli applausi
        if (applausiMediaPlayer != null) {
            applausiMediaPlayer.release();
        }

        super.onDestroy();
    }
}
