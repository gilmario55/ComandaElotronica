package com.example.comandaelotrnica.ui.clientes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.adapter.AdapterCliente;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClienteFragment extends Fragment {

    private TextView textViewNome, textViewEmail, textViewStatus;
    private ImageView imageViewComanda;
    private RecyclerView recyclerView;
    private AdapterCliente adapterCliente;
    private List<Usuario> list = new ArrayList<>();
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private StorageReference storageReference = ConfiguracaoFirebase.getFirebaseStorage();
    private ValueEventListener listener;

    public ClienteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clientes, container, false);

        textViewNome = view.findViewById(R.id.textViewNomeCliente);
        textViewEmail = view.findViewById(R.id.textViewEmailCliente);
        textViewStatus = view.findViewById(R.id.textViewStatusCliente);
        imageViewComanda = view.findViewById(R.id.imageViewComanda);
        recyclerView = view.findViewById(R.id.recyclerCliente);

        // config adapter
        adapterCliente = new AdapterCliente(list, getActivity());

        // config recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterCliente);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarUsuarios();
    }

    @Override
    public void onStop() {
        super.onStop();
        databaseReference.removeEventListener(listener);
    }

    public void recuperarUsuarios(){
        final Query query = databaseReference
                .child("usuarios");
        listener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    final Query query = databaseReference
                            .child("usuarios")
                            .child(data.getKey());

                    listener = query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.getValue() != null) {
                                Usuario usuario = snapshot.getValue(Usuario.class);
                                if (usuario.getStatus().equals("online") && usuario.getTipoUsuario().equals("cliente")){
                                usuario.setIdUsuario(snapshot.getKey());
                                list.add(usuario);}
                                adapterCliente.notifyDataSetChanged();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
