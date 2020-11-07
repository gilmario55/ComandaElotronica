package com.example.comandaelotrnica.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.model.Mesa;

import java.util.List;

public class AdapterMesaDialog extends RecyclerView.Adapter<AdapterMesaDialog.MyViewHolder> {

    List<Mesa> mesas;
    private Context context;
    private RecyclerItemClick itemClick;

    public AdapterMesaDialog(List<Mesa> mesas, Context context, RecyclerItemClick itemClick) {
        this.mesas = mesas;
        this.context = context;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_mesa,parent,false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final Mesa mesa = mesas.get(position);
        holder.numeroMesa.setText("Mesa " + String.valueOf(mesa.getNumeroMesa() + 1));
        holder.status.setText(mesa.getStatus());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClick.itemClick(mesa);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mesas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView numeroMesa;
        TextView status;
        ImageView mesa;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
             numeroMesa = itemView.findViewById(R.id.textViewCodigoMesa);
             status = itemView.findViewById(R.id.textViewStatusMesa);
             mesa = itemView.findViewById(R.id.imageViewMesa);
             mesa.setVisibility(View.GONE);
        }
    }

    public interface RecyclerItemClick{
        void itemClick(Mesa mesa);
    }

}
