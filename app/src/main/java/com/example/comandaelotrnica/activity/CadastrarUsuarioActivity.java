package com.example.comandaelotrnica.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.helper.Base64Custom;
import com.example.comandaelotrnica.helper.DateUtil;
import com.example.comandaelotrnica.helper.ToastConfig;
import com.example.comandaelotrnica.helper.UsuarioFirebase;
import com.example.comandaelotrnica.model.Usuario;
import com.example.comandaelotrnica.service.UsuarioService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastrarUsuarioActivity extends AppCompatActivity {
    private EditText campoNome, campoEmail, campoSenha;
    private Spinner spinner;
    private Button buttonCadastrar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;
    private UsuarioService usuarioService;
    private String perfil[] = new String[]{"cliente","empresa"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_usuario);

        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        spinner = findViewById(R.id.spinnerPerfil);
        buttonCadastrar = findViewById(R.id.buttonCadastrar);

        ArrayAdapter<String> adapterPerfil = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, perfil);
        adapterPerfil.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterPerfil);

        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             validarCampos();
            }
        });
    }

    public void cadastrarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),usuario.getSenha())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String texto = "Sucesso ao cadastrar usuário.";
                            ToastConfig.showCustomAlert(CadastrarUsuarioActivity.this,getLayoutInflater(),texto);
                            UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());
                            finish();
                            try {
                                String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                                usuario.setIdUsuario(idUsuario);
                                usuario.setStatus("offline");
                                usuario.setPerfil("ativo");
                                usuarioService = new UsuarioService();
                                usuarioService.Salvar(usuario);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else {
                            String excecao;
                            try{
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                    excecao = "Digite uma senha mais forte";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                    excecao = "Por favor, digite um email válido";
                            }catch (FirebaseAuthUserCollisionException e){
                                    excecao = "Esta conta já foi cadastrada";
                            }catch (Exception e){
                                excecao ="Erro ao cadastrar usuário" + e.getMessage();
                                e.printStackTrace();
                            }
                            ToastConfig.showCustomAlert(CadastrarUsuarioActivity.this,
                                    getLayoutInflater(),
                                    excecao);
                        }
                    }
                });
    }

    public void login(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void validarCampos(){

        String textoNome = campoNome.getText().toString();
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        // Validar os campos que foram preenchidos
        if(!textoNome.isEmpty()){
            if(!textoEmail.isEmpty()){
                if (!textoSenha.isEmpty()){
                    String perfil = (String) spinner.getSelectedItem();
                    String data = DateUtil.dataAtual();

                    usuario = new Usuario();
                    if (perfil.equals("cliente")){
                        usuario.setIdEmpresa("vazio");
                        usuario.setNumeroMesa(-1);
                    }
                    usuario.setNome(textoNome);
                    usuario.setEmail(textoEmail);
                    usuario.setSenha(textoSenha);
                    usuario.setTipoUsuario(perfil);
                    usuario.setDataCadstro(data);
                    cadastrarUsuario();

                }else {
                    ToastConfig.showCustomAlert(CadastrarUsuarioActivity.this,
                            getLayoutInflater(),
                            "Prencha o campo senha.");
                }
            }else{
                ToastConfig.showCustomAlert(CadastrarUsuarioActivity.this,
                        getLayoutInflater(),
                        "Preencha o campo email.");
            }
        }else{
            ToastConfig.showCustomAlert(CadastrarUsuarioActivity.this,
                    getLayoutInflater(),
                    "Preencha o campo nome.");
        }

    }
}
