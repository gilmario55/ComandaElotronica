package com.example.comandaelotrnica.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.model.Mesa;

import java.util.ArrayList;
import java.util.List;

public class AdapterMesa extends RecyclerView.Adapter<AdapterMesa.MyViewHolder> {

    List<Mesa> mesas;
    private Context context;

    public AdapterMesa(List<Mesa> mesas, Context context) {
        this.mesas = mesas;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_mesa,parent,false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Mesa mesa = mesas.get(position);
        holder.numeroMesa.setText("Mesa " + String.valueOf(mesa.getNumeroMesa() + 1));
        holder.status.setText(mesa.getStatus());

    }

    @Override
    public int getItemCount() {
        return mesas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView numeroMesa;
        TextView status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
             numeroMesa = itemView.findViewById(R.id.textViewCodigoMesa);
             status = itemView.findViewById(R.id.textViewStatusMesa);
        }
    }

}
