package com.example.comandaelotrnica.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.comandaelotrnica.adapter.AdapterCardapio;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.helper.ToastConfig;
import com.example.comandaelotrnica.model.ItemCardapio;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;


public class CardapioService {

    public void salvar(final ItemCardapio item, final String key, final Context context,
                       final LayoutInflater inflater, final StorageReference storageReference){

        DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase().child("cardapio");

               reference
                       .child(item.getIdEmpresa())
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

    public void editar(ItemCardapio item){
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        firebase.child("cardapio")
                .child(item.getIdEmpresa())
                .child(item.getCategoria())
                .child(item.getIdItemCardapio())
                .setValue(item);
    }

    public void salvarFoto(ImageView imageView, StorageReference storageReference, String chave){
        final StorageReference storageRef = storageReference
                .child("imagens")
                .child("cardapio")
                .child(chave)
                .child("imagemCardapio.jpeg");


    }

    public void excluirItem(final ItemCardapio cardapio, final DatabaseReference reference,
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
                        .child(cardapio.getIdItemCardapio())
                        .child("imagemCardapio.jpeg");
                imagem.delete().addOnFailureListener((Activity) context, new OnFailureListener() {
                    String texto = "Erro ao apagar imagem!";
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ToastConfig.showCustomAlert(context,inflater,texto);
                    }
                });

                reference.child(cardapio.getIdEmpresa())
                        .child(cardapio.getCategoria())
                        .child(cardapio.getIdItemCardapio())
                        .removeValue().addOnFailureListener(new OnFailureListener() {
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
