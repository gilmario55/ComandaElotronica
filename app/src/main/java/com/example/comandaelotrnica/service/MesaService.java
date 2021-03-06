package com.example.comandaelotrnica.service;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.example.comandaelotrnica.adapter.AdapterMesa;
import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.example.comandaelotrnica.model.Mesa;
import com.example.comandaelotrnica.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;

public class MesaService {

    public void salvarMesa(Mesa mesa){
        DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
        String idEmpresa = UsuarioFirebase.getIdentificacaoUsuario();

        reference.child("mesa")
                .child(idEmpresa)
                .push()
                .setValue(mesa);
    }

    public void excluirMesa(final Mesa mesa, final DatabaseReference reference,
                            final AdapterMesa adapterMesa, final Context context, final int position){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Excluir Mesa.");
        alertDialog.setMessage("Vocẽ tem certeza que deseja realmente excluir essa mesa?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reference
                        .child(mesa.getIdEmpresa())
                        .child(mesa.getIdMesa()).removeValue();
                adapterMesa.notifyItemRemoved(position);
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context,
                        "Exclusão cancelada",
                        Toast.LENGTH_SHORT).show();
                adapterMesa.notifyDataSetChanged();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();



    }
}
