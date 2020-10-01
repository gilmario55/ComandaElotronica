package com.example.comandaelotrnica.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.helper.Base64Custom;
import com.example.comandaelotrnica.model.Cardapio;
import com.example.comandaelotrnica.service.CardapioService;
import com.google.firebase.auth.FirebaseAuth;

public class EditarCaradpioActivity extends AppCompatActivity {

    private EditText editTextNome;
    private EditText editTextValor, editTextDescricao;
    private Button buttonCancelar;
    private Cardapio cardapio;
    private CardapioService cardapioService = new CardapioService();
    private FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAutenticacao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_caradpio);

        editTextNome = findViewById(R.id.editNomeCardapioEditar);
        editTextValor = findViewById(R.id.editPrecoCardapioEditar);
        editTextDescricao = findViewById(R.id.textAreaDescricaoEditar);
        buttonCancelar = findViewById(R.id.buttonEditarCancelar);

        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        verificarParametro();
    }

    private void verificarParametro(){
        Bundle bundle = getIntent().getExtras();

        if (bundle != null && (bundle.containsKey("item"))){
             cardapio = (Cardapio) bundle.getSerializable("item");
             editTextNome.setText(cardapio.getNome());
             editTextValor.setText(Double.toString(cardapio.getPreco()));
             editTextDescricao.setText(cardapio.getDescricao());
        }
    }


    public void editarCardapio(View view){

        if (validarCampos()){
            auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
            String idUsuario = Base64Custom.codificarBase64(auth.getCurrentUser().getEmail());

            cardapio.setIdUsuario(idUsuario);
            cardapio.setNome(editTextNome.getText().toString());
            cardapio.setPreco(Double.valueOf(editTextValor.getText().toString()));
            cardapioService.editar(cardapio);
            finish();

        }

    }

    private Boolean validarCampos(){
        String textoNomeItem = editTextNome.getText().toString();
        String textoPrecoItem = editTextValor.getText().toString();

        if (!textoNomeItem.isEmpty()){
            if (!textoPrecoItem.isEmpty()){

            }else {
                Toast.makeText(EditarCaradpioActivity.this,
                        "Campo preço do item não preenchido",
                        Toast.LENGTH_SHORT).show();
                return false;
            }

        }else {
            Toast.makeText(EditarCaradpioActivity.this,
                    "Campo nome do item não foi preenchido!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
