package com.example.comandaelotrnica.ui.home;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.helper.Base64Custom;
import com.example.comandaelotrnica.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private String texto;
    private TextView textViewNome;
    private FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private ValueEventListener listener;
    private Usuario usuario = new Usuario();

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        textViewNome = view.findViewById(R.id.textViewNomeUser);

            recupearUsuario(new MyCallback() {
                @Override
                public void onCallback(Usuario usuario) {
                    textViewNome.setText(usuario.getNome());
                }
            });
        return view;
    }


    public void recupearUsuario(final MyCallback myCallback){
        String emailUser = "";
        if (auth.getCurrentUser() != null){
             emailUser = auth.getCurrentUser().getEmail();
        }else {
            getActivity().onBackPressed();
        }

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


    @Override
    public void onStop() {
        super.onStop();
        usuarioRef.removeEventListener(listener);
    }

    public interface MyCallback {
        void onCallback(Usuario usuario);
    }

}
