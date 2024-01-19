package it.uniba.dib.sms2324_16;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private EditText nome;
    private EditText cognome;
    private EditText email;
    private EditText password;
    private Button registrazione;
    private String nomeString;
    private String cognomeString;
    private String emailString;
    private String passwordString;
    private FirebaseAuth mAuth;
    private TextView linkAccedi;
    private NavController navController;
    private ProgressBar progressBar;
    private TextView textViewErrore;

    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignInFragment.
     */
    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void handleSignInError(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidUserException) {
            // L'utente non esiste o è stato disabilitato
            // Tratta l'errore appropriato
            textViewErrore.setText("L'utente non esiste");
            textViewErrore.setVisibility(View.VISIBLE);
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            // Credenziali non valide
            // Tratta l'errore appropriato
            if (exception instanceof FirebaseAuthUserCollisionException)
            {
                textViewErrore.setText("Utente già esistente");
                textViewErrore.setVisibility(View.VISIBLE);
            }
            else if (exception instanceof FirebaseAuthEmailException) {
                textViewErrore.setText("Formato sbagliato email");
                textViewErrore.setVisibility(View.VISIBLE);
            }
            else
            {
                textViewErrore.setText("Credenziali non valide");
                textViewErrore.setVisibility(View.VISIBLE);
            }
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            // Problema di collisione utente (ad esempio, tentativo di registrazione di un utente già esistente)
            // Tratta l'errore appropriato
            textViewErrore.setText("Utente già esistente");
            textViewErrore.setVisibility(View.VISIBLE);
        } else if (exception instanceof FirebaseAuthEmailException) {
            // Problema con l'indirizzo email fornito
            // Tratta l'errore appropriato
            textViewErrore.setText("Formato sbagliato email");
            textViewErrore.setVisibility(View.VISIBLE);
        } else {
            // Altro tipo di errore
            // Tratta l'errore generico
            textViewErrore.setText("Errore di accesso");
            textViewErrore.setVisibility(View.VISIBLE);
        }
    }

    private void controlloUtente(FirebaseUser firebaseUser, Users user) {
        if(firebaseUser != null && user != null)
        {
            String uID = firebaseUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Crea un riferimento al documento dell'utente utilizzando l'UID
            DocumentReference userRef = db.collection("Utente").document(uID);

            // Salva le informazioni aggiuntive nel documento
            userRef.set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            Intent intent = new Intent(getContext(), IntermediatePage.class);
                            intent.putExtra("ID_Utente", uID);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });
        }
        else
            Toast.makeText(getContext(), "Registrazione non è andata a buon fine", Toast.LENGTH_SHORT).show();
    }

    private void registrazione(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getContext(), "Registrazione effettuata con successo", Toast.LENGTH_SHORT).show();
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            nomeString = nome.getText().toString();
                            cognomeString = cognome.getText().toString();

                            Intent intent = getActivity().getIntent();
                            String tipoUtente = intent.getStringExtra("tipoUtente");

                            if(!nomeString.isEmpty() && !cognomeString.isEmpty())
                            {
                                Users user = null;

                                if (tipoUtente.equals("logopedista"))
                                    user = new Users(nomeString, cognomeString, tipoUtente);
                                else if (tipoUtente.equals("genitore"))
                                    user = new Users(nomeString, cognomeString, tipoUtente);

                                controlloUtente(firebaseUser, user);
                            }
                            else
                                Toast.makeText(getContext(), "Riempi i campi lasciati vuoti", Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            handleSignInError(task.getException());
                            controlloUtente(null, null);
                        }
                    }
                });
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
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Sign up PronuntiApp");
        }

        nome = view.findViewById(R.id.nome);
        cognome = view.findViewById(R.id.cognome);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        progressBar = view.findViewById(R.id.caricamento);
        textViewErrore = view.findViewById(R.id.errore);

        registrazione = view.findViewById(R.id.registrazione);

        registrazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                emailString = email.getText().toString();
                passwordString = password.getText().toString();

                if(!emailString.isEmpty() && !passwordString.isEmpty())
                {
                    progressBar.setVisibility(View.GONE);
                    registrazione(emailString, passwordString);
                }
                else
                    Toast.makeText(getContext(), "Riempi i campi lasciati vuoti", Toast.LENGTH_SHORT).show();
            }
        });

        linkAccedi = view.findViewById(R.id.link);

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        linkAccedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_signUpFragment_to_loginFragment);
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }

}