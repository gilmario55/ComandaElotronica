package com.example.comandaelotrnica.ui.cliente.comanda;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.comandaelotrnica.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComandaFragment extends Fragment {

    public ComandaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comanda_cliente, container, false);
    }
}
