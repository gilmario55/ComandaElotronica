package com.example.comandaelotrnica.ui.categoria;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.adapter.AdapterCategoria;
import com.example.comandaelotrnica.helper.ToastConfig;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.model.CategoriaCardapio;
import com.example.comandaelotrnica.service.CategoriaService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import com.example.comandaelotrnica.helper.UsuarioFirebase;

public class CategoriaFragment extends Fragment {

    private EditText editTextCategoria;
    private Button buttonCadastrar;
    private CategoriaService categoriaService = new CategoriaService();
    private AdapterCategoria adapterCategoria;
    private List<CategoriaCardapio> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private ValueEventListener listener;


    public CategoriaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categoria, container, false);
        editTextCategoria = view.findViewById(R.id.editTextCategoria);
        buttonCadastrar = view.findViewById(R.id.buttonCadastrarCategoria);
        recyclerView = view.findViewById(R.id.recyclerViewCategoria);

        adapterCategoria = new AdapterCategoria(list,getActivity(),getLayoutInflater());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterCategoria);

        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrarCategoria();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        listarCategorias();
    }

    @Override
    public void onStop() {
        super.onStop();
        databaseReference.removeEventListener(listener);
    }

    public void cadastrarCategoria(){
        String categoria = editTextCategoria.getText().toString();
        if (categoria.isEmpty()){
            String t = "Campo Nome categoria vazio, preencha-o por favor";
            ToastConfig.showCustomAlert(getActivity(),getLayoutInflater(),t);
        }else {
            CategoriaCardapio categoriaCardapio = new CategoriaCardapio();
            categoriaCardapio.setIdEmpresa(UsuarioFirebase.getIdentificacaoUsuario());
            categoriaCardapio.setNome(categoria);
            categoriaService.salvar(categoriaCardapio,getActivity(),getLayoutInflater());
            editTextCategoria.setText("");
            View current = getActivity().getCurrentFocus();
           InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
           imm.hideSoftInputFromWindow(editTextCategoria.getWindowToken(),0);
            if (current != null) current.clearFocus();
        }
    }

    public void listarCategorias(){
        final String idEmpresa = UsuarioFirebase.getIdentificacaoUsuario();
        Query query = databaseReference
                .child("categoriaCardapio")
                .child(idEmpresa);
        listener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot data : snapshot.getChildren()){
                    CategoriaCardapio cat = data.getValue(CategoriaCardapio.class);
                    cat.setIdCategoria(data.getKey());
                    cat.setIdEmpresa(idEmpresa);
                    list.add(cat);
                }

                adapterCategoria.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}