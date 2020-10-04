package com.example.comandaelotrnica.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.adapter.AdapterCardapio;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.helper.Base64Custom;
import com.example.comandaelotrnica.model.Cardapio;
import com.example.comandaelotrnica.service.CardapioService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
public class BebidasFragment extends Fragment {
    private RecyclerView recyclerView;

    private AdapterCardapio adapterCardapio;
    private List<Cardapio> list = new ArrayList<>();
    CardapioService cardapioService = new CardapioService();
    private DatabaseReference cardapioRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private StorageReference storageReference = ConfiguracaoFirebase.getFirebaseStorage();
    private ValueEventListener valueEventListenerCardapio;

    public BebidasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bebidas, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewBebida);

        cardapioRef = ConfiguracaoFirebase.getFirebaseDatabase().child("cardapio");

        // configurar adapter
        adapterCardapio = new AdapterCardapio(list,getActivity());
        //configurarRecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterCardapio);
        //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        swipe();

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
                Cardapio cardapio = list.get(viewHolder.getAdapterPosition());
                int position = viewHolder.getAdapterPosition();
                LayoutInflater inflater = getLayoutInflater();
               cardapioService.excluirItem(cardapio,cardapioRef,adapterCardapio,
                       getActivity(),storageReference,position,inflater);

            }
        };
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }

    public void recuperaCardapio(){
        Query query = cardapioRef.child("Bebida")
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
