package com.example.comandaelotrnica.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.helper.Permissao;
import com.example.comandaelotrnica.helper.UsuarioFirebase;
import com.example.comandaelotrnica.model.Usuario;
import com.example.comandaelotrnica.service.UsuarioService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesActivity extends AppCompatActivity {


    private ImageButton imageButtonGaleria;
    private ImageButton imageButtonCamera;
    private CircleImageView circleImageViewPerfil;
    private EditText editTextNome;
    private ImageView imageAtualizarNome;
    private StorageReference storageReference = ConfiguracaoFirebase.getFirebaseStorage();;
    private String idUsuario = UsuarioFirebase.getIdentificaçãoUsuario();
    private Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
    private UsuarioService usuarioService = new UsuarioService();
    private static final int SELECAO_CAMERA = 100, SELECAO_GALERIA = 200;
    private String[] permissoesNescessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        // idUsuario = UsuarioFirebase.getIdentificaçãoUsuario();
        idUsuario = UsuarioFirebase.getIdentificaçãoUsuario();

        // Validar Permições
        Permissao.validarPermissoes(permissoesNescessarias,this,1);

        imageButtonCamera = findViewById(R.id.imageButtonCamera);
        imageButtonGaleria = findViewById(R.id.imageButtonGaleria);
        circleImageViewPerfil = findViewById(R.id.circleImageViewFotoPerfil);
        editTextNome = findViewById(R.id.editPerfilNome);
        imageAtualizarNome = findViewById(R.id.imageAtualizarNome);
        imageButtonCamera.setBackgroundColor(Color.TRANSPARENT);
        imageButtonGaleria.setBackgroundColor(Color.TRANSPARENT);

        eventoClickImageButton();

        Toolbar toolbar = findViewById(R.id.toolbarPersonalizada);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Recuperar dados usuario
        FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();
        Uri url = usuario.getPhotoUrl();

        if (url != null){
            Glide.with(ConfiguracoesActivity.this)
                                .load(url)
                                .into(circleImageViewPerfil);

        }else {
            circleImageViewPerfil.setImageResource(R.drawable.perfil);
        }

        editTextNome.setText(usuario.getDisplayName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == SELECAO_CAMERA || requestCode == SELECAO_GALERIA){
            Bitmap imagem = null;

            try {

                switch (requestCode){
                    case SELECAO_CAMERA:
                            imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),localImagemSelecionada);
                        imagem = ImageDecoder.decodeBitmap(source);
                        //imagem = MediaStore.Images.Media.getBitmap(getContentResolver(),localImagemSelecionada);
                        break;
                }

                if (imagem != null){
                    circleImageViewPerfil.setImageBitmap(imagem);

                    // salvar imagem no firebase

                 final  StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("perfil")
                            .child(idUsuario)
                            .child("perfil.jpeg");

                    // Recuperar os dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG,70,baos);
                    byte[] dadosImagem = baos.toByteArray();

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ConfiguracoesActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();

                            if (taskSnapshot.getMetadata() != null){
                                if (taskSnapshot.getMetadata().getReference() != null){

                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                       atualizarFotoUsuario(uri);
                                        }
                                    });

                                }
                            }

                        }
                    });
                    }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResult : grantResults){
            if (permissaoResult == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void eventoClickImageButton(){
        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, SELECAO_CAMERA);
            }
            }
        });

        imageButtonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, SELECAO_GALERIA);
                }
            }
        });

        imageAtualizarNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = editTextNome.getText().toString();
                boolean retorno = UsuarioFirebase.atualizarNomeUsuario(nome);
                if (retorno){

                    usuarioLogado.setNome(nome);
                    usuarioService.atualizar(usuarioLogado);

                    Toast.makeText(ConfiguracoesActivity.this,
                            "Nome alterado com sucesso!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void atualizarFotoUsuario(Uri url){

       boolean retorno = UsuarioFirebase.atualizarFotoUsuario(url);

       if (retorno){
           usuarioLogado.setFoto(url.toString());
           usuarioService.atualizar(usuarioLogado);
       }

    }
}
