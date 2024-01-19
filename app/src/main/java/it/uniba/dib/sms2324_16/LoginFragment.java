package it.uniba.dib.sms2324_16;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    private String emailString;
    private String passwordString;
    private TextView linkRegistrati;
    private Button accesso;
    private EditText email;
    private EditText password;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView textViewErrore;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
            textViewErrore.setText("Credenziali non valide");
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



    private void accesso(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getContext(), "Accesso effettuato con successo", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uID = user.getUid();
                            Intent intent = new Intent(getContext(), IntermediatePage.class);
                            intent.putExtra("ID_Utente", uID);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                            progressBar.setVisibility(View.GONE);
                            handleSignInError(task.getException());
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Login PronuntiApp");
        }

        accesso = view.findViewById(R.id.accesso);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        progressBar = view.findViewById(R.id.caricamento);
        textViewErrore = view.findViewById(R.id.errore);

        accesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                emailString = email.getText().toString();
                passwordString = password.getText().toString();

                if (!emailString.isEmpty() && !passwordString.isEmpty())
                {

                    progressBar.setVisibility(View.GONE);
                    accesso(emailString, passwordString);
                }
                else
                    Toast.makeText(getContext(), "Riempi i campi lasciati vuoti", Toast.LENGTH_SHORT).show();

            }
        });

        linkRegistrati = view.findViewById(R.id.link);

        linkRegistrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_loginFragment_to_signUpFragment);
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        return view;
    }
}