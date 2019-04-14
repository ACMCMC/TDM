package ml.mitron.tdm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CardReelFragment extends Fragment {

    private ReelViewAdapter reelViewAdapter;
    private RecyclerView recyclerView;

    public CardReelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card_reel, container, false);
        reelViewAdapter = new ReelViewAdapter();
        recyclerView = (RecyclerView) view.findViewById(R.id.card_reel_recyclerView);
        recyclerView.setAdapter(reelViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        return view;
    }

    private class ReelViewAdapter extends RecyclerView.Adapter<ReelViewAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewHolder viewHolder;
            View cardView;
            cardView = LayoutInflater.from(getContext()).inflate(R.layout.tdm_card_standard, parent, false);
            viewHolder = new ViewHolder(cardView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.numero.setText(String.valueOf(position));
        }

        @Override
        public int getItemCount() {
            return User.getUser().getTarjetas().size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nombre;
            TextView numero;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                nombre = itemView.findViewById(R.id.nombreCard);
                numero = itemView.findViewById(R.id.numeroCard);
            }
        }


    }

}