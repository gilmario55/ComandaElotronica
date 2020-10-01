package com.example.comandaelotrnica.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;

public class AdminActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth autenticacao;


    /*private RecyclerView recyclerView;
    private AdapterCardapio adapterCardapio;
    private List<Cardapio> list = new ArrayList<>();
    private DatabaseReference cardapioRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private ValueEventListener valueEventListenerCardapio;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       /* FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_clientes, R.id.nav_cardapio, R.id.nav_mesas)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        /*recyclerView = findViewById(R.id.recyclerViewCardapio);

        // configurar adapter
            adapterCardapio = new AdapterCardapio(list,this);
        //configurarRecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterCardapio);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin, menu);
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
            case R.id.menu_configuracoes:
                abrirConfig();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public void cadastarCardapio(View view){
        Intent intent = new Intent(this,CardapioActivity.class);
        startActivity(intent);
    }

    public void cadastrarCliente(View view){
        Intent intent = new Intent(this,CadastrarUsuarioActivity.class);
        startActivity(intent);

    }

   /* public void recuperarCardapio(){
        String emaiUser = autenticacao.getCurrentUser().getEmail();
        String idUser = Base64Custom.codificarBase64(emaiUser);
        cardapioRef.child("cardapio")
                .child(idUser);
        valueEventListenerCardapio = cardapioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    Cardapio cardapio = dados.getValue(Cardapio.class);
                    list.add(cardapio);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/

   public void abrirConfig(){
       Intent intent = new Intent(AdminActivity.this,ConfiguracoesActivity.class);
       startActivity(intent);
   }

}
