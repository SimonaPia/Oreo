package it.uniba.dib.sms2324_16;

import static android.content.ContentValues.TAG;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseViewHolder> {
    private List<ExerciseItem> exerciseItemList;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    public ExerciseAdapter(List<ExerciseItem> exerciseItemList) {
        this.exerciseItemList = exerciseItemList;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_esercizi, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        ExerciseItem item = exerciseItemList.get(position);
        int posizione = position;

        holder.childNameTextView.setText("Nome: " + item.getChildName());
        holder.exerciseTypeTextView.setText("Tipo di esercizio: " + item.getExerciseType());

        String completedStatus = item.isExerciseCompleted() ? "Fatto" : "Non fatto";
        holder.completedStatusTextView.setText("Stato: " + completedStatus);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chiamato quando un elemento della RecyclerView viene cliccato
                // Mostra un dialog qui
                showDialog(item, holder, posizione);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exerciseItemList.size();
    }

    private void showDialog(ExerciseItem exerciseItem, @NonNull ExerciseViewHolder holder, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        builder.setTitle("Dettagli dell'esercizio");
        builder.setMessage("Tipo di esercizio: " + exerciseItem.getExerciseType() +
                "\nRisposta: " + exerciseItem.getRisposta());

        View view = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_audio, null);
        Button playButton = view.findViewById(R.id.play);
        Button stopButton = view.findViewById(R.id.stop);
        builder.setView(view);

        // Imposta il listener per il pulsante Play/Stop
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Gestisci l'azione play/stop qui senza chiudere il dialogo
                getFileAudio(holder);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Gestisci l'azione play/stop qui senza chiudere il dialogo
                stopAudio();
            }
        });

        builder.setPositiveButton("Corretto", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setMonete(3, exerciseItem.getIdBambino());
                setDecrementoErrori(5, exerciseItem.getIdBambino());
                inserimentoCorrezione("Corretto", exerciseItem.getIdBambino(), exerciseItem.getRisposta());
                exerciseItem.setCorrection(true);
                exerciseItemList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, exerciseItemList.size() - position);
                // Chiudi il dialog
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Sbagliato", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setIncrementoErrori(5, exerciseItem.getIdBambino());
                inserimentoCorrezione("Sbagliato", exerciseItem.getIdBambino(), exerciseItem.getRisposta());
                exerciseItem.setCorrection(true);
                exerciseItemList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, exerciseItemList.size() - position);
                dialogInterface.dismiss();
            }
        });

        // Mostra il dialog
        builder.show();
    }



    private void getFileAudio(@NonNull ExerciseViewHolder holder) {
        // Inizializza FirebaseStorage
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Sostituisci "il_tuo_file_path" con il percorso del tuo file su Firebase Storage
        String filePath = "z6bM9bqoEXTMQC3C3EPnvD8VbLb2.mp3";

        // Ottieni un riferimento al tuo file
        StorageReference storageRef = storage.getReference().child(filePath);

        // Scarica il file in un percorso locale
        File localFile = null;
        try {
            localFile = File.createTempFile("audio", "3gp");
            File finalLocalFile = localFile;
            storageRef.getFile(localFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Operazione di download riuscita
                        // Puoi ora utilizzare il percorso locale del file per riprodurlo
                        String localFilePath = finalLocalFile.getAbsolutePath();
                        try {
                            playAudio(holder, localFilePath);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        // Ora puoi utilizzare localFilePath per riprodurre il file audio
                    })
                    .addOnFailureListener(exception -> {
                        // Gestisci eventuali errori durante il download
                        // ...
                        Log.e("TAG", "exception" + exception);
                    });
        } catch (IOException e) {
            // Gestisci eventuali errori nella creazione del file locale
            e.printStackTrace();
        }
    }

    private void playAudio(@NonNull ExerciseViewHolder holder, String filePath) throws IOException {
        //stopAudio();
        if (mediaPlayer == null)
        {
            //mediaPlayer = MediaPlayer.create(holder.itemView.getContext(), R.raw.unwritten);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
            // Aggiungi un listener per gestire l'evento di fine riproduzione
            mediaPlayer.setOnCompletionListener(mp -> {
                // Esegui eventuali azioni al termine della riproduzione
                // Ad esempio, rilascia le risorse del MediaPlayer
                mediaPlayer.release();
            });
        }


        mediaPlayer.start();
    }

    private void stopAudio() {
        if (isPlaying) {
            mediaPlayer.stop();
            mediaPlayer.release();
            isPlaying = false;
        }
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

    private void setDecrementoErrori(int incremento, String idBambino) {
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
                            if (document.contains("percentualeerrori"))
                            {
                                valore[0] = Math.toIntExact(document.getLong("percentualeerrori"));
                                valore[0] -= incremento;
                                aggiornaPercentualeErrori(document.getReference(), valore[0]);
                            }
                            else
                            {
                                aggiornaPercentualeErrori(document.getReference(), 0);
                            }

                        }
                    }
                } else {
                    Log.e(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }


    private void setIncrementoErrori(int incremento, String idBambino) {
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
                            if (document.contains("percentualeerrori"))
                            {
                                valore[0] = Math.toIntExact(document.getLong("percentualeerrori"));
                                valore[0] += incremento;
                                aggiornaPercentualeErrori(document.getReference(), valore[0]);
                            }
                            else
                            {
                                aggiornaPercentualeErrori(document.getReference(), incremento);
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void aggiornaPercentualeErrori(DocumentReference documentReference, int nuovoValore) {
        Map<String, Object> data = new HashMap<>();
        data.put("percentualeerrori", nuovoValore); // Sostituisci con il tuo nuovo campo e valore
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

    private void inserimentoCorrezione(String correzione, String idBambino, String risposta) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Ottieni un riferimento al documento che vuoi aggiornare
        CollectionReference collectionReference = db.collection("EserciziSvolti");

        collectionReference.whereEqualTo("id_bambino", idBambino).whereEqualTo("filePath", risposta)
                        .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                // Trovato un documento corrispondente
                                                String idDocumento = document.getId();

                                                // Ora puoi utilizzare l'ID del documento per aggiungere il campo
                                                DocumentReference documentRef = db.collection("EserciziSvolti").document(idDocumento);

                                                Map<String, Object> dati = new HashMap<>();
                                                dati.put("corretto", correzione);
                                                Log.d("TAG", correzione);

                                                documentRef.update(dati)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                // L'aggiornamento è stato eseguito con successo
                                                                Log.d("TAG", "Campo aggiunto con successo!");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                // Si è verificato un errore durante l'aggiornamento
                                                                Log.e("TAG", "Errore durante l'aggiornamento del campo", e);
                                                            }
                                                        });
                                            }
                                        } else {
                                            Log.e("TAG", "Errore durante la query", task.getException());
                                        }
                                    }
                                });
    }
}

