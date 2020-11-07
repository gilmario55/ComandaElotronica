package com.example.comandaelotrnica.service;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.example.comandaelotrnica.config.ConfiguracaoFirebase;

import com.example.comandaelotrnica.helper.ToastConfig;
import com.example.comandaelotrnica.model.CategoriaCardapio;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

public class CategoriaService {
    private DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase().child("categoriaCardapio");
    public void salvar(CategoriaCardapio categoria, final Context context, final LayoutInflater inflater){

        reference.child(categoria.getIdEmpresa())
                .push()
                .setValue(categoria).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               // String texto = "Erro ao cadastrar categoria";
                //ToastConfig.showCustomAlert(context,inflater,texto);
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String texto = "Sucesso ao cadastrar categoria";
                ToastConfig.showCustomAlert(context,inflater,texto);

            }
        });

    }

    public void excluir(final CategoriaCardapio categoria, final Context context,final LayoutInflater inflater){
        reference.child(categoria.getIdEmpresa())
                .child(categoria.getIdCategoria()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String texto = "Sucesso ao excluir categoria";
                ToastConfig.showCustomAlert(context,inflater,texto);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String texto = "Erro ao excluir categoria";
                ToastConfig.showCustomAlert(context,inflater,texto);
            }
        });

    }
}
