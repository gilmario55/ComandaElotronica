package com.example.comandaelotrnica.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.activity.ComandaActivity;
import com.example.comandaelotrnica.adapter.AdapterMesa;
import com.example.comandaelotrnica.adapter.AdapterMesaDialog;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.model.Mesa;
import com.example.comandaelotrnica.model.Usuario;
import com.example.comandaelotrnica.service.MesaService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MesaCustomDialog extends DialogFragment implements AdapterMesaDialog.RecyclerItemClick {

    private static final String TAG = "MesaCustomDialog";

    @Override
    public void itemClick(Mesa mesa) {
        Intent intent = new Intent(getContext(), ComandaActivity.class);
        intent.putExtra("mesa",mesa);
        startActivity(intent);
    }

    public interface OnInputListener{
        void sendInput(String input);
    }
    public OnInputListener mOnInputListener;

    //widgets
    private RecyclerView recyclerView;
    private TextView mActionCancel;
    private AdapterMesaDialog adapterMesaDialog;
    private List<Mesa> list = new ArrayList<>();
    private DatabaseReference referenceMesa = ConfiguracaoFirebase.getFirebaseDatabase();
    private ValueEventListener listenerMesa;


    //vars

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_mesa, container, false);
        mActionCancel = view.findViewById(R.id.actionCancelarMesa);
        recyclerView = view.findViewById(R.id.recyclerViewDialogMesa);

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });

        // config adapter
        adapterMesaDialog = new AdapterMesaDialog(list, getActivity(),this);

        // config recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterMesaDialog);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarMesas();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mOnInputListener = (OnInputListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
    }

    public void recuperarMesas(){
        Query query = referenceMesa
                .child("mesa")
                .orderByChild("status").equalTo("livre");
        listenerMesa = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dados : snapshot.getChildren()){
                    Mesa mesa = dados.getValue(Mesa.class);
                    mesa.setIdMesa(dados.getKey());
                    list.add(mesa);
                }
                adapterMesaDialog.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
