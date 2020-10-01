package com.example.comandaelotrnica.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.adapter.AdapterCardapio;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.model.Cardapio;
import com.google.firebase.auth.FirebaseAuth;
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
public class SobremesaClienteFragment extends Fragment {
    private RecyclerView recyclerView;
    private AdapterCardapio adapter;
    private List<Cardapio> list = new ArrayList<>();
    private DatabaseReference cardapioRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private StorageReference storageReference = ConfiguracaoFirebase.getFirebaseStorage();
    private ValueEventListener valueEventListener;

    public SobremesaClienteFragment(){
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sobremesa_cliente, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewSobremesaCliente);
        cardapioRef = ConfiguracaoFirebase.getFirebaseDatabase().child("cardapio");

        // configurar adapter
        adapter = new AdapterCardapio(list,getActivity());
        //configurar recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        listarCardapio();
    }

    @Override
    public void onStop() {
        super.onStop();
        cardapioRef.removeEventListener(valueEventListener);
    }

    public void listarCardapio(){
        Query query = cardapioRef.child("sobremesa")
                .orderByChild("valorItem");
        valueEventListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot data : snapshot.getChildren()){
                    Cardapio cardapio = data.getValue(Cardapio.class);
                    cardapio.setKey(data.getKey());
                    cardapio.setCategoria(snapshot.getKey());
                    list.add(cardapio);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}