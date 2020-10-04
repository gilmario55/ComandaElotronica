package com.example.comandaelotrnica.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.helper.Base64Custom;
import com.example.comandaelotrnica.helper.ToastConfig;
import com.example.comandaelotrnica.helper.UsuarioFirebase;
import com.example.comandaelotrnica.model.Cardapio;
import com.example.comandaelotrnica.model.Usuario;
import com.example.comandaelotrnica.service.CardapioService;
import com.example.comandaelotrnica.service.UsuarioService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class EditarCaradpioActivity extends AppCompatActivity {

    private TextView textViewtituloCategoriaBebida;
    private EditText nomeItem, precoItem,editTextDescricao;
    private ImageButton imageButtonGalery, imageButtonCamera;
    private ImageView imageViewCardapio;
    private Spinner spinnerCategoria, spinnerBebida;
    private String categoria [] = new String[]{"Prato", "Bebida", "Sobremesa"};
    private String tipoBebida [] = new String[]{"Alcolica", "Suco", "Refrigerante", "Quente"};
    private String categoriaEscolhida, tipoBebidaEscolhida, descricacao;
    private Button buttonCancelar;
    private Cardapio cardapio;
    private CardapioService cardapioService = new CardapioService();
    private StorageReference storageReference = ConfiguracaoFirebase.getFirebaseStorage();;
    private String idUsuario = UsuarioFirebase.getIdentificaçãoUsuario();
    private static final int SELECAO_CAMERA = 100, SELECAO_GALERIA = 200;
    private ArrayAdapter<String> adapterCategoria, adapterBebida;
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
        spinnerBebida = findViewById(R.id.spinnerBebidaEditar);
        textViewtituloCategoriaBebida = findViewById(R.id.textViewCatBebidaEditar);
        editTextDescricao = findViewById(R.id.textAreaDescricaoEditar);
        buttonCancelar = findViewById(R.id.buttonEditarCancelar);

        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // ArrayAdapter para categoria
        adapterCategoria =
                new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,categoria);
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapterCategoria);
        // ArrayAdapter para tipos de bebidas
        adapterBebida =
                new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,tipoBebida);
        adapterBebida.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBebida.setAdapter(adapterBebida);

        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (categoria[position] != "Bebida") {
                    spinnerBebida.setVisibility(View.GONE);
                    textViewtituloCategoriaBebida.setVisibility(View.GONE);
                } else {
                    spinnerBebida.setVisibility(View.VISIBLE);
                    textViewtituloCategoriaBebida.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        verificarParametro();
        botoes();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECAO_CAMERA || requestCode == SELECAO_GALERIA){
            Bitmap image = null;

            try {
                switch (requestCode){
                    case SELECAO_CAMERA:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        Uri localImage = data.getData();
                        ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),localImage);
                        image = ImageDecoder.decodeBitmap(source);
                }

                if (image != null){
                    imageViewCardapio.setImageBitmap(image);

                    // Recuperar os dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG,70,baos);
                    byte[] dadosImagem = baos.toByteArray();

                    // salvar imagem
                    final StorageReference storageRef = storageReference
                            .child("imagens")
                            .child("cardapio")
                            .child(cardapio.getKey())
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
                            System.out.println("Chave do user: " + cardapio.getKey());
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

    private void verificarParametro(){
        Bundle bundle = getIntent().getExtras();

        if (bundle != null && (bundle.containsKey("item"))){
             cardapio = (Cardapio) bundle.getSerializable("item");
             nomeItem.setText(cardapio.getNome());
             precoItem.setText(Double.toString(cardapio.getPreco()));
             if (cardapio.getFoto() != null){
                 Uri uri = Uri.parse(cardapio.getFoto());
                 Glide.with(EditarCaradpioActivity.this).load(uri).into(imageViewCardapio);
             }
             editTextDescricao.setText(cardapio.getDescricao());
                spinnerCategoria.setSelection(adapterCategoria.getPosition(cardapio.getCategoria()));
                spinnerBebida.setSelection(adapterBebida.getPosition(cardapio.getTipoBebida()));
             }
    }


    public void editarCardapio(View view){

        if (validarCampos()){
                if(categoriaEscolhida == "Bebida")
                    tipoBebidaEscolhida = (String) spinnerBebida.getSelectedItem();
                Cardapio item = new Cardapio();
                item.setIdUsuario(idUsuario);
                item.setNome(nomeItem.getText().toString());
                item.setPreco(Double.valueOf(precoItem.getText().toString()));
                item.setNome(nomeItem.getText().toString());
                item.setCategoria(categoriaEscolhida);
                item.setTipoBebida(tipoBebidaEscolhida);
                item.setDescricao(descricacao);
                item.setKey(cardapio.getKey());
                item.setFoto(url.toString());
            System.out.println("Url da imagem: "+item.getFoto());
                cardapioService.editar(item);
                finish();

        }

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
                        if(url != null){
                            return true;
                        }else {
                              String texto = "Imagem não selecionada ou upload em execução. " +
                                            "Se já selecionou a imagem clique novamente em SALVAR";
                            ToastConfig.showCustomAlert(EditarCaradpioActivity.this,getLayoutInflater(),texto);
                            return false;
                        }
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
}
