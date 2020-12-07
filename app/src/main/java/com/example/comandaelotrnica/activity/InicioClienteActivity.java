package com.example.comandaelotrnica.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
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

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class InicioClienteActivity extends AppCompatActivity {
    //private SmartTabLayout smartTabLayout;
    // private ViewPager viewPager;
    private String texto;
    private String idUser;
    private TextView textViewNome;
    private RecyclerView recyclerView;
    private FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private ValueEventListener listener;
    private List<Usuario> empresaList = new ArrayList<>();
    private AdapterUsuario adapterEmpresa;
    private Usuario usuario = new Usuario();
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_cliente);

        //smartTabLayout = findViewById(R.id.viewPagerTabCliente);
        //viewPager = findViewById(R.id.viewPagerCliente);
        Toolbar toolbar = findViewById(R.id.toolbarPersonalizada);
        toolbar.setTitle("Seja bem vindo");
        setSupportActionBar(toolbar);
        textViewNome = findViewById(R.id.textViewNomeUserInicio);
        recyclerView = findViewById(R.id.recyclerHomeCliente);
        if (auth.getCurrentUser() != null){
            idUser = UsuarioFirebase.getIdentificacaoUsuario()   ;
        }

        // config adpter
        adapterEmpresa = new AdapterUsuario(empresaList,this);

        // configurar dialog
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setTheme(R.style.Custom)
                .setCancelable( false )
                .build();

        // config recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterEmpresa);

        if (auth.getCurrentUser() != null){
            recupearUsuario(new ClienteActivity.MyCallback() {
                @Override
                public void onCallback(Usuario usuario) { 
                    textViewNome.setText("Olá " + usuario.getNome() + "! \nSelecione uma empresa e aproveite.");

                }
            });
        }

        recuperarEmpresas();
        abrirRecycler();

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cliente_aux,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.action_sair:
                deslogar();
                intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
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

    public void recupearUsuario(final ClienteActivity.MyCallback myCallback) {

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


                                dialog.show();
                                final Usuario empresa = empresaList.get(position);
                                HashMap<String,Object> value = new HashMap<>();
                                value.put("idEmpresa",empresa.getIdEmpresa());
                                value.put("status","online");
                                DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase();
                                String idUser = UsuarioFirebase.getIdentificacaoUsuario();
                                usuarioRef.child("usuarios").child(idUser).updateChildren(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dialog.dismiss();
                                    }
                                });
                                Intent i = new Intent(InicioClienteActivity.this,ClienteActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
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

    private void atualizarStatus( String s) {
        if (auth.getCurrentUser() != null) {
            HashMap<String, Object> value = new HashMap<>();
            value.put("status", s);
            value.put("idEmpresa","vazio");
            DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase();
            usuarioRef.child("usuarios").child(UsuarioFirebase.getIdentificacaoUsuario()).updateChildren(value);
        }
    }

    public void deslogar(){
        try {
            auth.signOut();
            finish();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

