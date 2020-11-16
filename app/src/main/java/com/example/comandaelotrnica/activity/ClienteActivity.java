package com.example.comandaelotrnica.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.adapter.AdapterUsuario;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.dialog.MesaCustomDialog;
import com.example.comandaelotrnica.helper.Base64Custom;
import com.example.comandaelotrnica.helper.UsuarioFirebase;
import com.example.comandaelotrnica.listener.RecyclerItemClickListener;
import com.example.comandaelotrnica.model.Usuario;
import com.example.comandaelotrnica.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClienteActivity extends AppCompatActivity {
    //private SmartTabLayout smartTabLayout;
   // private ViewPager viewPager;
    private String texto;
    private TextView textViewNome;
    private RecyclerView recyclerView;
    private FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private ValueEventListener listener;
    private List<Usuario> empresaList = new ArrayList<>();
    private AdapterUsuario adapterEmpresa;
    private Usuario usuario = new Usuario();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        //smartTabLayout = findViewById(R.id.viewPagerTabCliente);
        //viewPager = findViewById(R.id.viewPagerCliente);
        Toolbar toolbar = findViewById(R.id.toolbarPersonalizada);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        // textViewNome = findViewById(R.id.textViewNomeUserCliente);
        recyclerView = findViewById(R.id.recyclerHomeCliente);

        // config adpter
        adapterEmpresa = new AdapterUsuario(empresaList,this);

        // config recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterEmpresa);

        recupearUsuario(new HomeFragment.MyCallback() {
            @Override
            public void onCallback(Usuario usuario) {

//                textViewNome.setText(usuario.getNome());

                    if (usuario.getIdEmpresa().equals("desativado")){
                        recuperarEmpresas();

                    }

            }
        });

        abrirRecycler();

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        recupearUsuario(new HomeFragment.MyCallback() {
            @Override
            public void onCallback(Usuario usuario) {
                if(!usuario.getIdEmpresa().equals("vazio"))
                getMenuInflater().inflate(R.menu.cliente, menu);
                else{
                    getMenuInflater().inflate(R.menu.cliente_aux,menu);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_sair:
                String idUser = UsuarioFirebase.getIdentificacaoUsuario();
                HashMap<String,Object> value = new HashMap<>();
                value.put("status","offline");
                usuarioRef.child("usuarios").child(idUser).updateChildren(value);
                auth.signOut();
                finish();
                break;
            case R.id.action_cardapio:
                abrirCardapio();
                break;
            case R.id.action_home:
                abrirHome();
                break;
            case R.id.action_comanda:

                //abrirDialog();
                Intent intent = new Intent(this,ComandaActivity.class);
                startActivity(intent);
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

    public void recuperarEmpresas(){
        Query query = usuarioRef.child("usuarios").orderByChild("tipoUsuario").equalTo("empresa");
        listener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                empresaList.clear();
                for (DataSnapshot data : snapshot.getChildren()){
                    Usuario usuario = data.getValue(Usuario.class);
                    usuario.setIdEmpresa(data.getKey());
                    empresaList.add(usuario);
                }
                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void recupearUsuario(final HomeFragment.MyCallback myCallback) {
        String emailUser;
            emailUser = auth.getCurrentUser().getEmail();
        final String idUser = Base64Custom.codificarBase64(emailUser);
        Query query = usuarioRef.child("usuarios").child(idUser);
        listener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                myCallback.onCallback(usuario);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void abrirDialog(){
        MesaCustomDialog dialog = new MesaCustomDialog();
        dialog.show(getSupportFragmentManager(),"MesaCustomDialog");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //recuperarEmpresas();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuarioRef.removeEventListener(listener);
    }

    public interface MyCallback {
        void onCallback(Usuario usuario);
    }

    public void abrirRecycler(){
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                final Usuario empresa = empresaList.get(position);
                                HashMap<String,Object> value = new HashMap<>();
                                value.put("idEmpresa",empresa.getIdEmpresa());
                                value.put("status","online");
                                DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase();
                                String idUser = UsuarioFirebase.getIdentificacaoUsuario();
                                usuarioRef.child("usuarios").child(idUser).updateChildren(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent i = new Intent(ClienteActivity.this, ComandaActivity.class);
                                        i.putExtra("empresa",empresa);
                                        startActivity(i);
                                        finish();
                                    }
                                });

                               // Intent i = new Intent(ClienteActivity.this, ComandaActivity.class);
                               // i.putExtra("empresa",empresa);
                                //startActivity(i);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );



    }

}
