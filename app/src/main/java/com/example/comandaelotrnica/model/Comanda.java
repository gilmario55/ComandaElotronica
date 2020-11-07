package com.example.comandaelotrnica.model;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import com.example.comandaelotrnica.config.ConfiguracaoFirebase;

public class Comanda {

   private String idUsuario;
   private String idEmpresa;
   private String idMesa;
   private String idComanda;
   private String nomeUsuario;
   private List<ItensComanda> itens;
   private double total;
   private String status = "abreta";
   private String metodoPagamento;
   private String obs;

    public Comanda() {
    }

    public Comanda(String idUsuario, String idEmpresa, String idMesa) {
        this.idUsuario = idUsuario;
        this.idEmpresa = idEmpresa;
        this.idMesa = idMesa;

        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference reference = databaseReference
                .child("comanda")
                .child(getIdEmpresa())
                .child(getIdUsuario())
                .child(getIdMesa());
        setIdComanda(reference.push().getKey());
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

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

    public List<ItensComanda> getItens() {
        return itens;
    }

    public void setItens(List<ItensComanda> itens) {
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

    public String getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(String idMesa) {
        this.idMesa = idMesa;
    }
}
