package com.example.comandaelotrnica.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.fragment.BebidasClienteFragment;
import com.example.comandaelotrnica.fragment.BebidasFragment;
import com.example.comandaelotrnica.fragment.RefeicaoClienteFragment;
import com.example.comandaelotrnica.fragment.SobremesaFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class CardapioClienteActivity extends AppCompatActivity {

    private SmartTabLayout smartTabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio_cliente);

        smartTabLayout = findViewById(R.id.viewPagerTabCardapioCliente);
        viewPager = findViewById(R.id.viewPagerCardapioCliente);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Pratos", RefeicaoClienteFragment.class)
                .add("Bebidas", BebidasClienteFragment.class)
                .add("Sobremesas", SobremesaFragment.class)
                .create());

        viewPager.setAdapter(adapter);
        smartTabLayout.setViewPager(viewPager);
        Toolbar toolbar = findViewById(R.id.toolbarPersonalizada);
        toolbar.setTitle("Cardapio");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}