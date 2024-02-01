package it.uniba.dib.sms2324_16;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class PazienteAdapter extends BaseAdapter {

    private Context context;
    private List<Paziente> listaPazienti;

    public PazienteAdapter(Context context, List<Paziente> listaPazienti) {
        this.context = context;
        this.listaPazienti = listaPazienti;
    }

    @Override
    public int getCount() {
        return listaPazienti.size();
    }

    @Override
    public Object getItem(int position) {
        return listaPazienti.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.profilo_utente, null);
        }

        // Ottieni il riferimento al paziente corrente
        Paziente paziente = listaPazienti.get(position);


        return convertView;
    }
}
