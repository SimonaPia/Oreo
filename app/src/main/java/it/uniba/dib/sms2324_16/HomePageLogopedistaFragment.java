package it.uniba.dib.sms2324_16;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePageLogopedistaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePageLogopedistaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomePageLogopedistaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomePagaLogopedistaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomePageLogopedistaFragment newInstance(String param1, String param2) {
        HomePageLogopedistaFragment fragment = new HomePageLogopedistaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page_logopedista, container, false);
        GridLayout gridLayout = view.findViewById(R.id.gridLayout);
        CardView cardAppuntamenti = view.findViewById(R.id.cardAppuntamenti);
        CardView cardEsercizi = view.findViewById(R.id.cardEsercizi);
        CardView cardPazienti = view.findViewById(R.id.cardPazienti);
        CardView cardProva = view.findViewById(R.id.cardProva);

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Homepage Logopedista");
        }

        // Imposta il click listener
        cardEsercizi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia l'ExerciseListActivity quando il pulsante viene premuto
                Intent intent = new Intent(getActivity(), ExerciseListActivity.class);
                startActivity(intent);
            }
        });


        cardAppuntamenti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Utilizza Navigation.findNavController() passando l'activity e il tuo fragment
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host);

                // Controlla se il fragment di destinazione è già presente
                if (navController.getCurrentDestination().getId() != R.id.fragment_appuntamenti_logopedista) {
                    // Esegui la navigazione solo se non sei già nel fragment di destinazione
                    navController.navigate(R.id.action_homepage_logopedista_fragment_to_fragment_appuntamenti_logopedista);
                }
            }
        });


        cardProva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Utilizza Navigation.findNavController() passando l'activity e il tuo fragment
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host);

                // Controlla se il fragment di destinazione è già presente
                if (navController.getCurrentDestination().getId() != R.id.gioco1_fragment) {
                    // Esegui la navigazione solo se non sei già nel fragment di destinazione
                    navController.navigate(R.id.action_homepage_logopedista_fragment_to_classificaVistaLogopedista);
                }
            }
        });

        cardPazienti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), Lista_Pazienti.class);
                startActivity(intent);
            }
        });

        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

}