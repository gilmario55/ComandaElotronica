package com.example.comandaelotrnica.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.comandaelotrnica.fragment.ListCardapioFragment;
import com.example.comandaelotrnica.helper.UsuarioFirebase;
import com.example.comandaelotrnica.ui.home.HomeFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

import java.util.HashMap;

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
                R.id.nav_home, R.id.nav_clientes, R.id.nav_cardapio, R.id.nav_mesas, R.id.nav_categoria)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

       // fragment = (RefeicaoFragment) getSupportFragmentManager().findFragmentByTag("fragment_list_cardapio.xml");

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
                DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
                String idUser = UsuarioFirebase.getIdentificacaoUsuario();
                HashMap<String,Object> value = new HashMap<>();
                value.put("status","offline");
                reference.child("usuarios").child(idUser).updateChildren(value);
                autenticacao.signOut();
                Intent intent  = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
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

   public void abrirConfig(){
       Intent intent = new Intent(AdminActivity.this,ConfiguracoesActivity.class);
       startActivity(intent);
   }
}
