package it.uniba.dib.sms2324_16;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class AppuntamentiGenitoreFragment extends Fragment {
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ottieni le referenze alle viste dal layout
        CalendarView calendarView = view.findViewById(R.id.calendarView3);

        // Imposta il listener per la selezione della data
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                // Chiamato quando un giorno è selezionato

                // Visualizza la finestra di dialogo per l'appuntamento
                showAppointmentDialog(year, month, dayOfMonth);
            }
        });
    }

    private void showAppointmentDialog(int year, int month, int dayOfMonth) {
        // Puoi personalizzare il layout della finestra di dialogo come preferisci
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.appuntamenti_genitore_dettagli, null);

        // Inizializza e mostra la finestra di dialogo
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle("Appuntamento del " + dayOfMonth + "/" + (month + 1) + "/" + year)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Aggiungi azioni se necessario quando viene premuto il pulsante OK
                    dialog.dismiss();
                })
                .create();

        alertDialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_appuntamenti_logopedista, container, false);
        Button bottoneModifica = rootView.findViewById(R.id.button3);
        ImageView buttonWithIcon = rootView.findViewById(R.id.buttonWithIcon);
        ImageView topLeftIcon = rootView.findViewById(R.id.topLeftIcon);
        bottoneModifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia l'ExerciseListActivity quando il pulsante viene premuto
                Intent intent = new Intent(getActivity(), NewAppointmentActivity.class);
                startActivity(intent);
            }
        });
        buttonWithIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia l'ExerciseListActivity quando il pulsante viene premuto
                Intent intent = new Intent(getActivity(), NewAppointmentActivity.class);
                startActivity(intent);
            }
        });
        topLeftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_genitore);


                // Esegui la navigazione solo se non sei già nel fragment di destinazione
                navController.navigate(R.id.homePageGenitoreFragment);

            }
        });

        Log.d("FragmentLifecycle", "onCreateView called");
        Toast.makeText(requireContext(), "onCreateView called", Toast.LENGTH_SHORT).show();
        return rootView;
    }

}
