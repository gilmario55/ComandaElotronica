package com.example.comandaelotrnica.config;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {

    private static FirebaseAuth autenticacao;
    private static StorageReference storage;
    private static DatabaseReference firebase;

    // retorna a instância do FirebaseDatabase
    public static DatabaseReference getFirebaseDatabase(){
        if ((firebase == null)) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            firebase = database.getReference();

        }
        return firebase;
    }
    //retorna a instancia do FirebaseAuth
    public static FirebaseAuth getFirebaseAutenticacao(){

        if(autenticacao == null){
        autenticacao = FirebaseAuth .getInstance();
        }
        return autenticacao;
    }

    // retorna instância do firebase storage
    public static StorageReference getFirebaseStorage(){
        if(storage == null){
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }
}
