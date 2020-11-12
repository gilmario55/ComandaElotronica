package com.example.comandaelotrnica.service;

import android.content.Context;
import android.widget.Toast;

import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.model.Comanda;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

public class ComandaService {

    public void salvar(Comanda comanda){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pedidoRef = firebaseRef
                .child("comanda_aberta")
                .child(comanda.getIdUsuario())
                .child( comanda.getIdEmpresa() );
        pedidoRef.setValue( comanda );

    }



}
