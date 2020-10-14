package com.example.comandaelotrnica.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.model.Comanda;

import java.util.List;

public class AdapterComanda extends RecyclerView.Adapter<AdapterComanda.MyViewHolder>
implements View.OnClickListener {

    private Context context;
    private List<Comanda> list;
    private View.OnClickListener listener;

    public AdapterComanda(Context context, List<Comanda> comanda){
        context = context;
        list = comanda;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_comanda,parent,false);
        item.setOnClickListener(this);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Comanda comanda = list.get(position);
        holder.nome.setText(comanda.getNomeItem());
        holder.quantidade.setText("Qunatidade: " + comanda.getQuantidade());
        holder.preco.setText("Preço a pagar: " + String.valueOf(comanda.getPreco()));
        holder.status.setText(comanda.getStatus());

    }

    @Override
    public int getItemCount() {
        return list.size();
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
        private TextView nome, preco, quantidade, status;
        Button buttoncancelar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textViewNomeItemComanda);
            preco = itemView.findViewById(R.id.textViewPrecoComanda);
            quantidade = itemView.findViewById(R.id.textViewQtdComanda);
            status = itemView.findViewById(R.id.textViewStatusComanda);
            buttoncancelar = itemView.findViewById(R.id.buttonCancelarItemComanda);
        }
    }
}
