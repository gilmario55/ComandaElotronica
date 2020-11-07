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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.helper.UsuarioFirebase;
import com.example.comandaelotrnica.helper.Base64Custom;
import com.example.comandaelotrnica.helper.DateUtil;
import com.example.comandaelotrnica.helper.Permissao;
import com.example.comandaelotrnica.model.ItemCardapio;
import com.example.comandaelotrnica.model.CategoriaCardapio;
import com.example.comandaelotrnica.service.CardapioService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CardapioActivity extends AppCompatActivity {

    private EditText nomeItem, precoItem,editTextDescricao;
    private ImageButton imageButtonGalery, imageButtonCamera;
    private ImageView imageViewCardapio;
    private Spinner spinnerCategoria;
    private String categoriaEscolhida, descricacao;
    private String[] permissoesNescessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private static final int CAMERA = 100, GALERIA = 200;
    private FirebaseAuth auth;
    private StorageReference storageReference = ConfiguracaoFirebase.getFirebaseStorage();
    private DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
    private ValueEventListener listener;
    private String key = firebase.push().getKey();
    private CardapioService cardapioService = new CardapioService();
    private List<String> listCategorias = new ArrayList<>();
    private ArrayAdapter<String> adapterCategoria;
    private Uri url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio);

        nomeItem = findViewById(R.id.editNomeCardapio);
        precoItem = findViewById(R.id.editPrecoCardapio);
        editTextDescricao = findViewById(R.id.textArea_information);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        imageButtonCamera = findViewById(R.id.imageButtonCameraCardapio);
        imageButtonGalery = findViewById(R.id.imageButtonGalery);
        imageViewCardapio = findViewById(R.id.imageViewAddCardapio);
        imageButtonGalery.setBackgroundColor(Color.TRANSPARENT);
        imageButtonCamera.setBackgroundColor(Color.TRANSPARENT);

        // Setando a Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPersonalizada);
        toolbar.setTitle("Cadastro Cardapio");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Validar Permições
        Permissao.validarPermissoes(permissoesNescessarias,this,2);

        // ArrayAdapter para categoria
         adapterCategoria =
                new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,listCategorias);
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapterCategoria);

        recuperarCategoria();
        botoes();
    }


    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA || requestCode == GALERIA){
            Bitmap image = null;

            try {
                switch (requestCode){
                    case CAMERA:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                    case GALERIA:
                        Uri localImageSelecionada = data.getData();
                        ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(),localImageSelecionada);
                        image = ImageDecoder.decodeBitmap(source);
                        break;
                }

                if(image != null){
                    imageViewCardapio.setImageBitmap(image);

                    // Recuperar os dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG,70,baos);
                    byte[] dadosImagem = baos.toByteArray();

                    final StorageReference storageRef = storageReference
                            .child("imagens")
                            .child("cardapio")
                            .child(key)
                            .child("imagemCardapio.jpeg");

                    UploadTask uploadTask = storageRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CardapioActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(CardapioActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                            if (taskSnapshot.getMetadata() != null){
                                if (taskSnapshot.getMetadata().getReference() != null){

                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            final Uri ur = uri;
                                            url = ur;
                                            System.out.println("url da imagem : " + url);
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
        builder.setTitle("Prmissões negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões.");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void salvarCardapio(View view){
        if (validarCamposCardapio()){
            ItemCardapio item = new ItemCardapio();
            String dataAtual = DateUtil.dataAtual();
            double valor = Double.parseDouble( precoItem.getText().toString());
            auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
            String idEmpresa = Base64Custom.codificarBase64(auth.getCurrentUser().getEmail());
            item.setDataCastrato(dataAtual);
            item.setIdEmpresa(idEmpresa);
            item.setNome(nomeItem.getText().toString());
            item.setCategoria(categoriaEscolhida);
            item.setDescricao(descricacao);
            item.setPreco(valor);
            item.setFoto(url.toString());
            cardapioService.salvar(item, key, CardapioActivity.this, getLayoutInflater(),storageReference);
            finish();
        }


    }

    public Boolean validarCamposCardapio(){
        String textoNomeItem = nomeItem.getText().toString();
        String textoPrecoItem = precoItem.getText().toString();
        descricacao = editTextDescricao.getText().toString();
        categoriaEscolhida = (String) spinnerCategoria.getSelectedItem();

        if (!textoNomeItem.isEmpty()){
            if (!textoPrecoItem.isEmpty()){
                    if (!categoriaEscolhida.isEmpty()){
                        if(!descricacao.isEmpty()){
                            if(url != null){
                                return true;
                            }else {
                                Toast.makeText(CardapioActivity.this,
                                        "Imagem não selecionada ou upload em execução. " +
                                                "Se já selecionou a imagem clique novamente em SALVAR",
                                        Toast.LENGTH_LONG).show();
                                return false;
                            }
                        }else {
                            Toast.makeText(CardapioActivity.this,
                                    "Campo descrição não preenchido",
                                    Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }else {
                        Toast.makeText(CardapioActivity.this,
                                "Escolha um acategoria",
                                    Toast.LENGTH_SHORT).show();
                        return false;
                    }

            }else {
                Toast.makeText(CardapioActivity.this,
                        "Campo preço do item não preenchido",
                Toast.LENGTH_SHORT).show();
                return false;
            }

        }else {
            Toast.makeText(CardapioActivity.this,
                    "Campo nome do item não foi preenchido!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public void botoes(){
        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getPackageManager()) != null){
                startActivityForResult(intent,CAMERA);
                }
            }
        });

        imageButtonGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent,GALERIA);
                }
            }
        });

    }

    public void recuperarCategoria(){

        String idEmpresa = UsuarioFirebase.getIdentificaçãoUsuario();

        firebase = firebase
                .child("categoriaCardapio")
                .child(idEmpresa);
        listener = firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCategorias.clear();
                for (DataSnapshot data : snapshot.getChildren()){
                    CategoriaCardapio categoria = data.getValue(CategoriaCardapio.class);
                    listCategorias.add(categoria.getNome());
                }

                adapterCategoria.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public interface MyCallback {
        void onCallback(String url);
    }
}
