package com.example.comandaelotrnica.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.model.Comanda;
import com.example.comandaelotrnica.model.ItemComanda;
import com.example.comandaelotrnica.service.ComandaService;
import com.google.firebase.database.DatabaseReference;

import java.text.DecimalFormat;
import java.util.List;

public class AdapterComanda extends RecyclerView.Adapter<AdapterComanda.MyViewHolder>
implements View.OnClickListener {

    private Context context;
    private List<ItemComanda> listCarrinho;
    private List<Comanda> comanda;
    private View.OnClickListener listener;

    public AdapterComanda(Context context, List<ItemComanda> listCarrinho, List<Comanda> comanda){
        this.context = context;
        this.listCarrinho = listCarrinho;
        this.comanda = comanda;

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
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

       ItemComanda item = listCarrinho.get(position);
        DecimalFormat df = new DecimalFormat("0.00");
           holder.nome.setText(item.getNomeItem());
           holder.quantidade.setText("Quantidade: " + item.getQuantidade());
           holder.preco.setText("R$ " + df.format(item.getPrecoTotal()));
           holder.status.setText(item.getStatusItem());

           holder.imagecancelar.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   ComandaService service = new ComandaService();
                   Comanda c = comanda.get(0);
                   c.getItens().remove(position);
                   service.salvar(c);
               }
           });


    }

    @Override
    public int getItemCount() {
        return listCarrinho.size();
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
        ImageView imagecancelar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textViewNomeItemComanda);
            preco = itemView.findViewById(R.id.textViewPrecoComanda);
            quantidade = itemView.findViewById(R.id.textViewQtdComanda);
            status = itemView.findViewById(R.id.textViewPreparoComanda);
            imagecancelar = itemView.findViewById(R.id.imageViewExcluirItem);
        }



    }
}
