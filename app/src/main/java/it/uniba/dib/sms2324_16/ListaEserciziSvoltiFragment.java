package it.uniba.dib.sms2324_16;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.firebase.database.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListaEserciziSvoltiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaEserciziSvoltiFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String idBambino, tipoEsercizio, filePath, tipoUtente = "";
    private List<ExerciseItem> exerciseItemList;
    private RecyclerView recyclerView;
    private boolean esercizioCorretto;

    public ListaEserciziSvoltiFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListaEserciziSvoltiFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListaEserciziSvoltiFragment newInstance(String param1, String param2) {
        ListaEserciziSvoltiFragment fragment = new ListaEserciziSvoltiFragment();
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
        View view = inflater.inflate(R.layout.fragment_lista_esercizi_svolti, container, false);

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Correzione esericizi");
        }

        getTipoUtente();

        recyclerView = view.findViewById(R.id.recyclerView);

        return view;
    }

    private void getTipoUtente() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = db.collection("Utente");

            collectionReference.document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists())
                        {
                            tipoUtente = document.getString("tipoUtente");

                            if (tipoUtente.equals("genitore"))
                                getDatiGenitore();
                            else
                                getDatiLogopedista();
                        }
                        else
                            Log.d("TAG", "Il documento non esiste per userId: " + userId);
                    }
                    else {
                        Log.e(TAG, "Errore durante la lettura del documento: ", task.getException());
                    }
                }
            });
        }
    }

    private void getDatiLogopedista() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = db.collection("EserciziSvolti");

            // Esegui una query filtrata per ottenere i documenti associati all'utente attualmente loggato
            collectionReference.whereEqualTo("id_logopedista", userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                exerciseItemList = new ArrayList<>();

                                for (DocumentSnapshot document : task.getResult()) {
                                    idBambino = document.getString("id_bambino");
                                    tipoEsercizio = document.getString("tipoEsercizio");
                                    filePath = document.getString("filePath");

                                    if (document.contains("corretto"))
                                        esercizioCorretto = true;
                                    else
                                        esercizioCorretto = false;

                                    getNomeBambino(document, esercizioCorretto);
                                }

                                ExerciseAdapter adapter = new ExerciseAdapter(exerciseItemList);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                            } else {
                                Log.e(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    private void getDatiGenitore() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = db.collection("EserciziSvolti");

            // Esegui una query filtrata per ottenere i documenti associati all'utente attualmente loggato
            collectionReference.whereEqualTo("id_genitore", userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                exerciseItemList = new ArrayList<>();

                                for (DocumentSnapshot document : task.getResult()) {
                                    idBambino = document.getString("id_bambino");
                                    tipoEsercizio = document.getString("tipoEsercizio");
                                    filePath = document.getString("filePath");

                                    if (document.contains("corretto"))
                                        esercizioCorretto = true;
                                    else
                                        esercizioCorretto = false;

                                    getNomeBambino(document, esercizioCorretto);
                                }

                                ExerciseAdapter adapter = new ExerciseAdapter(exerciseItemList);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                            } else {
                                Log.e(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    private void getNomeBambino(DocumentSnapshot document, boolean esercizioCorretto) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Utente").document(idBambino);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        String nomeBambino = doc.getString("nome");
                        boolean controllo;
                        if (filePath.isEmpty())
                            controllo = false;
                        else
                            controllo = true;

                        idBambino = document.getString("id_bambino");
                        tipoEsercizio = document.getString("tipoEsercizio");
                        filePath = document.getString("filePath");

                        if (!esercizioCorretto)
                            exerciseItemList.add(new ExerciseItem(nomeBambino, tipoEsercizio, controllo, filePath, idBambino, esercizioCorretto));
                        ExerciseAdapter adapter = new ExerciseAdapter(exerciseItemList);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    } else {
                        Log.d("Firestore", "Il documento non esiste");
                        Toast.makeText(getContext(), "Il documento non esiste", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("Firestore", "Errore durante la query: " + task.getException().getMessage());
                    Toast.makeText(requireContext(), "La query non è andata a buon fine", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}