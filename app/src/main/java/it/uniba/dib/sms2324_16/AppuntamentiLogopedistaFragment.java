package it.uniba.dib.sms2324_16;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;


public class AppuntamentiLogopedistaFragment extends Fragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CalendarView calendarView = view.findViewById(R.id.calendarView3);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Imposta la data corretta nel CalendarView
                view.setDate(view.getDate(), true, true);

                // Mostriamo una finestra di dialogo con l'appuntamento per la data selezionata
                showAppointmentDialog(year, month, dayOfMonth);
            }
        });
    }

    private void showAppointmentDialog(int year, int month, int dayOfMonth) {
        // Puoi personalizzare il layout della finestra di dialogo come preferisci
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_appuntamenti_logopedista, null);

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


}