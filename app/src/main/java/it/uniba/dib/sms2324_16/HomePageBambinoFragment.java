package it.uniba.dib.sms2324_16;

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
    private NavController navController;
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
        CardView cardPercorso = view.findViewById(R.id.cardPercorso);
        CardView cardScenario = view.findViewById(R.id.cardScenario);


        cardPercorso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(requireActivity(), R.id.nav_host_bambino);
                navController.navigate(R.id.action_homePageBambino_fragmentpercorso);
            }
        });

        return view;
    }
}
