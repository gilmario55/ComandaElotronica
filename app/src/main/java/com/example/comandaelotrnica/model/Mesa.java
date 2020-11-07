package com.example.comandaelotrnica.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Mesa implements Serializable {

    private String idEmpresa;
    private String idMesa;
    private int numeroMesa;
    private String status;

    public int getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(int numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public String getIdMesa() {
        return idMesa;
    }

    @Exclude
    public void setIdMesa(String key) {
        this.idMesa = key;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
