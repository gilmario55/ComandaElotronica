package com.example.comandaelotrnica.ui.cardapio;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.fragment.ListCardapioFragment;
import com.example.comandaelotrnica.model.CategoriaCardapio;
import com.example.comandaelotrnica.helper.UsuarioFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardapioFragment extends Fragment {

    private List<String> list;
    private ValueEventListener listener;
    private DatabaseReference reference;

    FragmentManager manager;

    FragmentPagerItemAdapter adapter;

    private SmartTabLayout smartTabLayout;
    private ViewPager viewPager;

    public CardapioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cardapio, container, false);
        smartTabLayout = view.findViewById(R.id.viewPagerTab);
        viewPager = view.findViewById(R.id.viewPager);
        list = new ArrayList<>();
        reference = ConfiguracaoFirebase.getFirebaseDatabase();
        manager = getActivity().getSupportFragmentManager();
        recuperarCategoria();
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        reference.removeEventListener(listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        reference.removeEventListener(listener);
    }

    public void recuperarCategoria( ){
        String idEmpresa = UsuarioFirebase.getIdentificacaoUsuario();
        Query query = reference
                .child("categoriaCardapio")
                .child(idEmpresa);
        listener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot data : snapshot.getChildren()){
                    CategoriaCardapio cat = data.getValue(CategoriaCardapio.class);
                    list.add(cat.getNome());
                }
                tabs();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void tabs (){
        FragmentPagerItems pages = new FragmentPagerItems(getActivity());
        for (String categoria : list){
            Bundle bundle = new Bundle();
            bundle.putString("categoria",categoria);
            pages.add(FragmentPagerItem.of(categoria, ListCardapioFragment.class,bundle));
        }
        adapter = new FragmentPagerItemAdapter(
                manager
                ,pages);

        viewPager.setAdapter(adapter);
        smartTabLayout.setViewPager(viewPager);

    }

}
