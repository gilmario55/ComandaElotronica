package com.example.comandaelotrnica.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.helper.ToastConfig;
import com.example.comandaelotrnica.model.CategoriaCardapio;
import com.example.comandaelotrnica.service.CategoriaService;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCategoria extends RecyclerView.Adapter<AdapterCategoria.MyViewHolder> {

    private List<CategoriaCardapio> categorias;
    private Context context;
    private LayoutInflater inflater;

    public AdapterCategoria(List<CategoriaCardapio> categoria, Context context, LayoutInflater inflater) {
        this.categorias = categoria;
        this.context = context;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_categoria,parent,false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

         CategoriaCardapio cat = categorias.get(position);
        holder.categoria.setText(cat.getNome());
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView categoria;
        ImageView excluir;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            categoria = itemView.findViewById(R.id.textViewCategoriaAdapter);
            excluir = itemView.findViewById(R.id.imageViewExcluirCategoria);

            excluir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Excluir categoria");
                    alertDialog.setMessage("Tem certeza que deseja excluir essa categoria?");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CategoriaService service = new CategoriaService();
                            CategoriaCardapio categoria = categorias.get(getLayoutPosition());
                            service.excluir(categoria,context, inflater);

                        }
                    });

                    alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ToastConfig.showCustomAlert(context,inflater,
                                    "Exclusão cancelada.");
                        }
                    });
                    AlertDialog dialog = alertDialog.create();
                    dialog.show();
                }

            });
        }

    }
}
