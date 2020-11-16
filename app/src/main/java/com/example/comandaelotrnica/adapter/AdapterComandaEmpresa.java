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
import com.example.comandaelotrnica.model.Comanda;
import com.example.comandaelotrnica.model.ItemComanda;
import com.example.comandaelotrnica.service.ComandaService;

import java.text.DecimalFormat;
import java.util.List;

public class AdapterComandaEmpresa extends RecyclerView.Adapter<AdapterComandaEmpresa.MyViewHolder>
implements View.OnClickListener {

    private Context context;
    private List<Comanda> comandas;
    private View.OnClickListener listener;

    public AdapterComandaEmpresa(Context context, List<Comanda> comandas){
        this.context = context;
        this.comandas = comandas;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_comanda_empresa,parent,false);
        item.setOnClickListener(this);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        DecimalFormat df = new DecimalFormat("0.00");
        Comanda comanda = comandas.get(position);
        holder.nomeCliente.setText(comanda.getNomeUsuario());
        holder.pagamento.setText(comanda.getMetodoPagamento());
        holder.data.setText(comanda.getDataComanda());
        holder.mesa.setText(String.valueOf(comanda.getNumeroMesa()));
//        holder.valorTotal.setText(String.valueOf(comanda.getTotal()));

    }

    @Override
    public int getItemCount() {
        return comandas.size();
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

        private TextView nomeCliente, pagamento, mesa, data, valorTotal;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeCliente = itemView.findViewById(R.id.textViewNomeItemComandaAdapterEmpresa);
            pagamento = itemView.findViewById(R.id.textViewPagamentoComandaAdapterEmpresa);
            mesa = itemView.findViewById(R.id.textViewMesaComandaAdapterEmpresa);
            data = itemView.findViewById(R.id.textViewDataComandaAdapterEmpresa);
            valorTotal = itemView.findViewById(R.id.textViewValorComandaEmpresa);


        }



    }
}
