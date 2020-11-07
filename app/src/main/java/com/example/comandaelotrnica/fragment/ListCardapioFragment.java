package com.example.comandaelotrnica.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.adapter.AdapterCardapio;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.helper.Base64Custom;
import com.example.comandaelotrnica.model.ItemCardapio;
import com.example.comandaelotrnica.service.CardapioService;
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
public class ListCardapioFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdapterCardapio adapterCardapio;
    private List<ItemCardapio> list = new ArrayList<>();
    private CardapioService cardapioService = new CardapioService();
    private DatabaseReference cardapioRef;
    private FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private StorageReference storageReference = ConfiguracaoFirebase.getFirebaseStorage();
    private ValueEventListener valueEventListenerCardapio;
    private String categoria;

    public ListCardapioFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_cardapio, container, false);

        recyclerView = view.findViewById(R.id.recyclerListCardapio);

        cardapioRef = ConfiguracaoFirebase.getFirebaseDatabase().child("cardapio");

        // configurar adapter
        adapterCardapio = new AdapterCardapio(list,getActivity());
        //configurarRecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterCardapio);
        //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

        categoria = getArguments().getString("categoria");

       swipe();
        return view;
    }





    public void swipe(){
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START|ItemTouchHelper.END;
                return makeMovementFlags(dragFlags,swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                ItemCardapio cardapio = list.get(position);
                cardapioService.excluirItem(cardapio,cardapioRef,adapterCardapio,getActivity(),
                        storageReference,position,getLayoutInflater());

            }
        };
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }

    @Override
    public void onStart() {
        super.onStart();

        recuperaCardapio(categoria);
    }

    @Override
    public void onStop() {
        super.onStop();
        cardapioRef.removeEventListener(valueEventListenerCardapio);
    }

    public void recuperaCardapio(String c){

        String emaiUser = auth.getCurrentUser().getEmail();
        final String idUser = Base64Custom.codificarBase64(emaiUser);
        Query query = cardapioRef
                .child(idUser)
                .child(c)
                .orderByChild("valorItem");
        valueEventListenerCardapio = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    ItemCardapio cardapio = dados.getValue(ItemCardapio.class);
                    cardapio.setIdItemCardapio(dados.getKey());
                    cardapio.setCategoria(dataSnapshot.getKey());
                    cardapio.setIdEmpresa(idUser);

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
