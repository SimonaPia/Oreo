package it.uniba.dib.sms2324_16;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SceltaScenarioAdapter extends RecyclerView.Adapter<SceltaScenarioAdapter.ScenarioViewHolder> {

    public interface OnItemClickListener {
        void onScenarioClick(int actionId);
    }

    private List<ScenarioItem> scenarioItems;
    private OnItemClickListener listener;
    private Context context;

    public SceltaScenarioAdapter(Context context, List<ScenarioItem> scenarioItems, OnItemClickListener listener) {
        this.context = context;
        this.scenarioItems = scenarioItems;
        this.listener = listener;

    }

    @NonNull
    @Override
    public ScenarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_scenario, parent, false);
        return new ScenarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScenarioViewHolder holder, int position) {
        ScenarioItem scenarioItem = scenarioItems.get(position);
        holder.bind(scenarioItem, listener);

        // Imposta il colore del pulsante (modificato a titolo di esempio)
        holder.scenarioButton.setBackgroundResource(R.color.coloreDiSfondoPulsante);
    }

    @Override
    public int getItemCount() {
        return scenarioItems.size();
    }

    public class ScenarioViewHolder extends RecyclerView.ViewHolder {

        private Button scenarioButton;

        public ScenarioViewHolder(@NonNull View itemView) {
            super(itemView);
            scenarioButton = itemView.findViewById(R.id.scenario_button_id);
        }

        public void bind(final ScenarioItem scenarioItem, final OnItemClickListener listener) {
            scenarioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Richiama il listener quando uno scenario viene selezionato
                    listener.onScenarioClick(scenarioItem.getActionId());
                }
            });
        }

    }

    public static class ScenarioItem {
        private int actionId;

        public ScenarioItem(int actionId) {
            this.actionId = actionId;
        }

        public int getActionId() {
            return actionId;
        }
    }
}