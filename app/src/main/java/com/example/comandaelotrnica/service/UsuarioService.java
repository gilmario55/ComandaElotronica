package com.example.comandaelotrnica.service;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.helper.UsuarioFirebase;
import com.example.comandaelotrnica.model.Usuario;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsuarioService {

    private DatabaseReference firebase;

    public void Salvar(Usuario usuario) {
        firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        firebase.child("usuarios")
                .child(usuario.getIdUsuario())
                .setValue(usuario);
    }

    public void atualizar(Usuario usuario){
        String idUsuario = UsuarioFirebase.getIdentificaçãoUsuario();
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuarioRef = database.child("usuarios")
                .child(idUsuario);

       Map<String,Object> valoresUsuario = converterParaMap(usuario);
       usuarioRef.updateChildren(valoresUsuario);

    }

    @Exclude
    private Map<String, Object> converterParaMap(Usuario usuario){
        HashMap<String,Object> usuarioMAp = new HashMap<>();
        usuarioMAp.put("email",usuario.getEmail());
        usuarioMAp.put("nome",usuario.getNome());
        usuarioMAp.put("foto",usuario.getFoto());
        return usuarioMAp;
    }
}
