package com.example.comandaelotrnica.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.adapter.AdapterComanda;
import com.example.comandaelotrnica.helper.UsuarioFirebase;
import com.example.comandaelotrnica.model.Comanda;
import com.example.comandaelotrnica.model.ItemComanda;
import com.example.comandaelotrnica.model.Mesa;
import com.example.comandaelotrnica.model.Usuario;
import com.example.comandaelotrnica.service.ComandaService;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ComandaActivity extends AppCompatActivity {

    private AdapterComanda adapterComanda;
    private List<Comanda> comanda = new ArrayList<>();
    private List<ItemComanda> listCarrinho = new ArrayList<>();
    private List<ItemComanda> itensCarrinho = new ArrayList<>();
    private ComandaService service = new ComandaService();
    private RecyclerView recyclerView;
    private DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
    private ValueEventListener listener;
    private Mesa mesa;
    private Usuario empresa, cliente;
    private Comanda comandaRecuperada;
    private String idUsuario = UsuarioFirebase.getIdentificacaoUsuario();
    private int qtdItensCarrinho;
    private double totalCarrinho;
    private String metodoPagamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comanda);
        recyclerView = findViewById(R.id.recyclerViewComanda);
        adapterComanda = new AdapterComanda(ComandaActivity.this,listCarrinho,comanda);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ComandaActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterComanda);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mesa = (Mesa) bundle.getSerializable("mesa");
            empresa = (Usuario) bundle.getSerializable("empresa");
        }

        Toolbar toolbar = findViewById(R.id.toolbarPersonalizada);
        toolbar.setTitle("Comanda");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recuperarUsuario();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.comanda,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_cardapio:
                abrirCardapio();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void atualizarStatusMesa(){
        HashMap<String,Object> status = new HashMap<>();
        status.put("status","ocupada");

        DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference databaseReference = reference
                .child("mesa")
                .child(mesa.getIdMesa());
        databaseReference.updateChildren(status);
    }

    public void abrirCardapio(){
        Intent intent = new Intent(this,CardapioClienteActivity.class);
        startActivity(intent);
    }

    public  void recuperarUsuario(){
        DatabaseReference database = reference
                .child("usuarios")
                .child(idUsuario);

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    cliente = snapshot.getValue(Usuario.class);
                }
                recuperarComanda();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void recuperarComanda(){
        DatabaseReference database = reference
                .child("comanda_aberta")
                .child(idUsuario)
                .child(cliente.getIdEmpresa());

       database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                qtdItensCarrinho = 0;
                totalCarrinho = 0.0;
                itensCarrinho = new ArrayList<>();

                if(snapshot.getValue() != null){
                    listCarrinho.clear();
                    comanda.clear();
                    comandaRecuperada = snapshot.getValue(Comanda.class);
                    comandaRecuperada.setIdUsuario(idUsuario);
                    comandaRecuperada.setIdEmpresa(snapshot.getKey());
                    itensCarrinho = comandaRecuperada.getItens();
                    comanda.add(comandaRecuperada);
                    if (itensCarrinho != null){
                        for(ItemComanda itemPedido: itensCarrinho){
                            int qtde = itemPedido.getQuantidade();
                            double preco = itemPedido.getPreco();

                            totalCarrinho += (qtde * preco);
                            qtdItensCarrinho += qtde;
                            listCarrinho.add(itemPedido);
                            adapterComanda.notifyDataSetChanged();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}