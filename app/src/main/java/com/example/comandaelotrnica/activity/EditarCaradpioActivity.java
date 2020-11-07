package com.example.comandaelotrnica.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.config.GlideApp;
import com.example.comandaelotrnica.helper.Permissao;
import com.example.comandaelotrnica.helper.ToastConfig;
import com.example.comandaelotrnica.helper.UsuarioFirebase;
import com.example.comandaelotrnica.model.ItemCardapio;
import com.example.comandaelotrnica.model.CategoriaCardapio;
import com.example.comandaelotrnica.service.CardapioService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class EditarCaradpioActivity extends AppCompatActivity {

    private EditText nomeItem, precoItem,editTextDescricao;
    private ImageButton imageButtonGalery, imageButtonCamera;
    private ImageView imageViewCardapio;
    private Spinner spinnerCategoria;
    private final List<String> listCategorias = new ArrayList<>();
    private String categoriaEscolhida, descricacao;
    private Bitmap image = null;
    private Button buttonCancelar;
    private ItemCardapio cardapio;
    private CardapioService cardapioService = new CardapioService();
    private StorageReference storageReference = ConfiguracaoFirebase.getFirebaseStorage();
    private DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
    private ValueEventListener listener;
    private String idUsuario = UsuarioFirebase.getIdentificaçãoUsuario();
    private static final int SELECAO_CAMERA = 100, SELECAO_GALERIA = 200;
    private ArrayAdapter<String> adapterCategoria;
    private String[] permissoesNescessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private Uri url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_caradpio);

        nomeItem = findViewById(R.id.editNomeCardapioEditar);
        precoItem = findViewById(R.id.editPrecoCardapioEditar);
        spinnerCategoria = findViewById(R.id.spinnerCategoriaEditar);
        imageButtonCamera = findViewById(R.id.imageButtonCameraCardapioEditar);
        imageButtonGalery = findViewById(R.id.imageButtonGaleryEditar);
        imageViewCardapio = findViewById(R.id.imageViewAddCardapioEditar);
        imageButtonGalery.setBackgroundColor(Color.TRANSPARENT);
        imageButtonCamera.setBackgroundColor(Color.TRANSPARENT);
        editTextDescricao = findViewById(R.id.textAreaDescricaoEditar);
        buttonCancelar = findViewById(R.id.buttonEditarCancelar);

        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Validar Permições
        Permissao.validarPermissoes(permissoesNescessarias,this,2);

        // ArrayAdapter para categoria
        adapterCategoria =
                new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,listCategorias);
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapterCategoria);

        recuperarCategoria();
        verificarParametro();
        botoes();
    }


    @Override
    protected void onStop() {
        super.onStop();
        reference.removeEventListener(listener);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECAO_CAMERA || requestCode == SELECAO_GALERIA){

            try {
                switch (requestCode){
                    case SELECAO_CAMERA:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        Uri localImage = data.getData();
                        ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),localImage);
                        image = ImageDecoder.decodeBitmap(source);
                        break;
                }

                System.out.println("image: " + image);
                if (image != null) {
                    imageViewCardapio.setImageBitmap(image);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private void verificarParametro(){
        Bundle bundle = getIntent().getExtras();

        if (bundle != null && (bundle.containsKey("item"))){
             cardapio = (ItemCardapio) bundle.getSerializable("item");
             nomeItem.setText(cardapio.getNome());
             precoItem.setText(Double.toString(cardapio.getPreco()));
             if (cardapio.getFoto() != null){
                   Uri uri = Uri.parse(cardapio.getFoto());
                 //Glide.with(EditarCaradpioActivity.this).load(uri).into(imageViewCardapio);
                 GlideApp.with(this)
                         .load(uri)
                         .into(imageViewCardapio);
             }else {
                 imageViewCardapio.setImageResource(R.drawable.add_galery_image);
             }
             editTextDescricao.setText(cardapio.getDescricao());
                spinnerCategoria.setSelection(adapterCategoria.getPosition(cardapio.getCategoria()));

             }
    }


    public void editarCardapio(View view){

        try {
            if (image != null){
                System.out.println("dentro.............");
                imageViewCardapio.setImageBitmap(image);

                // Recuperar os dados da imagem para o firebase
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG,70,baos);
                byte[] dadosImagem = baos.toByteArray();

                // salvar imagem
                final StorageReference storageRef = storageReference
                        .child("imagens")
                        .child("cardapio")
                        .child(cardapio.getIdItemCardapio())
                        .child("imagemCardapio.jpeg");

                UploadTask uploadTask = storageRef.putBytes(dadosImagem);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditarCaradpioActivity.this,
                                "Erro ao fazer upload da imagem",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(EditarCaradpioActivity.this,
                                "Sucesso ao fazer upload da imagem",
                                Toast.LENGTH_SHORT).show();
                        System.out.println("Chave do user: " + cardapio.getIdItemCardapio());
                        if (taskSnapshot.getMetadata() != null){
                            if (taskSnapshot.getMetadata().getReference() != null){

                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final Uri ur = uri;
                                        url = ur;
                                        if (validarCampos()){
                                            ItemCardapio item = new ItemCardapio();
                                            item.setIdEmpresa(idUsuario);
                                            item.setNome(nomeItem.getText().toString());
                                            item.setPreco(Double.valueOf(precoItem.getText().toString()));
                                            item.setNome(nomeItem.getText().toString());
                                            item.setCategoria(categoriaEscolhida);
                                            item.setDescricao(descricacao);
                                            item.setIdItemCardapio(cardapio.getIdItemCardapio());
                                            item.setFoto(url.toString());
                                            cardapioService.editar(item);
                                            finish();

                                        }
                                    }
                                });

                            }
                        }
                    }
                });
            }
            if (image == null){
                if (validarCampos()){
                    ItemCardapio item = new ItemCardapio();
                    item.setIdEmpresa(idUsuario);
                    item.setNome(nomeItem.getText().toString());
                    item.setPreco(Double.valueOf(precoItem.getText().toString()));
                    item.setNome(nomeItem.getText().toString());
                    item.setCategoria(categoriaEscolhida);
                    item.setDescricao(descricacao);
                    item.setFoto(cardapio.getFoto());
                    item.setIdItemCardapio(cardapio.getIdItemCardapio());
                    cardapioService.editar(item);
                    finish();

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void recuperarCategoria(){

        reference = reference.child("categoriaCardapio");
        listener = reference.addValueEventListener(new ValueEventListener() {
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



    public Boolean validarCampos(){
        String textoNomeItem = nomeItem.getText().toString();
        String textoPrecoItem = precoItem.getText().toString();
        descricacao = editTextDescricao.getText().toString();
        categoriaEscolhida = (String) spinnerCategoria.getSelectedItem();

        if (!textoNomeItem.isEmpty()){
            if (!textoPrecoItem.isEmpty()){
                if (!categoriaEscolhida.isEmpty()){
                    if(!descricacao.isEmpty()){
                       return true;
                    }else {
                        ToastConfig.showCustomAlert(EditarCaradpioActivity.this,getLayoutInflater(),
                                "Campo descrição não preenchido");
                        return false;
                    }
                }else {
                    ToastConfig.showCustomAlert(EditarCaradpioActivity.this,getLayoutInflater()
                    ,"Escolha uma categoria!");
                    return false;
                }

            }else {
                ToastConfig.showCustomAlert(EditarCaradpioActivity.this,getLayoutInflater(),
                        "Campo preço não preenchido!");
                return false;
            }

        }else {
            ToastConfig.showCustomAlert(EditarCaradpioActivity.this,getLayoutInflater(),
                    "Campo nome não preechido!");
            return false;
        }
    }

    public void botoes(){
        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent,SELECAO_CAMERA);
                }
            }
        });

        imageButtonGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent,SELECAO_GALERIA);
                }
            }
        });

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

    public interface MyCallback{
        void onCallback(List<String> list);
    }

}
