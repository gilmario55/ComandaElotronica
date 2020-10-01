package com.example.comandaelotrnica.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class ClienteActivity extends AppCompatActivity {
    //private SmartTabLayout smartTabLayout;
   // private ViewPager viewPager;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        //smartTabLayout = findViewById(R.id.viewPagerTabCliente);
        //viewPager = findViewById(R.id.viewPagerCliente);
        Toolbar toolbar = findViewById(R.id.toolbarPersonalizada);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        //Configurar adapter para abas
       /* FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(getApplicationContext())
                        .add("Home", InicioFragment.class)
                        .add("Cardapio", CardapioClienteFragment.class )
                        .add("Comanda", ComandaFragment.class )
                        .create()
        ); */

        //viewPager.setAdapter( adapter );
        //smartTabLayout.setViewPager( viewPager );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cliente, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_sair:
                autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                autenticacao.signOut();
                finish();
                break;
            case R.id.action_cardapio:
                abrirCardapio();
                break;
            case R.id.action_home:
                abrirHome();
                break;
            case R.id.action_comanda:
                abrirComanda();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void abrirCardapio(){
        Intent intent = new Intent(this,CardapioClienteActivity.class);
        startActivity(intent);
    }
    public void abrirHome(){
        Intent intent = new Intent(this,ClienteActivity.class);
        startActivity(intent);
    }
    public void abrirComanda(){
        Intent intent = new Intent(this,ComandaActivity.class);
        startActivity(intent);
    }

}
