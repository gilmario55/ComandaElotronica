package com.example.comandaelotrnica.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.adapter.AdapterComanda;
import com.example.comandaelotrnica.model.Comanda;
import com.example.comandaelotrnica.service.ComandaService;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ComandaActivity extends AppCompatActivity {

    private AdapterComanda adapterComanda;
    private List<Comanda> list = new ArrayList<>();
    private ComandaService service = new ComandaService();
    private RecyclerView recyclerView;
    private DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
    private ValueEventListener listener;

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




        Toolbar toolbar = findViewById(R.id.toolbarPersonalizada);
        toolbar.setTitle("Comanda");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}