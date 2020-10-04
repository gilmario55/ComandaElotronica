package com.example.comandaelotrnica.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandaelotrnica.adapter.AdapterCardapio;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.helper.Base64Custom;
import com.example.comandaelotrnica.helper.ToastConfig;
import com.example.comandaelotrnica.model.Cardapio;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseRegistrar;
import com.google.firebase.storage.StorageReference;


public class CardapioService {

    public void salvar(final Cardapio item, final String key, DatabaseReference databaseReference,
                       final Context context, final LayoutInflater inflater, final StorageReference storageReference){

               databaseReference
                       .child(item.getCategoria())
                       .child(key)
                       .setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       ToastConfig.showCustomAlert(context,inflater,"Sucesso ao cadastrar item ao cardápio.");
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       ToastConfig.showCustomAlert(context,inflater,"Erro ao cadastrar: " + e.getMessage());
                       StorageReference imagem = storageReference
                               .child("imagens")
                               .child("cardapio")
                               .child(key)
                               .child("imagemCardapio.jpeg");
                       imagem.delete().addOnFailureListener((Activity) context, new OnFailureListener() {
                           String texto = "Erro ao apagar imagem!";
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               ToastConfig.showCustomAlert(context,inflater,texto);
                           }
                       });
                   }
               });
        //salvarFoto(item.getFoto(),storageReference,key);

    }

    public void editar(Cardapio item){
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        firebase.child("cardapio")
                .child(item.getCategoria())
                .child(item.getKey())
                .setValue(item);
    }

    public void salvarFoto(ImageView imageView, StorageReference storageReference, String chave){
        final StorageReference storageRef = storageReference
                .child("imagens")
                .child("cardapio")
                .child(chave)
                .child("imagemCardapio.jpeg");


    }

    public void excluirItem(final Cardapio cardapio, final DatabaseReference reference,
                            final AdapterCardapio adapterCardapio, final Context context,
                            final StorageReference storageReference, final int position,
                            final LayoutInflater inflater){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        //Configurar Dialog

        alertDialog.setTitle("Excluir prato do cardápio.");
        alertDialog.setMessage("Vocẽ tem certeza que deseja realmente excluir esse prato do seu cardápio?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                StorageReference imagem = storageReference
                        .child("imagens")
                        .child("cardapio")
                        .child(cardapio.getKey())
                        .child("imagemCardapio.jpeg");
                imagem.delete().addOnFailureListener((Activity) context, new OnFailureListener() {
                    String texto = "Erro ao apagar imagem!";
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ToastConfig.showCustomAlert(context,inflater,texto);
                    }
                });

                reference.child(cardapio.getCategoria())
                .child(cardapio.getKey()).removeValue().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ToastConfig.showCustomAlert(context,inflater,"Erro ao excluir item.");
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ToastConfig.showCustomAlert(context, inflater, "Sucesso ao excluir ao " + cardapio.getCategoria());
                    }
                });
                adapterCardapio.notifyItemRemoved(position);

            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ToastConfig.showCustomAlert(context,inflater, "Exclusão cancelada");
                adapterCardapio.notifyDataSetChanged();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();



    }



}
