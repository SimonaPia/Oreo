package it.uniba.dib.sms2324_16;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


import java.util.Locale;
public class gioco4_fragment extends Fragment {

    private TextToSpeech textToSpeech;
    private MediaPlayer mediaPlayer;
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

        // Inizializza il MediaPlayer
        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.mancia);

        buttonAudio.setOnClickListener(v -> {
            // Riproduci l'audio quando il pulsante viene cliccato
            playAudio();
        });

        buttonInvioRisposta.setOnClickListener(v -> {
            // Verifica quale RadioButton è selezionato
            if (radioButtonmangia.isChecked()) {
                // L'utente ha selezionato l'immagine con l'ID radioButton_sordo
                inviaRisposta("Hai scelto l'immagine con la mano sorda");

            } else if (radioButtonmancia.isChecked()) {
                // L'utente ha selezionato l'immagine con l'ID radioButton_soldo
                inviaRisposta("Hai scelto l'immagine con la mano soldo");
               

            } else {
                // Nessun RadioButton selezionato, gestisci l'errore
                Toast.makeText(getActivity(), "Seleziona un'immagine", Toast.LENGTH_SHORT).show();
            }

        });

        return view;
    }


    private void playAudio() {
        // Implementa la logica per riprodurre l'audio desiderato
        mediaPlayer.start();

        // Esempio di test con un messaggio vocale
        //String testMessage = "soldo";
        // textToSpeech.speak(testMessage, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void inviaRisposta(String messaggio) {
        // Implementa la logica per inviare la risposta al logopedista
        // Esempio: ApiService.inviaRisposta(messaggio);

        // Mostra un messaggio all'utente
        Toast.makeText(getActivity(), "Abbiamo inviato la tua risposta al tuo logopedista", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        // Rilascia le risorse quando il fragment viene distrutto
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        super.onDestroy();
    }
}
