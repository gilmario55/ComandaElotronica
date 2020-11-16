package com.example.comandaelotrnica.model;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import com.example.comandaelotrnica.config.ConfiguracaoFirebase;
import com.google.firebase.database.Exclude;

public class Comanda {

   private String idUsuario;
   private String idEmpresa;
    private String idComanda;
   private String nomeUsuario;
   private List<ItemComanda> itens;
   private String status = "aberta";
   private String metodoPagamento;
   private String obs;
   private String dataComanda;
    private int numeroMesa;
    private double total;

    public Comanda() {
    }

    public Comanda(String idUsuario, String idEmpresa) {
        this.idUsuario = idUsuario;
        this.idEmpresa = idEmpresa;

        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference reference = databaseReference
                .child("comanda")
                .child(getIdUsuario())
                .child(getIdEmpresa());
        setIdComanda(reference.push().getKey());
    }

    @Exclude
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Exclude
    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getIdComanda() {
        return idComanda;
    }

    public void setIdComanda(String idComanda) {
        this.idComanda = idComanda;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public List<ItemComanda> getItens() {
        return itens;
    }

    public void setItens(List<ItemComanda> itens) {
        this.itens = itens;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getDataComanda() {
        return dataComanda;
    }

    public void setDataComanda(String dataComanda) {
        this.dataComanda = dataComanda;
    }

    public int getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(int numeroMesa) {
        this.numeroMesa = numeroMesa;
    }
}
