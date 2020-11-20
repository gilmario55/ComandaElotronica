package com.example.comandaelotrnica.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.adapter.AdapterComanda;
import com.example.comandaelotrnica.adapter.AdapterComandaEmpresa;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.helper.UsuarioFirebase;
import com.example.comandaelotrnica.model.Comanda;
import com.example.comandaelotrnica.model.ItemComanda;
import com.example.comandaelotrnica.model.Mesa;
import com.example.comandaelotrnica.model.Usuario;
import com.example.comandaelotrnica.service.ComandaService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ComandaEmpresaActivity extends AppCompatActivity {

    private TextView textViewNumMesa, textViewData, textViewValorTotal, textViewTitulo;
    private Button buttonFechar;
    private LinearLayout linearLayout;
    private List<Comanda> comanda = new ArrayList<>();
    private List<ItemComanda> listCarrinho = new ArrayList<>();
    private List<ItemComanda> itensCarrinho = new ArrayList<>();
    private ComandaService service = new ComandaService();
    private RecyclerView recyclerView;
    private DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
    private Usuario empresa, cliente;
    private Comanda comandaRecuperada;
    final private String idUsuario = UsuarioFirebase.getIdentificacaoUsuario();
    private AdapterComanda adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comanda_empresa);

        recyclerView = findViewById(R.id.recyclerViewComandaEmpresa);
        textViewData = findViewById(R.id.textViewDataComandaEmpresa);
        textViewNumMesa = findViewById(R.id.textViewNumMesaEmpresa);
        textViewValorTotal = findViewById(R.id.textViewValorComandaEmpresa);
        textViewTitulo = findViewById(R.id.textViewComandaEmpresa);
        buttonFechar = findViewById(R.id.buttonFecharComandaEmpresa);
        linearLayout = findViewById(R.id.linearLayoutComandaEmpresa);

        adapter = new AdapterComanda(this,listCarrinho,comanda);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ComandaEmpresaActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            cliente = (Usuario) bundle.getSerializable("cliente");
           textViewTitulo.setText( "Cliente " + cliente.getNome());
        }


        Toolbar toolbar = findViewById(R.id.toolbarPersonalizada);
        toolbar.setTitle("Comanda");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buttonFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comfirmarComanda();
            }

        });
        recuperarUsuario();

    }

    public  void recuperarUsuario(){
        DatabaseReference database = reference
                .child("usuarios")
                .child(idUsuario);

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    empresa = snapshot.getValue(Usuario.class);
                }
                recuperarComanda();

            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    public void recuperarComanda(){

        Query database = reference
                .child("comanda")
                .child(idUsuario)
                .child(cliente.getIdUsuario())
                .child("aberta");

         database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itensCarrinho = new ArrayList<>();

                if(snapshot.getValue() != null){
                    System.out.println("Empresa: " + snapshot.getValue().toString());
                    listCarrinho.clear();
                    comanda.clear();
                    comandaRecuperada = snapshot.getValue(Comanda.class);
                    comandaRecuperada.setIdUsuario(cliente.getIdUsuario());
                    comandaRecuperada.setIdEmpresa(idUsuario);
                    itensCarrinho = comandaRecuperada.getItens();
                    comanda.add(comandaRecuperada);
                    if (itensCarrinho != null){
                            linearLayout.setVisibility(View.VISIBLE);
                            buttonFechar.setVisibility(View.VISIBLE);
                        for(ItemComanda itemPedido: itensCarrinho){

                            listCarrinho.add(itemPedido);
                            adapter.notifyDataSetChanged();
                        }


                    }else {
                        adapter.notifyDataSetChanged();

                    }
                    DecimalFormat df = new DecimalFormat("0.00");
                    textViewData.setText("Data da Comanda: " + comandaRecuperada.getDataComanda());
                    textViewNumMesa.setText(String.valueOf("Numero da Mesa: " + (comandaRecuperada.getNumeroMesa() + 1)));
                    textViewValorTotal.setText(String.valueOf("Valor total da comanda: R$ " + comandaRecuperada.getTotalPreco()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void comfirmarComanda() {
        String cod = comandaRecuperada.getIdComanda();
        comandaRecuperada.setIdComanda( null);
        service.comfirmar(comandaRecuperada,cod);
        service.removerComanda(comandaRecuperada,"aberta");
        listCarrinho.clear();
        startActivity( new Intent(this, AdminActivity.class));
    }
}