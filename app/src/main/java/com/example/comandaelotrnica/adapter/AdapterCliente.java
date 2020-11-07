package com.example.comandaelotrnica.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCliente extends RecyclerView.Adapter<AdapterCliente.MyViewHolder> {

    private List<Usuario> clientes;
    private Context context;

    public AdapterCliente(List<Usuario> clientes, Context context) {
        this.clientes = clientes;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_cliente,parent,false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Usuario user = clientes.get(position);
        holder.nome.setText("Nome: " + user.getNome());
        holder.email.setText("E-mail: " + user.getEmail());
        holder.dataCad.setText("Data de cadastro: " + user.getDataCadstro());
        holder.status.setText(user.getStatus());
        if(user.getStatus().equals("online")){
        holder.status.setTextColor(Color.parseColor("#1CB07E"));
        }

        if (user.getFoto() != null){
            Uri uri = Uri.parse(user.getFoto());
            Glide.with(context).load(uri).into(holder.imagePerfil);
        }


    }

    @Override
    public int getItemCount() {
        return clientes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nome, email, dataCad ,status;
        CircleImageView imagePerfil, imageEditar, imageExcluir;
        Button comandda;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textViewNomeCliente);
            email = itemView.findViewById(R.id.textViewEmailCliente);
            imagePerfil = itemView.findViewById(R.id.circleImageCliente);
            dataCad = itemView.findViewById(R.id.textViewDataCliente);
            status = itemView.findViewById(R.id.textViewStatusCliente);

        }
    }
}
