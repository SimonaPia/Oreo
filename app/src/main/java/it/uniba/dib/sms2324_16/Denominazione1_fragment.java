package it.uniba.dib.sms2324_16;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class Denominazione1_fragment extends Fragment {

    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;


    public Denominazione1_fragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_denominazione1_fragment, container, false);

        FirebaseApp.initializeApp(getActivity());


        // Inizializza i bottoni
        Button buttonAudio = rootView.findViewById(R.id.buttonAudio);
        Button buttonRegistraAudio = rootView.findViewById(R.id.buttonRegistraAudio);
        Button buttonInvioRisposta = rootView.findViewById(R.id.buttonInvioRisposta);

        // Imposta il listener per il pulsante Ascolta Vocale
        buttonAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio();
            }
        });

        // Imposta il listener per il pulsante Registra
        buttonRegistraAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });

        // Imposta il listener per il pulsante Invia Risposta
        buttonInvioRisposta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviaRisposta();
            }
        });

        return rootView;
    }

    private void playAudio() {
        // Riproduci l'audio
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.treno_triste_trucco); // Sostituisci con la tua risorsa audio
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                // Aggiungi un ritardo prima di iniziare la riproduzione successiva (ad esempio, 1 secondo)
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        playAudio();
                    }
                }, 1000);
            }
        });
        mediaPlayer.start();
    }

    private void startRecording() {
        // Avvia la registrazione
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(getOutputFile());

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getOutputFile() {
        // Sostituisci con il percorso desiderato. le registrazioni ATTUALMENTE vengono salvate nella directory di cache esterna dell'applicazione
        File audioFile = new File(requireContext().getExternalCacheDir(), "audio_record.3gp");
        return audioFile.getAbsolutePath();
    }

    private void inviaRisposta() {
        // Simula l'invio della registrazione al logopedista
        Toast.makeText(requireContext(), "Invio registrazione al tuo logopedista", Toast.LENGTH_SHORT).show();

        String filePath = getOutputFile();

        // Invia il file audio a Firebase Firestore
        inviaFileAudioAFirestore(filePath);
    }
    private void inviaFileAudioAFirestore(String filePath) {
        // Ottieni l'istanza di FirebaseFirestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Creare una nuova raccolta chiamata "registrazioni" (puoi cambiarla a seconda delle tue esigenze)
        // con un documento univoco per ogni registrazione
        db.collection("registrazioni")
                .add(new Registrazione(filePath))
                .addOnSuccessListener(documentReference -> {
                    // Operazione di invio riuscita
                    Toast.makeText(requireContext(), "Registrazione inviata con successo a Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Gestisci l'errore se l'invio fallisce
                    Toast.makeText(requireContext(), "Errore durante l'invio della registrazione a Firestore", Toast.LENGTH_SHORT).show();
                });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Rilascia le risorse quando il fragment viene distrutto
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
        }
    }
}
