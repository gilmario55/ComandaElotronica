package com.example.comandaelotrnica.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.activity.EditarCaradpioActivity;
import com.example.comandaelotrnica.adapter.AdapterCardapio;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.helper.Base64Custom;
import com.example.comandaelotrnica.model.Cardapio;
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
public class RefeicaoFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdapterCardapio adapterCardapio;
    private List<Cardapio> list = new ArrayList<>();
    private Cardapio cardapio;
    private DatabaseReference cardapioRef;
    private FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    StorageReference storageReference = ConfiguracaoFirebase.getFirebaseStorage();
    private ValueEventListener valueEventListenerCardapio;

    public RefeicaoFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_refeicao, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewRefeicao);

        cardapioRef = ConfiguracaoFirebase.getFirebaseDatabase().child("cardapio");

        // configurar adapter
        adapterCardapio = new AdapterCardapio(list,getActivity());
        //configurarRecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterCardapio);
        //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

       /* adapterCardapio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Escolhido: " +
                        list.get(recyclerView.getChildAdapterPosition(view)).getNomeItem(),
                        Toast.LENGTH_SHORT).show();
            }f (listener != null){
                        listener.onClick(view);
                    }
        });*/
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

                excluirCardapio(viewHolder);

            }
        };
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
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
        String emaiUser = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codificarBase64(emaiUser);
        Query query = cardapioRef.child("Prato")
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

    public void excluirCardapio(final RecyclerView.ViewHolder viewHolder){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        //Configurar Dialog

        alertDialog.setTitle("Excluir prato do cardápio.");
        alertDialog.setMessage("Vocẽ tem certeza que deseja realmente excluir esse prato do seu cardápio?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                int position = viewHolder.getAdapterPosition();
                cardapio = list.get(position);

                StorageReference imagem = storageReference
                        .child("imagens")
                        .child("cardapio")
                        .child(cardapio.getKey())
                        .child("imagemCardapio.jpeg");
                imagem.delete().addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),
                                "Erro ao deletar imagem",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                cardapioRef = cardapioRef
                        .child("alimento");
                cardapioRef.child(cardapio.getKey()).removeValue();
                adapterCardapio.notifyItemRemoved(position);

            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(),
                        "Exclusão cancelada",
                        Toast.LENGTH_SHORT).show();
                adapterCardapio.notifyDataSetChanged();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();

    }



}
