package com.example.comandaelotrnica.ui.admin.mesas;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comandaelotrnica.R;
import com.example.comandaelotrnica.adapter.AdapterMesa;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.helper.ToastConfig;
import com.example.comandaelotrnica.model.Mesa;
import com.example.comandaelotrnica.service.MesaService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MesasFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button buttonAdd;
    private AdapterMesa adapterMesa;
    private List<Mesa> list = new ArrayList<>();
    private DatabaseReference referenceMesa = ConfiguracaoFirebase.getFirebaseDatabase();
    private ValueEventListener listenerMesa;
    private MesaService mesaService = new MesaService();

    public MesasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_mesas, container, false);
         recyclerView = view.findViewById(R.id.recyclerViewMesa);
         buttonAdd = view.findViewById(R.id.buttonAdicionarMesa);
         referenceMesa = ConfiguracaoFirebase.getFirebaseDatabase().child("mesa");

         // config adapter
        adapterMesa = new AdapterMesa(list, getActivity());

        // config recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterMesa);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarMesa();
                adapterMesa.notifyDataSetChanged();
            }
        });

        swip();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarMesas();
    }

    @Override
    public void onStop() {
        super.onStop();
        referenceMesa.removeEventListener(listenerMesa);
    }

    public void swip(){
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags,swipFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                if (viewHolder.getAdapterPosition() < (list.size()-1)){
                    /*Toast toast = Toast.makeText(getActivity(),
                            "Exclua a ultima mesa da lista por favor!"
                    , Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.parseColor("#39B050"));
                    toast.show();*/
                    Context context = getActivity().getApplicationContext();
                    // Create layout inflator object to inflate toast.xml file
                    LayoutInflater inflater = getLayoutInflater();
                    String texto = "Exclua a ultima mesa da lista por favor.";
                    ToastConfig.showCustomAlert(context, inflater,texto);
                    adapterMesa.notifyDataSetChanged();
                }else {
                    Mesa mesa = list.get(viewHolder.getAdapterPosition());

                    mesaService.excluirMesa(mesa, referenceMesa, adapterMesa, getActivity());
                }
            }
        };
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }

    public void salvarMesa(){
        Mesa mesa = new Mesa();
       if (list.size() == 0){
        mesa.setNumeroMesa(0);
       }
       else {
           mesa.setNumeroMesa(list.size());
       }
       mesa.setStatus("livre");
       mesaService.salvarMesa(mesa);

    }

    public void recuperarMesas(){
        Query query = referenceMesa.orderByChild("numeroMesa");
        listenerMesa = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dados : snapshot.getChildren()){
                    Mesa mesa = dados.getValue(Mesa.class);
                    mesa.setKey(dados.getKey());
                    list.add(mesa);
                }
                adapterMesa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}
