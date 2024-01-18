package it.uniba.dib.sms2324_16;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class HomePageGenitoreFragment extends Fragment {

    public HomePageGenitoreFragment() {
        // Costruttore vuoto richiesto
    }

    private Activity view;
    Button yourButton = view.findViewById(R.id.bottone_conferma);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla il layout per questo fragment
        View view = inflater.inflate(R.layout.fragment_homepage_genitore, container, false);

        // Ottieni il NavController
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        // Trova il pulsante nella tua vista
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button yourButton = view.findViewById(R.id.your_button_id);

        // Aggiungi un listener per gestire il clic del pulsante
        yourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Esegui la transizione quando il pulsante viene premuto
                navController.navigate(R.id.action_homePageGenitoreFragment_to_activitySceltaScenario);
            }
        });

        return view;
    }
}



