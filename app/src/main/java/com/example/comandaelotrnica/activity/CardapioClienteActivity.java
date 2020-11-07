package com.example.comandaelotrnica.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.fragment.CardapioClienteFragment;
import com.example.comandaelotrnica.helper.UsuarioFirebase;
import com.example.comandaelotrnica.model.CategoriaCardapio;
import com.example.comandaelotrnica.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.List;

public class CardapioClienteActivity extends AppCompatActivity {

    private SmartTabLayout smartTabLayout;
    private ViewPager viewPager;
    private DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
    private ValueEventListener listener;
    private List<String> list = new ArrayList<>();
    private FragmentPagerItemAdapter adapter;
    private  String idEmpresa;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio_cliente);

        smartTabLayout = findViewById(R.id.viewPagerTabCardapioCliente);
        viewPager = findViewById(R.id.viewPagerCardapioCliente);

        manager = getSupportFragmentManager();
        recuperarCategoria();

        Toolbar toolbar = findViewById(R.id.toolbarPersonalizada);
        toolbar.setTitle("Cardapio");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(listener);
    }

    public void recuperarCategoria( ){

        Query query = firebase
                .child(idEmpresa)
                .child("categoriaCardapio");
        listener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot data : snapshot.getChildren()){
                    CategoriaCardapio cat = data.getValue(CategoriaCardapio.class);
                    list.add(cat.getNome());
                }
                tabs();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void tabs (){
        FragmentPagerItems pages = new FragmentPagerItems(this);
        for (String categoria : list){
            Bundle bundle = new Bundle();
            bundle.putString("categoria",categoria);
            pages.add(FragmentPagerItem.of(categoria+"s", CardapioClienteFragment.class,bundle));
        }
        adapter = new FragmentPagerItemAdapter(
                manager
                ,pages);

        viewPager.setAdapter(adapter);
        smartTabLayout.setViewPager(viewPager);

    }

    public interface MyCallback {
        void onCallback(Usuario usuario);
    }
}