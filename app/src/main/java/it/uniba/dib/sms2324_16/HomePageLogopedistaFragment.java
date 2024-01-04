package it.uniba.dib.sms2324_16;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.zip.Inflater;



public class HomePageLogopedistaFragment extends Fragment {

    private GridView gridview;
    private int[] icons = {
            R.drawable.ic_pazienti_foreground,
            //R.drawable.icon2,
            //R.drawable.icon3,
            //R.drawable.icon4
    };
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View rootView = inflater.inflate(R.layout.homepage_logopedista_fragment, container, false);
        gridview = rootView.findViewById(R.id.gridview);
        IconAdapter adapter = new IconAdapter(requireContext(), icons);
        gridview.setAdapter(adapter);
        return rootView.getRootView();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
    }
}
