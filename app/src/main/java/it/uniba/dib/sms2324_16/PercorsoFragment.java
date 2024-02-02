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
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PercorsoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PercorsoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
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

    //prende il numero di monete dal db e le fa visualizzare in questa schermata
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
                                else
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

    private void movimentoPersonaggi() {
        personaggio1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
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
                boolean personaggioAttivo = true;

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
                boolean personaggioAttivo = true;

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
                boolean personaggioAttivo = true;

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

                        livelloAttivato(x, y);

                        // Rendi nuovamente visibile l'ImageView
                        draggedImageView.setVisibility(View.VISIBLE);

                        return true;
                }
                return true;
            }
        });
    }

    private void livelloAttivato(float x, float y) {
        // Confronta il nome dell'esercizio con le parole specifiche
        String[] paroleDaConfrontare = {
                "Riconoscimento coppie minime n.1",
                "Riconoscimento coppie minime n.2",
                "Riconoscimento coppie minime n.3",
                "Riconoscimento coppie minime n.4",
                "ripeti la sequenza di parole",
                "riconosci la parola associata all'immagine"
        };

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        for (int i = 0; i < gameThread.getZigzagPath().size(); i++) {
            Point ovalCenter = gameThread.getZigzagPath().get(i);
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

                        if (nomeUtente != null) {
                            CollectionReference assignmentsRef = db.collection("assignments");

                            Log.d(TAG, "Nome utente: " + nomeUtente);

                            // Esegui un loop per confrontare ogni parola con le parole da confrontare
                            for (String parolaDaConfrontare : paroleDaConfrontare) {
                                Log.d(TAG, "Parola da confrontare: " + parolaDaConfrontare);

                                // Esegui una query su "assignments" per trovare corrispondenze con il nome utente e la parola specifica
                                assignmentsRef.whereEqualTo("patient_name", nomeUtente)
                                        .whereEqualTo("exercise_name", parolaDaConfrontare)
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                // Se ci sono documenti che soddisfano i criteri di query
                                                if (!task.getResult().isEmpty()) {
                                                    Log.d(TAG, "Documento trovato. Apertura del fragment gioco1_fragment.");
                                                    // Naviga verso il fragment gioco1_fragment corrispondente
                                                    navigateToFragment(parolaDaConfrontare);
                                                } else {
                                                    // Nessun documento trovato con il nome dell'esercizio corrispondente
                                                    // Puoi gestire questa situazione in base alle tue esigenze
                                                    Log.d(TAG, "Nessun documento trovato con il nome dell'esercizio corrispondente.");
                                                }
                                            } else {
                                                // Gestisci eventuali errori nella query
                                                Log.e(TAG, "Error getting documents: ", task.getException());
                                            }
                                        });
                            }
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
    }

    // Metodo per navigare verso il fragment corrispondente in base alla parola confrontata
    private void navigateToFragment(String parolaDaConfrontare) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_bambino);

        // Esegui un'azione diversa in base alla parola confrontata
        switch (parolaDaConfrontare) {
            case "Riconoscimento coppie minime n.1":
                navController.navigate(R.id.action_percorsoFragment_to_gioco1_fragment2);
                break;
            case "Riconoscimento coppie minime n.2":
                navController.navigate(R.id.action_percorsoFragment_to_gioco2_fragment2);
                break;
            case "Riconoscimento coppie minime n.3":
                navController.navigate(R.id.action_percorsoFragment_to_gioco3_fragment2);
                break;
            case "Riconoscimento coppie minime n.4":
                navController.navigate(R.id.action_percorsoFragment_to_gioco4_fragment2);
                break;
            case "ripeti la sequenza di parole":
                navController.navigate(R.id.action_percorsoFragment_to_gioco1B_fragment2);
                break;
            case "riconosci la parola associata all'immagine":
                navController.navigate(R.id.action_percorsoFragment_to_denominazione1_fragment);
                break;
            default:
                // Azione predefinita se la parola confrontata non corrisponde a nessun caso
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