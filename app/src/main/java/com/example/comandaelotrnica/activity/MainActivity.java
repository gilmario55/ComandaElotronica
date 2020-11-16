package com.example.comandaelotrnica.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.helper.UsuarioFirebase;
import com.example.comandaelotrnica.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText editEmail, editSenha;
    private Button buttonEntrar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editEmail = findViewById(R.id.textInputEditEmail);
        editSenha = findViewById(R.id.textInputSenha);
        buttonEntrar = findViewById(R.id.buttonEntrar);


        buttonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textoEmail = editEmail.getText().toString();
                String textoSenha = editSenha.getText().toString();
                if(!textoEmail.isEmpty()){
                    if (!textoSenha.isEmpty()){

                        usuario = new Usuario();
                        usuario.setSenha(textoSenha);
                        usuario.setEmail(textoEmail);
                        validarLogin();

                    }else {
                        Toast.makeText(MainActivity.this,
                                "Preencha o campo senha",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this,
                            "Preencha o campo email",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void validarLogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    recuperarUsuario();
                }else {
                    String excecao;
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e) {
                        excecao = "Usuário não cadastrado.";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "E-mail e senha não correspondem a um usuário cadastrado.";
                    }catch (Exception e){
                        excecao ="Erro ao fazer login" + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        verificarUsuarioLogado();
    }

    public void btnCadastrar(View view){
        startActivity(new Intent(this,CadastrarUsuarioActivity.class));
    }
    public void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
       //autenticacao.signOut();
        if (autenticacao.getCurrentUser() != null){
            recuperarUsuario();
        }
    }

    public void abrirTelaPrincipal(String texto){

        if (texto.equals("empresa")) {
           if(autenticacao.getCurrentUser() != null){
               HashMap<String,Object> value = new HashMap<>();
               value.put("status","online");
               DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase();
               usuarioRef.child("usuarios").child(idUsuario).updateChildren(value);
           }
            startActivity(new Intent(this, AdminActivity.class));
            finish();
        }else {
            startActivity(new Intent(this, ClienteActivity.class));
        }
    }

    public void recuperarUsuario(){
        idUsuario = UsuarioFirebase.getIdentificacaoUsuario();
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                if(usuario.getTipoUsuario() != null){
                    abrirTelaPrincipal(usuario.getTipoUsuario());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
