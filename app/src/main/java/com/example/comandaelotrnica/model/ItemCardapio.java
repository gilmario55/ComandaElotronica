package com.example.comandaelotrnica.model;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class ItemCardapio implements Serializable {

    private String idEmpresa;
    private String idItemCardapio;
    private String nome;
    private String categoria;
    private String dataCadastrato;
    private String foto;
    private double preco;
    private String descricao;

    public ItemCardapio() {
    }

    @Exclude
    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idUsuario) {
        this.idEmpresa = idUsuario;
    }

    public String getIdItemCardapio() {
        return idItemCardapio;
    }

    public void setIdItemCardapio(String idItemCardapio) {
        this.idItemCardapio = idItemCardapio;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Exclude
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDataCastrato() {
        return dataCadastrato;
    }

    public void setDataCastrato(String dataCastrato) {
        this.dataCadastrato = dataCastrato;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
