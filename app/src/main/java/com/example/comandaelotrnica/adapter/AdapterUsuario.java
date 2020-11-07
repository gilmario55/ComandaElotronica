package com.example.comandaelotrnica.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.config.GlideApp;
import com.example.comandaelotrnica.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUsuario extends RecyclerView.Adapter<AdapterUsuario.MyViewHolder> {

    private List<Usuario> empresas;
    private Context context;

    public AdapterUsuario(List<Usuario> empresas, Context context) {
        this.empresas = empresas;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterUsuario.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_empresa,parent,false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUsuario.MyViewHolder holder, int position) {

        Usuario user = empresas.get(position);
        holder.nome.setText(user.getNome());
        holder.email.setText(user.getEmail());
        if (user.getFoto() != null){
            GlideApp.with(context)
                    .load(Uri.parse(user.getFoto()))
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.perfil);
        }


    }

    @Override
    public int getItemCount() {
        return empresas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nome, email;
        CircleImageView imageView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textViewNomeEmpresa);
            email = itemView.findViewById(R.id.textViewEmailEmpresa);
            imageView = itemView.findViewById(R.id.circleImageEmpresa);


        }
    }
}
