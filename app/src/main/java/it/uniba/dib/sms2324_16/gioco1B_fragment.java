package it.uniba.dib.sms2324_16;

import static android.content.ContentValues.TAG;

import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.media.MediaRecorder;
import android.os.Environment;
import java.io.IOException;
import java.io.File;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class gioco1B_fragment extends Fragment {
     MediaRecorder mediaRecorder;
     MediaPlayer mediaPlayer;
    private TextToSpeech textToSpeech;
    private String audioFilePath;  // Percorso del file audio registrato

    private int monete = 0;
    private static int MICROPHONE_PERMISSION_CODE= 200;

    private String textToRead = "treno, triste, trucco"; // La stringa da riprodurre
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = auth.getCurrentUser();
    private DatabaseReference pazientiRef = FirebaseDatabase.getInstance().getReference().child("Pazienti");

    private boolean isRecording = false;

    public gioco1B_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gioco1b_fragment, container, false);
        audioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio_record.3gp";

        // Inizializza i bottoni
        Button buttonAudio = rootView.findViewById(R.id.buttonAudio);
        Button buttonRegistraAudio = rootView.findViewById(R.id.buttonRegistraAudio);
        Button buttonStopAudio = rootView.findViewById(R.id.buttonStopAudio);
        Button buttonPlayAudio = rootView.findViewById(R.id.buttonPlayAudio);
        Button buttonInvioRisposta = rootView.findViewById(R.id.buttonInvioRisposta);

        // Inizializza il TextToSpeech
        textToSpeech = new TextToSpeech(requireContext(), status -> {
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

        if (currentUser != null) {
            String userId = currentUser.getUid();
        }

        // Imposta il listener per il pulsante Ascolta Vocale
        buttonAudio.setOnClickListener(v -> {
            playAudio(textToRead);
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

        // Imposta il listener per il pulsante Registra
        buttonInvioRisposta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviaRisposta();
                mostraDialog("Complimenti!", "Hai guadagnato 2 monkeys monete. Continua a giocare per guadagnare altri monkeys!");
                aggiornaMonete(2);
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                    String uID = user.getUid();
                setMonete(2, uID);
            }
        });


        if(isMicrophonePresent()) {
            getMicrophonePermission();
        }


        return rootView;
    }

    private void setMonete(int incremento, String idBambino) {
        // Inizializza Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Ottieni un riferimento al documento che vuoi aggiornare
        CollectionReference collectionReference = db.collection("Pazienti");

        final int[] valore = new int[1];
        // Recupera i dati del documento
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        String idDocumento = document.getId();
                        idDocumento = idDocumento.replaceAll("\\s", "");

                        if (idDocumento.equals(idBambino))
                        {
                            valore[0] = Math.toIntExact(document.getLong("monete"));
                            valore[0] += incremento;
                            aggiornaMonete(document.getReference(), valore[0]);
                        }
                    }
                } else {
                    Log.e(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void aggiornaMonete(DocumentReference documentReference, int nuovoValore) {
        Map<String, Object> data = new HashMap<>();
        data.put("monete", nuovoValore); // Sostituisci con il tuo nuovo campo e valore
        documentReference.update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Aggiornamento riuscito
                        Log.d("TAG", "Campo aggiunto con successo!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@org.checkerframework.checker.nullness.qual.NonNull Exception e) {
                        // Errore durante l'aggiornamento
                        Log.w("TAG", "Errore durante l'aggiornamento del campo", e);
                    }
                });
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

    private void playAudio(String textToRead) {
        // Converti la stringa in output vocale utilizzando TextToSpeech
        textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private String getOutputFile() {
        // Sostituisci con il percorso desiderato. le registrazioni ATTUALMENTE vengono salvate nella directory di cache esterna dell'applicazione
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uID = user.getUid();
        File audioFile = new File(requireContext().getExternalCacheDir(), (uID + ".mp3"));
        return audioFile.getAbsolutePath();
    }

    private void inviaRisposta() {
        // Simula l'invio della registrazione al logopedista
        Toast.makeText(requireContext(), "Invio registrazione al tuo logopedista", Toast.LENGTH_SHORT).show();


        String filePath = getOutputFile();

        // Invia il file audio a Firebase Firestore
        inviaFileAudioAFirestore(filePath);
        uploadFileAudio();
    }

    private File getRecordingFile(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uID = user.getUid();
        ContextWrapper contextWrapper = new ContextWrapper(requireContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        String filePath = uID + ".mp3";
        Log.d("TAG", "filePath: " + filePath);
        File file = new File(musicDirectory, filePath);
        return file;
    }

    private void uploadFileAudio() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        Uri file = Uri.fromFile(getRecordingFile());
        StorageReference riversRef = storageRef.child(file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                getIdGenitore();
            }
        });
    }

    private void getIdGenitore() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null)
        {
            String uID = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Bambino - Genitore").whereEqualTo("id_bambino1", uID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful())
                    {
                        for (QueryDocumentSnapshot document : task.getResult())
                        {
                            Log.d("TAG", "risultato query " + task.getResult().size());
                            String idGenitore = document.getString("id_genitore");
                            getIdLogopedista(idGenitore);
                        }

                    }
                }
            });
        }
    }

    private void getIdLogopedista(String idGenitore) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null)
        {
            String uID = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Bambino - Logopedista").whereEqualTo("id_bambino", uID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful())
                    {
                        for (QueryDocumentSnapshot document : task.getResult())
                        {
                            Log.d("TAG", "risultato query " + task.getResult().size());
                            String idLogopedista = document.getString("id_logopedista");
                            inviaFileAudio(idGenitore, idLogopedista);
                        }

                    }
                }
            });
        }
    }

    private void inviaFileAudio(String idGenitore, String idLogopedista) {
        // Ottieni l'istanza di FirebaseFirestore
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Crea un oggetto Map con i dati da salvare nel database
        Map<String, Object> data = new HashMap<>();
        data.put("id_bambino", currentUser.getUid()); // Aggiungi l'ID dell'utente
        data.put("id_genitore", idGenitore);
        data.put("id_logopedista", idLogopedista);
        data.put("tipoEsercizio", "Audio");
        data.put("risposta", getRecordingFilePath());


        // Creare una nuova raccolta chiamata "registrazioni" (puoi cambiarla a seconda delle tue esigenze)
        // con un documento univoco per ogni registrazione
        db.collection("EserciziSvolti")
                .add(new Registrazione(currentUser.getUid(), idGenitore, idLogopedista, "Audio", getRecordingFilePath()))
                .addOnSuccessListener(documentReference -> {
                    // Operazione di invio riuscita
                    Toast.makeText(requireContext(), "Registrazione inviata con successo a Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Gestisci l'errore se l'invio fallisce
                    Toast.makeText(requireContext(), "Errore durante l'invio della registrazione a Firestore", Toast.LENGTH_SHORT).show();
                });
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
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uID = user.getUid();
        ContextWrapper contextWrapper = new ContextWrapper(requireContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, uID + ".mp3");
        return file.getPath();
    }

}

