package it.uniba.dib.sms2324_16;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class HomePageGenitoreFragment extends Fragment {

        public HomePageGenitoreFragment() {
            // Costruttore vuoto richiesto
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Infla il layout per questo fragment
            return inflater.inflate(R.layout.fragment_homepage_genitore, container, false);
        }
    }


