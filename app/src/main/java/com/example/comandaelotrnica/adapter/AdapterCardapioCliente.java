package com.example.comandaelotrnica.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.model.Cardapio;

import java.text.DecimalFormat;
import java.util.List;

public class AdapterCardapioCliente
        extends RecyclerView.Adapter<AdapterCardapioCliente.MyViewHolder>
        implements View.OnClickListener{

    private List<Cardapio> cardapios;
    private Context context;
    private View.OnClickListener listener;

    public AdapterCardapioCliente(List<Cardapio> cardapios, Context context) {
        this.cardapios = cardapios;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        if (listener != null){
            listener.onClick(v);
        }
    }
    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_cardapio_cliente,parent,false);
                itemLista.setOnClickListener(this);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Cardapio cardapio = cardapios.get(position);
        String valorFormatado = new DecimalFormat("#,##0.00").format(cardapio.getPreco());
        holder.nomeCardapio.setText(cardapio.getNome());
        holder.valor.setText("R$ " + valorFormatado);

        if (cardapio.getCategoria().equals("alimento")){
            holder.nomeCardapio.setTextColor(Color.BLACK);
            holder.valor.setTextColor(Color.RED);
        }else if (cardapio.getCategoria().equals("bebida")){
            holder.nomeCardapio.setTextColor(Color.BLACK);
            holder.valor.setTextColor(Color.RED);
        }else {
            holder.nomeCardapio.setTextColor(Color.BLACK);
            holder.valor.setTextColor(Color.RED);
        }

    }

    @Override
    public int getItemCount() {
        return cardapios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nomeCardapio;
        TextView valor;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeCardapio = itemView.findViewById(R.id.textNomeCardapioCliente);
            valor =itemView.findViewById(R.id.textValorCardapioCliente);
        }
    }

}
