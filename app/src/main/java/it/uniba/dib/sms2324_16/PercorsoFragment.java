package it.uniba.dib.sms2324_16;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PercorsoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PercorsoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String[] paroleDaConfrontare = {
            "Riconoscimento coppie minime n.1",
            "Riconoscimento coppie minime n.2",
            "Riconoscimento coppie minime n.3",
            "Riconoscimento coppie minime n.4",
            "ripeti la sequenza di parole",
            "riconosci la parola associata all'immagine"
    };

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GameThread gameThread;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private ImageView personaggio1;
    private ImageView personaggio2;
    private ImageView personaggio3;
    private ImageView personaggio4;
    private TextView numeroMonete;
    private String personaggioScelto = "";
    private boolean personaggioAttivo1 = false, personaggioAttivo2 = false, personaggioAttivo3 = false, personaggioAttivo4 = false;

    public PercorsoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomePageBambinoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PercorsoFragment newInstance(String param1, String param2) {
        PercorsoFragment fragment = new PercorsoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_percorso, container, false);

        surfaceView = view.findViewById(R.id.surfaceView);
        personaggio1 = view.findViewById(R.id.personaggio1);
        personaggio2 = view.findViewById(R.id.personaggio2);
        personaggio3 = view.findViewById(R.id.personaggio3);
        personaggio4 = view.findViewById(R.id.personaggio4);

        surfaceHolder = surfaceView.getHolder();
        gameThread = new GameThread(surfaceHolder, requireContext());
        //surfaceView.setOnTouchListener(this);

        setPersonaggi();
        movimentoPersonaggi();
        //visualizzare il numero delle  monete per ogni paziente
        numeroMonete = view.findViewById(R.id.numeroMonete);
        caricaNumeroMonete();

        gameThread = new GameThread(surfaceHolder, requireContext());
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (gameThread.getState() == Thread.State.NEW) {
                    gameThread.setCanvasSize(holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height());
                    gameThread.start();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                // Handle surface changes if needed
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                gameThread.setRunning(false);
                try {
                    gameThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    /*@Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float touchX = motionEvent.getX();
        float touchY = motionEvent.getY();

        // Verifica se il tocco è avvenuto all'interno di uno degli ovali
        for (int i = 0; i < gameThread.getZigzagPath().size(); i++) {
            Point ovalCenter = gameThread.getZigzagPath().get(i);
            float ovalLeft = ovalCenter.x - 60;
            float ovalTop = ovalCenter.y - 50;
            float ovalRight = ovalCenter.x + 60;
            float ovalBottom = ovalCenter.y + 50;

            if (touchX >= ovalLeft && touchX <= ovalRight && touchY >= ovalTop && touchY <= ovalBottom) {
                // Il tocco è avvenuto all'interno dell'ovale, esegui l'azione desiderata
                // Ad esempio, visualizza un messaggio o avvia un'attività
                Toast.makeText(requireContext(), "Hai toccato l'ovale " + (i + 1), Toast.LENGTH_SHORT).show();
                return true; // Consuma l'evento di tocco
            }
        }

        // Il tocco non è avvenuto all'interno di nessun ovale
        return false;
    }*/

    //prende il numero di monete dal db e le fa visualizzae in questa schermata
    private void caricaNumeroMonete() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = db.collection("Pazienti");

            // Esegui una query filtrata per ottenere i documenti associati all'utente attualmente loggato
            collectionReference.document(userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Ottieni il valore del campo "monete"
                                    Long monete = document.getLong("monete");

                                    // Aggiorna la TextView con il numero di monete
                                    numeroMonete.setText(String.valueOf(monete));

                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.e(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    protected void messaggio(String msg) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setPersonaggi() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = db.collection("SceltaScenario");

            // Esegui una query filtrata per ottenere i documenti associati all'utente attualmente loggato
            collectionReference.whereEqualTo("id_bambino", userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    String scenario = document.getString("SceltaScenario");

                                    if (scenario.equals("Savana"))
                                    {
                                        personaggio1.setImageResource(R.drawable.personaggio_savana_elefante);
                                        personaggio2.setImageResource(R.drawable.personaggio_savana_leone);
                                        personaggio3.setImageResource(R.drawable.personaggio_savana_leopardo);
                                        personaggio4.setImageResource(R.drawable.personaggio_savana_scimmia);
                                    }

                                    if (scenario.equals("Mondo incantato"))
                                    {
                                        personaggio1.setImageResource(R.drawable.personaggio_fatato_1);
                                        personaggio2.setImageResource(R.drawable.personaggio_fatato_2);
                                        personaggio3.setImageResource(R.drawable.personaggio_fatato_3);
                                        personaggio4.setImageResource(R.drawable.personaggio_fatato_4);
                                    }

                                    if (scenario.equals("Pirata"))
                                    {
                                        personaggio1.setImageResource(R.drawable.personaggio_pirata_1);
                                        personaggio2.setImageResource(R.drawable.personaggio_pirata_2);
                                        personaggio3.setImageResource(R.drawable.personaggio_pirata_3);
                                        personaggio4.setImageResource(R.drawable.personaggio_pirata_4);
                                    }

                                    if (scenario.equals("Fiabesco"))
                                    {
                                        personaggio1.setImageResource(R.drawable.personaggio_fiabesco_1);
                                        personaggio2.setImageResource(R.drawable.personaggio_fiabesco_2);
                                        personaggio3.setImageResource(R.drawable.personaggio_fiabesco_3);
                                        personaggio4.setImageResource(R.drawable.personaggio_fiabesco_4);
                                    }

                                    personaggioScelto();
                                }
                            } else {
                                Log.e(TAG, "Error getting documents: ", task.getException());
                                messaggio("Il tuo genitore non ha ancora scelto lo scenario per te!");
                            }
                        }
                    });
        }
    }

    private void showDialog(String titolo, String messaggio) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(titolo);
        builder.setMessage(messaggio);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Chiudi il dialog
                dialog.dismiss();
            }
        });
    }

    private void setMonete() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
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

                            if (idDocumento.equals(userId)) {
                                valore[0] = Math.toIntExact(document.getLong("monete"));

                                if ((valore[0] > 5 && valore[0] <= 10) || (valore[0] > 10 && valore[0] <= 20) || (valore[0] > 20 && valore[0] <= 30))
                                {
                                    if (personaggioAttivo1)
                                    {
                                        if (personaggioAttivo2)
                                        {
                                            if (personaggioAttivo3)
                                            {
                                                if (personaggioAttivo4)
                                                {
                                                    showDialog("Personaggi completati!", "Hai sbloccato tutti i personaggi!\nControlla la sezione RISCUOTI PREMI per collezionare altre ricompense!");
                                                }
                                                else
                                                {
                                                    showDialog("Complimenti!", "Hai sbloccato un altro personaggio!");
                                                    personaggio4.setColorFilter(null);
                                                    personaggioAttivo4 = true;
                                                }
                                            }
                                            else
                                            {
                                                showDialog("Complimenti!", "Hai sbloccato un altro personaggio!");
                                                personaggio3.setColorFilter(null);
                                                personaggioAttivo3 = true;
                                            }
                                        }
                                        else
                                        {
                                            showDialog("Complimenti!", "Hai sbloccato un altro personaggio!");
                                            personaggio2.setColorFilter(null);
                                            personaggioAttivo2 = true;
                                        }
                                    }
                                    else
                                    {
                                        showDialog("Complimenti!", "Hai sbloccato un altro personaggio!");
                                        personaggio1.setColorFilter(null);
                                        personaggioAttivo1 = true;
                                    }
                                }
                                if (valore[0] > 30)
                                {
                                    personaggio1.setColorFilter(null);
                                    personaggioAttivo1 = true;
                                    personaggio2.setColorFilter(null);
                                    personaggioAttivo2 = true;
                                    personaggio3.setColorFilter(null);
                                    personaggioAttivo3 = true;
                                    personaggio4.setColorFilter(null);
                                    personaggioAttivo4 = true;
                                }
                            }
                        }
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }

    private void disattivaPersonaggi() {
        Log.d("TAG", personaggioScelto);

        switch (personaggioScelto)
        {
            case "Leone":
            case "Principessa":
            case "PappagalloPirata":
            case "Principessa Ginevra":
                personaggio1.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                personaggio3.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                personaggio4.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                personaggioAttivo2 = true;
                break;
            case "Ghepardo":
            case "Draghetto":
            case "BarbaNera":
            case "Cavaliere Lancillotto":
                personaggio1.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                personaggio2.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                personaggio4.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                personaggioAttivo3 = true;
                break;
            case "Scimmia":
            case "Elfo":
            case "PiratessaElizabeth":
            case "Cavaliere Artù":
                personaggio1.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                personaggio3.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                personaggio2.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                personaggioAttivo4 = true;
                break;
            case "Elefante":
            case "Fatina":
            case "PirataJack":
            case "Fatina Lilla":
                personaggio2.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                personaggio3.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                personaggio4.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                personaggioAttivo1 = true;
                break;
            default:
                personaggio1.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                personaggio2.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                personaggio3.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                personaggio4.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                break;
        }

        setMonete();
    }

    private void personaggioScelto() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null)
        {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = db.collection("SceltaPersonaggio");

            collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "Query successful. Result: " + task.getResult().size());
                        for (DocumentSnapshot document : task.getResult()) {
                            String idDocumento = document.getId();
                            idDocumento = idDocumento.replaceAll("\\s", "");
                            Log.d("TAG", "idDocumento: " + idDocumento + " userId: " + userId);
                            Log.d("TAG", "condizione: " + idDocumento.equals(userId));

                            if (idDocumento.equals(userId))
                            {
                                personaggioScelto = document.getString("nome_personaggio");
                                Log.d("TAG", "Personaggio scelto: " + personaggioScelto);
                                disattivaPersonaggi();
                            }
                        }
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }

    private void movimentoPersonaggi() {
        personaggio1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //da cambiare in base alla logica di sblocco dei personaggi

                if (personaggioAttivo1)
                {
                    ClipData clipData = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    v.startDragAndDrop(clipData, shadowBuilder, v, 0);
                    v.setVisibility(View.INVISIBLE);
                    return true;
                }
                else
                    return false;
            }
        });

        personaggio2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //da cambiare in base alla logica di sblocco dei personaggi


                if (personaggioAttivo2)
                {
                    ClipData clipData = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    v.startDragAndDrop(clipData, shadowBuilder, v, 0);
                    v.setVisibility(View.INVISIBLE);
                    return true;
                }
                else
                    return false;
            }
        });

        personaggio3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //da cambiare in base alla logica di sblocco dei personaggi

                if (personaggioAttivo3)
                {
                    ClipData clipData = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    v.startDragAndDrop(clipData, shadowBuilder, v, 0);
                    v.setVisibility(View.INVISIBLE);
                    return true;
                }
                else
                    return false;
            }
        });

        personaggio4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //da cambiare in base alla logica di sblocco dei personaggi

                if (personaggioAttivo4)
                {
                    ClipData clipData = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    v.startDragAndDrop(clipData, shadowBuilder, v, 0);
                    v.setVisibility(View.INVISIBLE);
                    return true;
                }
                else
                    return false;

            }
        });

        surfaceView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DROP:
                        // Recupera l'ImageView trascinato
                        ImageView draggedImageView = (ImageView) event.getLocalState();

                        // Ottieni le coordinate in cui l'utente ha rilasciato l'ImageView
                        float x = event.getX();
                        float y = event.getY();

                        // Passa l'ImageView e le coordinate a GameThread per il disegno
                        gameThread.setDraggedImage(draggedImageView, x, y);

                        // Supponendo che tu abbia un'istanza di GameThread chiamata gameThread
                        int ovalIndex = gameThread.getCurrentOvalIndex(x, y) - 1; // Ottenere l'indice ovale corrente

                        // Verifica se l'indice ovale è valido
                        if (ovalIndex >= 0) {
                            // Ottieni l'esercizio corrispondente all'ovale corrente
                            String esercizioDaAssegnare = paroleDaConfrontare[ovalIndex]; // Supponendo che paroleDaConfrontare sia un array contenente i nomi degli esercizi

                            // Chiamata a livelloAttivato con l'ovale corrente e l'esercizio da assegnare
                            livelloAttivato(x, y, esercizioDaAssegnare);
                        } else {
                            // Gestisci il caso in cui l'indice ovale non sia valido
                            // Ad esempio, mostra un messaggio di errore o gestisci il flusso del programma di conseguenza
                            // ...
                        }


                        // Rendi nuovamente visibile l'ImageView
                        draggedImageView.setVisibility(View.VISIBLE);

                        return true;
                }
                return true;
            }
        });
    }
    private int ovaleCorrente = 0;
    private void livelloAttivato(float x, float y, String esercizioDaAssegnare) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Ottieni l'ID dell'utente corrente
            String userId = currentUser.getUid();

            // Ottieni il riferimento al documento utente utilizzando l'ID dell'utente
            DocumentReference userRef = db.collection("Utente").document(userId);

            // Ottieni il nome utente dal documento utente
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String nomeUtente = documentSnapshot.getString("nome"); // Supponendo che il campo si chiami "nome"
                    // Ottieni l'ID del bambino corrente
                    String bambinoId = currentUser.getUid();
                    if (nomeUtente != null) {
                        CollectionReference assignmentsRef = db.collection("assignments");
                        Log.d(TAG, "Livello attivato per esercizio: " + esercizioDaAssegnare);
                        // Esegui una query su "assignments" per trovare l'assegnazione corrispondente all'ovale corrente
                        assignmentsRef.whereEqualTo("patient_id", bambinoId)
                                .whereEqualTo("exercise_name", esercizioDaAssegnare)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String exerciseName = document.getString("exercise_name");
                                            if (exerciseName != null) {
                                                Log.d(TAG, "Esercizio trovato: " + exerciseName);

                                                // Assegna l'esercizio all'ovale corrente
                                                if (ovaleCorrente < gameThread.getZigzagPath().size()) {
                                                    gameThread.assignExerciseToOval(ovaleCorrente, esercizioDaAssegnare);
                                                    ovaleCorrente++; // Passa all'ovale successivo

                                                    // Confronta l'esercizio con il database per navigare al fragment corrispondente
                                                    confrontaParolaConDatabase(assignmentsRef, bambinoId, esercizioDaAssegnare);
                                                }
                                            }
                                        }
                                    } else {
                                        // Gestisci eventuali errori nella query
                                        Log.e(TAG, "Error getting documents: ", task.getException());
                                    }
                                });
                    } else {
                        Log.d(TAG, "Nome utente non trovato nel documento utente.");
                    }
                } else {
                    Log.d(TAG, "Documento utente non trovato per l'utente corrente.");
                }
            }).addOnFailureListener(e -> {
                // Gestisci eventuali errori nel recupero del documento utente
                Log.e(TAG, "Error getting user document: ", e);
            });
        }
    }


    // Metodo per confrontare ogni parola con il database
    private void confrontaParolaConDatabase(CollectionReference assignmentsRef, String nomeUtente, String parolaDaConfrontare) {
        // Array per tenere traccia degli esercizi trovati per ogni ovale
        boolean[] esercizioTrovatoPerOvale = new boolean[gameThread.getZigzagPath().size()];
        // Inizializza l'array a false
        Arrays.fill(esercizioTrovatoPerOvale, false);

        // Esegui una query su "assignments" per trovare corrispondenze con il nome utente e la parola specifica
        assignmentsRef.whereEqualTo("patient_id", nomeUtente)
                .whereEqualTo("exercise_name", parolaDaConfrontare)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String exerciseName = document.getString("exercise_name");
                            if (exerciseName != null) {
                                Log.d(TAG, "Esercizio trovato: " + exerciseName);

                                // Ottieni l'ID dell'ovale associato all'esercizio
                                int ovalId = getOvalIdForExercise(exerciseName);
                                Log.d(TAG, "ID dell'ovale associato all'esercizio: " + ovalId);

                                if (ovalId != -1 && ovalId < esercizioTrovatoPerOvale.length) {
                                    // Se l'ovale corrispondente all'esercizio è valido, apri il fragment solo se non è già stato aperto per quell'ovale
                                    if (!esercizioTrovatoPerOvale[ovalId]) {
                                        Log.d(TAG, "Documento trovato. Apertura del fragment corrispondente per l'ovale " + ovalId);
                                        Point ovalCenter = gameThread.getZigzagPath().get(ovalId);
                                        navigateToFragment(exerciseName);
                                        esercizioTrovatoPerOvale[ovalId] = true; // Imposta il flag per indicare che l'esercizio è stato trovato per questo ovale
                                    }
                                }
                            }
                        }
                    } else {
                        // Gestisci eventuali errori nella query
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }


    private int getOvalIdForExercise(String exerciseName) {
        // Esegui un'azione diversa in base al nome dell'esercizio
        switch (exerciseName) {
            case "Riconoscimento coppie minime n.1":
                return 0;
            case "Riconoscimento coppie minime n.2":
                return 1;
            case "Riconoscimento coppie minime n.3":
                return 2;
            case "Riconoscimento coppie minime n.4":
                return 3;
            case "riconosci la parola associata all'immagine":
                return 5;
            case "ripeti la sequenza di parole":
                return 4;
            default:
                // Se il nome dell'esercizio non corrisponde a nessun caso, restituisci un valore non valido
                return -1;
        }
    }

    // Metodo per navigare verso il fragment corrispondente in base alla parola confrontata
    private void navigateToFragment(String exerciseName) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_bambino);

        // Esegui un'azione diversa in base al nome dell'esercizio
        switch (exerciseName) {
            case "Riconoscimento coppie minime n.1":
                navController.navigate(R.id.gioco1_fragment2);
                break;
            case "Riconoscimento coppie minime n.2":
                navController.navigate(R.id.gioco2_fragment2);
                break;
            case "Riconoscimento coppie minime n.3":
                navController.navigate(R.id.gioco3_fragment2);
                break;
            case "Riconoscimento coppie minime n.4":
                navController.navigate(R.id.gioco1B_fragment2);
                break;
            case "ripeti la sequenza di parole":
                navController.navigate(R.id.gioco4_fragment2);
                break;
            case "riconosci la parola associata all'immagine":
                navController.navigate(R.id.denominazione1_fragment);
                break;
            default:
                // Azione predefinita se il nome dell'esercizio non corrisponde a nessun caso
                Log.d(TAG, "Parola non gestita.");
                break;
        }
    }

    private float calculateDistance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}