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

public class gioco1_fragment extends Fragment {

    private TextToSpeech textToSpeech;


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

        // Inizializza il MediaPlayer


        buttonAudio.setOnClickListener(v -> {
            // Converti la stringa in output vocale utilizzando TextToSpeech
            textToSpeech.speak("soldo", TextToSpeech.QUEUE_FLUSH, null, null);
        });


        buttonInvioRisposta.setOnClickListener(v -> {
            // Verifica quale RadioButton è selezionato
            if (radioButtonSordo.isChecked()) {
                // L'utente ha selezionato l'immagine con l'ID radioButton_sordo
                inviaRisposta("Hai scelto l'immagine con la mano sorda");
                gioco2_fragment gioco2Fragment = new gioco2_fragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.gioco1_fragment, gioco2Fragment)
                        .addToBackStack(null)
                        .commit();

            } else if (radioButtonSoldo.isChecked()) {
                // L'utente ha selezionato l'immagine con l'ID radioButton_soldo
                inviaRisposta("Hai scelto l'immagine con la mano soldo");
                gioco2_fragment gioco2Fragment = new gioco2_fragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.gioco1_fragment, gioco2Fragment)
                        .addToBackStack(null)
                        .commit();
            }

            else {
                // Nessun RadioButton selezionato, gestisci l'errore
                Toast.makeText(getActivity(), "Seleziona un'immagine", Toast.LENGTH_SHORT).show();
            }

        });

        return view;
    }

    private void inviaRisposta(String messaggio) {
        // Implementa la logica per inviare la risposta al logopedista
        // Esempio: ApiService.inviaRisposta(messaggio);

        // Mostra un messaggio all'utente
        Toast.makeText(getActivity(), "Abbiamo inviato la tua risposta al tuo logopedista", Toast.LENGTH_SHORT).show();
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
