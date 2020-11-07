package com.example.comandaelotrnica.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.adapter.AdapterComanda;
import com.example.comandaelotrnica.fragment.ListCardapioFragment;
import com.example.comandaelotrnica.model.Comanda;
import com.example.comandaelotrnica.model.Mesa;
import com.example.comandaelotrnica.service.ComandaService;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ComandaActivity extends AppCompatActivity {

    private AdapterComanda adapterComanda;
    private List<Comanda> list = new ArrayList<>();
    private ComandaService service = new ComandaService();
    private RecyclerView recyclerView;
    private DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
    private ValueEventListener listener;
    private Mesa mesa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comanda);
        recyclerView = findViewById(R.id.recyclerViewComanda);
        adapterComanda = new AdapterComanda(ComandaActivity.this,list);
        adapterComanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comanda comanda = list.get(recyclerView.getChildAdapterPosition(v));
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ComandaActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterComanda);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mesa = (Mesa) bundle.getSerializable("mesa");
        }
        if(mesa != null){
            atualizarStatusMesa();
        }




        Toolbar toolbar = findViewById(R.id.toolbarPersonalizada);
        toolbar.setTitle("Comanda");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
}