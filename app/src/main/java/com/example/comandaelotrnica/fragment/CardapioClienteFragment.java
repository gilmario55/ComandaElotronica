package com.example.comandaelotrnica.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.adapter.AdapterCardapio;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.helper.Base64Custom;
import com.example.comandaelotrnica.listener.RecyclerItemClickListener;
import com.example.comandaelotrnica.model.Comanda;
import com.example.comandaelotrnica.model.ItemCardapio;
import com.example.comandaelotrnica.model.ItemComanda;
import com.example.comandaelotrnica.model.Usuario;
import com.example.comandaelotrnica.service.CardapioService;
import com.example.comandaelotrnica.helper.UsuarioFirebase;
import com.example.comandaelotrnica.service.ComandaService;
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
public class CardapioClienteFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdapterCardapio adapterCardapio;
    private List<ItemCardapio> list = new ArrayList<>();
    private List<ItemComanda> carrinho;
    private String idUsuario;
    private Usuario empresa, usuario;
    private Comanda comandaRecuperada;
    private CardapioService cardapioService = new CardapioService();
    private ComandaService comandaService = new ComandaService();
    private DatabaseReference cardapioRef;
    private FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private StorageReference storageReference = ConfiguracaoFirebase.getFirebaseStorage();
    private ValueEventListener valueEventListenerCardapio;
    private String categoria;

    public CardapioClienteFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_cardapio, container, false);

        recyclerView = view.findViewById(R.id.recyclerListCardapio);
        carrinho = new ArrayList<>();

        cardapioRef = ConfiguracaoFirebase.getFirebaseDatabase();

        // configurar adapter
        adapterCardapio = new AdapterCardapio(list,getActivity());
        //configurarRecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterCardapio);
        //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        idUsuario = UsuarioFirebase.getIdentificacaoUsuario();
        categoria = getArguments().getString("categoria");

        recuperarUsuario();
        eventoRecycler();

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
    }

    @Override
    public void onStop() {
        super.onStop();
        cardapioRef.removeEventListener(valueEventListenerCardapio);
    }

    public void recuperaCardapio(final String idEmpresa){

        Query query = cardapioRef
                .child("cardapio")
                .child(idEmpresa)
                .child(categoria)
                .orderByChild("preco");
        valueEventListenerCardapio = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    ItemCardapio cardapio = dados.getValue(ItemCardapio.class);
                    cardapio.setIdEmpresa(idEmpresa);
                    cardapio.setIdItemCardapio(dados.getKey());
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

    public void comfirmarQuantidade(final int position){
        recuperarComanda();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Quantidade");
        builder.setMessage("Digite a quantidade");

        final EditText editQuantidade = new EditText(getActivity());
        editQuantidade.setText("1");

        builder.setView( editQuantidade );
        builder.setPositiveButton("confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String quantidade = editQuantidade.getText().toString();
                ItemCardapio cardapio = list.get(position);
                ItemComanda item = new ItemComanda();
                 if (!verificaItem(carrinho, cardapio, Integer.parseInt(quantidade))){
                     item.setIdCardapio(cardapio.getIdItemCardapio());
                     item.setNomeItem(cardapio.getNome());
                     item.setPreco(cardapio.getPreco());
                     item.setQuantidade(Integer.parseInt(quantidade));
                     item.setStatusItem("A ser preparado");
                     if(carrinho == null)
                         carrinho = new ArrayList<>();
                     carrinho.add(item);
                 }

                if (comandaRecuperada == null){
                    comandaRecuperada = new Comanda(idUsuario,cardapio.getIdEmpresa());
                }
                comandaRecuperada.setIdUsuario(idUsuario);
                comandaRecuperada.setIdEmpresa(cardapio.getIdEmpresa());
                comandaRecuperada.setItens(carrinho);
                comandaRecuperada.setNomeUsuario(usuario.getNome());
                comandaRecuperada.setNumeroMesa(usuario.getNumeroMesa());
                comandaService.salvar(comandaRecuperada);

            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void recuperarUsuario(){

        String email = auth.getCurrentUser().getEmail();
        idUsuario = Base64Custom.codificarBase64(email);
        DatabaseReference reference = cardapioRef.child("usuarios")
                .child(idUsuario);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null){
                    usuario = snapshot.getValue(Usuario.class);
                    recuperaCardapio(usuario.getIdEmpresa());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void eventoRecycler(){
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                comfirmarQuantidade(position);
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


    public void recuperarComanda(){
        Query query = cardapioRef
                .child("comanda_aberta")
                .child(idUsuario)
                .child(usuario.getIdEmpresa());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if (snapshot.getValue() != null){
                       comandaRecuperada = snapshot.getValue(Comanda.class);
                       carrinho = comandaRecuperada.getItens();
                   }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean verificaItem(List<ItemComanda> list, ItemCardapio item, int qtd){

        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getIdCardapio().equals(item.getIdItemCardapio())) {

                    int q = carrinho.get(i).getQuantidade();
                    q += qtd;
                    carrinho.get(i).setQuantidade(q);
                    double preco = carrinho.get(i).getPreco() * q;
                    carrinho.get(i).setPreco(preco);
                    return true;
                }
            }
        }
        return false;
    }


}
