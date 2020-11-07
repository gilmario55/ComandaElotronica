package com.example.comandaelotrnica.model;

import com.google.firebase.database.Exclude;

public class CategoriaCardapio {
    private String idEmpresa;
    private String idCategoria;
    private String nome;


    @Exclude
    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    @Exclude
    public String getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(String id) {
        this.idCategoria = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
