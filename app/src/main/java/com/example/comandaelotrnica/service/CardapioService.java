package com.example.comandaelotrnica.service;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.helper.Base64Custom;
import com.example.comandaelotrnica.model.Cardapio;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseRegistrar;
import com.google.firebase.storage.StorageReference;


public class CardapioService {

    public void salvar(Cardapio item, String key,DatabaseReference databaseReference){

               databaseReference
                       .child(item.getCategoria())
                       .child(key)
                       .setValue(item);
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


}
