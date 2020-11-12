package com.example.comandaelotrnica.ui.cliente.cardapio;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.comandaelotrnica.R;

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
