package com.example.comandaelotrnica.ui.comanda;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.adapter.AdapterComandaEmpresa;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.helper.UsuarioFirebase;
import com.example.comandaelotrnica.model.Comanda;
import com.example.comandaelotrnica.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComandaFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdapterComandaEmpresa adapter;
    private Usuario empresa;
    private Comanda comandaRecuperada;
    private List<Comanda> listComanda =  new ArrayList<>();
    private DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
    final private String idUser = UsuarioFirebase.getIdentificacaoUsuario();
    private ValueEventListener listener;



    public ComandaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comanda, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewListarComandas);

        // iniciar adapter
        adapter = new AdapterComandaEmpresa(getActivity(),listComanda);

        // configurar adapter
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        recuperarComanda();
    }

    public void recuperarComanda(){
        DatabaseReference comandaRef = reference
                .child("comanda")
                .child(idUser);

        listener = comandaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listComanda.clear();
                if (snapshot.getValue() != null){
                    listComanda.clear();
                    for (DataSnapshot data : snapshot.getChildren()){
                        System.out.println("Key: " + data.getKey());
                            DatabaseReference comandaRef = reference
                                    .child("comanda")
                                    .child(idUser)
                                    .child(data.getKey());
                            listener = comandaRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null){
                                        for (DataSnapshot data : snapshot.getChildren()){

                                            if ( !data.getKey().equals("aberta")){
                                                Comanda comanda = data.getValue(Comanda.class);
                                                listComanda.add(comanda);
                                            }
                                        }

                                        adapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
