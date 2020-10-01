package com.example.comandaelotrnica.ui.cliente.cardapio;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.adapter.AdapterCardapioCliente;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.fragment.BebidasFragment;
import com.example.comandaelotrnica.fragment.RefeicaoClienteFragment;
import com.example.comandaelotrnica.fragment.RefeicaoFragment;
import com.example.comandaelotrnica.fragment.SobremesaFragment;
import com.example.comandaelotrnica.helper.UsuarioFirebase;
import com.example.comandaelotrnica.model.Cardapio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardapioClienteFragment extends Fragment {



    public CardapioClienteFragment() {
        // Required empty public constructor
    }

    //private SmartTabLayout smartTabLayout;
    //private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view =  inflater.inflate(R.layout.fragment_cardapio_cliente, container, false);
        /*smartTabLayout = view.findViewById(R.id.viewPagerTabCardapio);
        viewPager = view.findViewById(R.id.viewPagerCardapio);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getActivity().getSupportFragmentManager(), FragmentPagerItems.with(getActivity())
                .add("Pratos", RefeicaoClienteFragment.class)
                .add("Bebidas", BebidasFragment.class)
                .add("Sobremesas", SobremesaFragment.class)
                .create());

        viewPager.setAdapter(adapter);
        smartTabLayout.setViewPager(viewPager);
        */
         return view;
    }





}
