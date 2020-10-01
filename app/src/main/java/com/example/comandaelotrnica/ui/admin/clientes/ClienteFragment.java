package com.example.comandaelotrnica.ui.admin.clientes;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.activity.CadastrarUsuarioActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClienteFragment extends Fragment {

    public ClienteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_clientes, container, false);
    }

}
