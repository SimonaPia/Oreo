package it.uniba.dib.sms2324_16;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>{
    private List<Bambino> bambinoList;

    public LeaderboardAdapter(List<Bambino> userList) {
        this.bambinoList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bambino bambino = bambinoList.get(position);
        holder.bind(bambino);
    }

    @Override
    public int getItemCount() {
        return bambinoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView rankTextView;
        private TextView nameTextView;
        private TextView valutaTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.rankTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            valutaTextView = itemView.findViewById(R.id.valutaTextView);
        }

        public void bind(Bambino bambino) {
            rankTextView.setText(String.valueOf(getAdapterPosition() + 1)); // Mostra il rango
            nameTextView.setText(bambino.getName());
            valutaTextView.setText(String.valueOf(bambino.getValuta()));
        }
    }
}
