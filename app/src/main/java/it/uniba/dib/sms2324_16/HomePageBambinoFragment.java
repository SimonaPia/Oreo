package it.uniba.dib.sms2324_16;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class HomePageBambinoFragment extends Fragment {
    public HomePageBambinoFragment() {
        // Costruttore vuoto richiesto
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla il layout per questo fragment
        //return inflater.inflate(R.layout.fragment_home_page_genitore, container, false);
        View view = inflater.inflate(R.layout.fragment_homepage_bambino, container, false);
        GridLayout gridLayout = view.findViewById(R.id.gridLayout);
        CardView cardAppuntamenti = view.findViewById(R.id.cardAppuntamenti);
        CardView cardEsercizi = view.findViewById(R.id.cardEsercizi);
        CardView cardScenario = view.findViewById(R.id.cardScenario);


        cardScenario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea un Intent per avviare l'activity di destinazione
                Intent intent = new Intent(requireContext(), SceltaScenarioActivity.class);

                // Avvia l'activity
                startActivity(intent);

                // Puoi rimuovere la CardView se necessario
                gridLayout.removeView(cardScenario);
                gridLayout.invalidate();
                gridLayout.requestLayout();
            }
        });

        return view;
    }
}
