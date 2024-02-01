package it.uniba.dib.sms2324_16;

import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class Denominazione1_fragment extends Fragment implements TextToSpeech.OnInitListener {


    private TextToSpeech textToSpeech;
    private String textToRead = "Mulino"; // La stringa da riprodurre
    private int playCount = 0;
    private boolean isRecording = false; // Aggiunto un flag per tracciare lo stato della registrazione
    private static final int MAX_PLAY_COUNT = 3;
    private int monete = 0;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = auth.getCurrentUser();
    private DatabaseReference pazientiRef = FirebaseDatabase.getInstance().getReference().child("Pazienti");
    private static int MICROPHONE_PERMISSION_CODE= 200;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_denominazione1_fragment, container, false);

        FirebaseApp.initializeApp(getActivity());

        // Inizializza i bottoni
        Button buttonAudio = rootView.findViewById(R.id.buttonAudio);
        Button buttonRegistraAudio = rootView.findViewById(R.id.buttonRegistraAudio);
        Button buttonStopAudio = rootView.findViewById(R.id.buttonStopAudio);
        Button buttonPlayAudio = rootView.findViewById(R.id.buttonPlayAudio);
        Button buttonInvioRisposta = rootView.findViewById(R.id.buttonInvioRisposta);


        buttonAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playCount < MAX_PLAY_COUNT) {
                    // Se il numero di ascolti è inferiore al massimo consentito, leggi il testo
                    readText();
                    playCount++;
                } else {
                    // Se il numero di ascolti supera il massimo, mostra un dialogo informativo
                    showMaxPlayCountMessage();
                    mostraDialog("Attenzione!", "Hai superato il numero massimo di ascolti.");
                }
            }
        });

        buttonRegistraAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonRegistraAudioPressed(v);
            }
        });

        buttonStopAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStopAudioPressed(v);
            }
        });

        // Imposta il listener per il pulsante Play
        buttonPlayAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPlayAudioPressed(v);
            }
        });

        // Imposta il listener per il pulsante Invio Risposta
        buttonInvioRisposta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviaRisposta();
                 mostraDialog("Complimenti!", "Hai guadagnato 2 monkeys monete. Continua a giocare per guadagnare altri monkeys!");
                aggiornaMonete(2);
              }
        });


        if(isMicrophonePresent()) {
            getMicrophonePermission();
        }


        textToSpeech = new TextToSpeech(requireContext(), this);

        return rootView;

    }

    private void readText() {
        if (textToSpeech != null) {
            // Puoi controllare la lingua desiderata utilizzando Locale, ad esempio Locale.US per l'inglese
            textToSpeech.setLanguage(Locale.getDefault());
            textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null);
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

      private void showMaxPlayCountMessage() {
        Toast.makeText(requireContext(), "Attenzione, hai superato il numero massimo di ascolti di questa registrazione", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Rilascia le risorse quando il fragment viene distrutto
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
        }
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
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

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // L'inizializzazione è avvenuta con successo
        } else {
            // Gestisci l'errore inizializzazione TextToSpeech
            Toast.makeText(requireContext(), "Errore inizializzazione TextToSpeech", Toast.LENGTH_SHORT).show();
        }
    }

    public void buttonRegistraAudioPressed(View v) {
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(getRecordingFilePath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(requireContext(), "Registrazione avviata", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void buttonStopAudioPressed(View v) {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        Toast.makeText(requireContext(), "Registrazione fermata", Toast.LENGTH_LONG).show();
    }
    public void buttonPlayAudioPressed(View v) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getRecordingFilePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(requireContext(), "Registrazione in corso...", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isMicrophonePresent() {
        if (requireContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            return true;
        } else {
            return false;
        }
    }

    private void getMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE);
        }
    }

    private String getRecordingFilePath(){
        ContextWrapper contextWrapper = new ContextWrapper(requireContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, "testRecordingFile" + ".mp3");
        return file.getPath();
    }
}
