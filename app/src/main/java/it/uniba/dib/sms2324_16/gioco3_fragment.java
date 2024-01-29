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
public class gioco3_fragment extends Fragment {

    private TextToSpeech textToSpeech;
    public gioco3_fragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gioco3_fragment, container, false);

        Button buttonAudio = view.findViewById(R.id.buttonAudio);
        RadioButton radioButtonpacco = view.findViewById(R.id.radioButton_pacco);
        RadioButton radioButtonparco = view.findViewById(R.id.radioButton_parco);
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

        buttonAudio.setOnClickListener(v -> {
            // Converti la stringa in output vocale utilizzando TextToSpeech
            textToSpeech.speak("pacco", TextToSpeech.QUEUE_FLUSH, null, null);
        });

        buttonInvioRisposta.setOnClickListener(v -> {
            // Verifica quale RadioButton è selezionato
            if (radioButtonpacco.isChecked()) {
                // L'utente ha selezionato l'immagine con l'ID radioButton_sordo
                inviaRisposta("Hai scelto l'immagine con la mano sorda");
                gioco4_fragment gioco4Fragment = new gioco4_fragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.gioco3_fragment, gioco4Fragment)
                        .addToBackStack(null)
                        .commit();

            } else if (radioButtonparco.isChecked()) {
                // L'utente ha selezionato l'immagine con l'ID radioButton_soldo
                inviaRisposta("Hai scelto l'immagine con la mano soldo");
                gioco4_fragment gioco4Fragment = new gioco4_fragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.gioco3_fragment, gioco4Fragment)
                        .addToBackStack(null)
                        .commit();

            } else {
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
