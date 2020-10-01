package com.example.comandaelotrnica.fragment;

import android.os.Bundle;

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
import com.example.comandaelotrnica.helper.Base64Custom;
import com.example.comandaelotrnica.model.Cardapio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BebidasClienteFragment extends Fragment {

    private RecyclerView recyclerView;

    private AdapterCardapio adapterCardapio;
    private List<Cardapio> list = new ArrayList<>();
    private DatabaseReference cardapioRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private ValueEventListener valueEventListenerCardapio;
    public BebidasClienteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bebidas_cliente, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewBebidaCliente);

        cardapioRef = ConfiguracaoFirebase.getFirebaseDatabase().child("cardapio");

        // configurar adapter
        adapterCardapio = new AdapterCardapio(list,getActivity());
        //configurarRecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterCardapio);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperaCardapio();
    }

    @Override
    public void onStop() {
        super.onStop();
        cardapioRef.removeEventListener(valueEventListenerCardapio);
    }

    public void recuperaCardapio(){

        Query query = cardapioRef.child("bebida")
                .orderByChild("valorItem");
        valueEventListenerCardapio = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    Cardapio cardapio = dados.getValue(Cardapio.class);
                    cardapio.setKey(dados.getKey());
                    cardapio.setCategoria(dataSnapshot.getKey());
                    list.add(cardapio);
                }
                adapterCardapio.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}