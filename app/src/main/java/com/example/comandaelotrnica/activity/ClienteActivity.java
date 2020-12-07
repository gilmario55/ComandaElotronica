package com.example.comandaelotrnica.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ClipData;
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

import dmax.dialog.SpotsDialog;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class ClienteActivity extends AppCompatActivity {
    //private SmartTabLayout smartTabLayout;
   // private ViewPager viewPager;
    private String texto;
    private String idUser;
    private TextView textViewNome;
    private ImageView image;
    private Button buttonAbrirComanda;
    private FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private List<Usuario> empresaList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        //smartTabLayout = findViewById(R.id.viewPagerTabCliente);
        //viewPager = findViewById(R.id.viewPagerCliente);
        Toolbar toolbar = findViewById(R.id.toolbarPersonalizada);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        textViewNome = findViewById(R.id.textViewNomeUserCliente);
        image = findViewById(R.id.imageViewSejaBemVindo);
        if (auth.getCurrentUser() != null){
            idUser = UsuarioFirebase.getIdentificacaoUsuario()   ;
        }

        buttonAbrirComanda = findViewById(R.id.buttonAbrirComanda);


        if (auth.getCurrentUser() != null){

            Usuario usuario = new Usuario();
            recupearUsuario(new MyCallback() {
                @Override
                public void onCallback(final Usuario usuario) {
                        textViewNome.setText("Olá " + usuario.getNome() + "!");

                    DatabaseReference reference = usuarioRef.child("comanda_aberta")
                            .child(usuario.getIdUsuario())
                            .child(usuario.getIdEmpresa());
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null){
                                DatabaseReference reference = usuarioRef.child("comanda")
                                        .child(usuario.getIdEmpresa())
                                        .child(usuario.getIdUsuario())
                                        .child("aberto");
                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.getValue() != null){
                                            buttonAbrirComanda.setVisibility(View.GONE);
                                        }else {
                                            buttonAbrirComanda.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    abrirDialog();
                                                }
                                            });
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }else{
                                buttonAbrirComanda.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cliente,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.action_sair:
                atualizarStatus("offline");
               deslogar();

                break;
            case R.id.action_cardapio:
                abrirCardapio();
                break;
            case R.id.action_home:
                abrirHome();
                break;
            case R.id.action_comanda:

                //abrirDialog();
                intent = new Intent(this,ComandaActivity.class);
                startActivity(intent);
                finish();
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

    public void recupearUsuario(final MyCallback myCallback) {

        Query query = usuarioRef.child("usuarios").child(idUser);
         query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                usuario.setIdUsuario(dataSnapshot.getKey());
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


    public interface MyCallback {
        void onCallback(Usuario usuario);
    }

    private void atualizarStatus( String s) {
        if (auth.getCurrentUser() != null) {
            HashMap<String, Object> value = new HashMap<>();
            value.put("status", s);
            value.put("idEmpresa","vazio");
            DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase();
            usuarioRef.child("usuarios").child(UsuarioFirebase.getIdentificacaoUsuario())
                    .updateChildren(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });
        }
    }


    public void deslogar(){
        try {
            auth.signOut();
            Intent intent = new Intent(this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}








