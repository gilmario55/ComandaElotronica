package com.example.comandaelotrnica.service;

import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.helper.UsuarioFirebase;
import com.example.comandaelotrnica.model.Usuario;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
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
        String idUsuario = UsuarioFirebase.getIdentificacaoUsuario();
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
