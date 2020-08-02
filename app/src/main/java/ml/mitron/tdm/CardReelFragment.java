package ml.mitron.tdm;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import ml.mitron.tdm.CardBuy.CardBuyActivity;

public class CardReelFragment extends Fragment {

    private ReelViewAdapter reelViewAdapter;
    private RecyclerView recyclerView;
    private PagerSnapHelper pagerSnapHelper;

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
        User.addDataSetChangeListener(new User.DataSetChangeListener() {
            @Override
            public void onDataSetChange() {
                reelViewAdapter.notifyDataSetChanged();
            }
        });

        pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    private class ReelViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder;
            View cardView;
            switch (viewType) {
                case 0:
                default:
                    cardView = LayoutInflater.from(getContext()).inflate(R.layout.layout_card_reel_card, parent, false);
                    viewHolder = new ViewHolderCard(cardView);
                    break;
                case 1:
                    cardView = LayoutInflater.from(getContext()).inflate(R.layout.layout_card_reel_add_card, parent, false);
                    viewHolder = new ViewHolderAddCard(cardView);
                    break;
            }
            return viewHolder;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == (getItemCount() - 1)) {
                return 1;
            } else {
                return 0;
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == 1) {
                ViewHolderAddCard viewHolderAddCard = (ViewHolderAddCard) holder;
                viewHolderAddCard.card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), CardBuyActivity.class));
                    }
                });
                return;
            }
            ViewHolderCard viewHolderCard = (ViewHolderCard) holder;
            List<TDMCard.CardNumber> cardNumberList = Arrays.asList(User.getUser().getTarjetas().keySet().toArray(new TDMCard.CardNumber[User.getUser().getTarjetas().keySet().size()]));
            viewHolderCard.numero.setText(User.getUser().getTarjetas().get(cardNumberList.get(position)).getHiddenCardNumber());
            viewHolderCard.nombre.setText(User.getUser().getTarjetas().get(cardNumberList.get(position)).getCardHolderName());
            viewHolderCard.icono.setImageDrawable(User.getUser().getTarjetas().get(cardNumberList.get(position)).getIconoCardDrawable(getContext()));
        }

        @Override
        public int getItemCount() {
            //sumamos uno porque el último view es el de añadir una tarjeta
            return (User.getUser().getTarjetas().size() + 1);
        }

        class ViewHolderCard extends RecyclerView.ViewHolder {
            TextView nombre;
            TextView numero;
            ImageView icono;

            ViewHolderCard(@NonNull View itemView) {
                super(itemView);
                nombre = itemView.findViewById(R.id.nombreCard);
                numero = itemView.findViewById(R.id.numeroCard);
                icono = itemView.findViewById(R.id.ic_card);
            }
        }

        class ViewHolderAddCard extends RecyclerView.ViewHolder {
            CardView card;

            ViewHolderAddCard(@NonNull View itemView) {
                super(itemView);
                card = itemView.findViewById(R.id.card_view_card_reel_add_card);
            }
        }


    }

}