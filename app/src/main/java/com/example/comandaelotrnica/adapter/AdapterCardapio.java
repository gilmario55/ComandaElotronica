package com.example.comandaelotrnica.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.activity.EditarCaradpioActivity;
import com.example.comandaelotrnica.model.ItemCardapio;

import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCardapio
        extends RecyclerView.Adapter<AdapterCardapio.MyViewHolder>
        implements View.OnClickListener {

    private List<ItemCardapio> cardapios;
    private Context context;
    private View.OnClickListener listener;

    public AdapterCardapio(List<ItemCardapio> cardapios, Context context) {
        this.cardapios = cardapios;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter,parent,false);

        itemLista.setOnClickListener(this);

        return new MyViewHolder(itemLista, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            ItemCardapio cardapio = cardapios.get(position);
        String valorFormatado = new DecimalFormat("#,##0.00").format(cardapio.getPreco());
            holder.nomeCardapio.setText(cardapio.getNome());
            holder.valorCardapio.setText("R$ " + valorFormatado);
            holder.descricao.setText(cardapio.getDescricao());
            if(cardapio.getFoto() != null){
                Uri uri = Uri.parse(cardapio.getFoto());
                Glide.with(context).load(uri).into(holder.imageCardapio);
            }   
    }

    @Override
    public int getItemCount() {
        return cardapios.size();
    }


    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null){
            listener.onClick(view);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nomeCardapio;
        TextView valorCardapio;
        TextView descricao;
        TextView tipoBebida;
        ImageView imageViewEditar;
        CircleImageView imageCardapio;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView, final Context context) {
            super(itemView);
            nomeCardapio = itemView.findViewById(R.id.textViewNomeCardapio);
            valorCardapio = itemView.findViewById(R.id.textViewPrecoCardapio);
            descricao = itemView.findViewById(R.id.textViewDescricao);
            imageViewEditar = itemView.findViewById(R.id.imageEditarCardapio);
            imageCardapio = itemView.findViewById(R.id.circleImageCardapio);
            cardView = itemView.findViewById(R.id.cardViewCardapio);


            imageViewEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (cardapios.size() > 0){

                        ItemCardapio cardapio = cardapios.get(getLayoutPosition());

                    Intent intent = new Intent(context, EditarCaradpioActivity.class);
                    intent.putExtra("item",cardapio);
                    context.startActivity(intent);
                    }
                }
            });

        }
    }



}
