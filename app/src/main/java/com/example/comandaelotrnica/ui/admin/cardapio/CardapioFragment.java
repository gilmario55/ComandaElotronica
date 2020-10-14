package com.example.comandaelotrnica.ui.admin.cardapio;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.activity.CardapioActivity;
import com.example.comandaelotrnica.adapter.AdapterCardapio;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.fragment.BebidasFragment;
import com.example.comandaelotrnica.fragment.RefeicaoFragment;
import com.example.comandaelotrnica.fragment.SobremesaFragment;
import com.example.comandaelotrnica.helper.Base64Custom;
import com.example.comandaelotrnica.model.Cardapio;
import com.google.firebase.auth.FirebaseAuth;
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

    private String[] cardapio = {"Pratos", "Bebidas", "Sobremesas"};

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


       FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                 getActivity().getSupportFragmentManager(), FragmentPagerItems.with(getActivity())
                .add(cardapio[0], RefeicaoFragment.class)
                .add(cardapio[1], BebidasFragment.class)
                .add(cardapio[2], SobremesaFragment.class)
                .create());


       // Intent intent = new Intent(this, CardapioFragment);

        viewPager.setAdapter(adapter);
        smartTabLayout.setViewPager(viewPager);

        return view;
    }



}
