package com.example.comandaelotrnica.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ComandaActivity extends AppCompatActivity {

    private TextView textViewNumMesa, textViewData, textViewValorTotal;
    private Button buttonFechar;
    private LinearLayout linearLayout;
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
    final private String idUsuario = UsuarioFirebase.getIdentificacaoUsuario();
    private int qtdItensCarrinho;
    private double totalCarrinho;
    private String metodoPagamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comanda);
        recyclerView = findViewById(R.id.recyclerViewComanda);
        textViewData = findViewById(R.id.textViewDataComanda);
        textViewNumMesa = findViewById(R.id.textViewNumMesa);
        textViewValorTotal = findViewById(R.id.textViewValorComanda);
        linearLayout= findViewById(R.id.linearLayoutComanda);
        buttonFechar = findViewById(R.id.buttonFecharComanda);

        adapterComanda = new AdapterComanda(ComandaActivity.this,listCarrinho,comanda);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ComandaActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterComanda);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mesa = (Mesa)  bundle.getSerializable("mesa");
            empresa = (Usuario) bundle.getSerializable("empresa");
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

        Query database = reference
                .child("comanda_aberta")
                .child(idUsuario)
                .child(cliente.getIdEmpresa());

       listener = database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                qtdItensCarrinho = 0;
                totalCarrinho = 0.0;
                itensCarrinho = new ArrayList<>();

                if(snapshot.getValue() != null){
                    linearLayout.setVisibility(View.VISIBLE);
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
                            double preco = itemPedido.getPrecoTotal();

                            totalCarrinho += itemPedido.getPrecoTotal();
                            qtdItensCarrinho += qtde;
                            listCarrinho.add(itemPedido);
                            adapterComanda.notifyDataSetChanged();
                        }


                    }else {
                        adapterComanda.notifyDataSetChanged();
                    }
                    DecimalFormat df = new DecimalFormat("0.00");
                    textViewData.setText("Data da Comanda: " + comandaRecuperada.getDataComanda());
                    textViewNumMesa.setText(String.valueOf("Numero da Mesa: " + (comandaRecuperada.getNumeroMesa() + 1)));
                    textViewValorTotal.setText(String.valueOf("Valor total da comanda: R$ " + df.format(totalCarrinho)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public  void comfirmarComanda(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione um método de pagamento");

        final CharSequence[] formas = new CharSequence[]{
          "Dinheiro", "Cartão"
        };

        builder.setSingleChoiceItems(formas, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                metodoPagamento = (String) formas[which];

            }
        });

        final EditText editObs = new EditText(this);
        editObs.setHint("Digite uma observação");
        builder.setView(editObs);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String obs = editObs.getText().toString();
                comandaRecuperada.setMetodoPagamento(metodoPagamento);
                comandaRecuperada.setObs(obs);
                comandaRecuperada.setStatus("finalizada");
                comandaRecuperada.setTotalPreco(totalCarrinho);
                service.comfirmar(comandaRecuperada,"aberta");
                service.remover(comandaRecuperada);
                listCarrinho.clear();
                linearLayout.setVisibility(View.GONE);
                adapterComanda.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public interface MyCallback {
        void onCallback(Comanda comanda);
    }

}