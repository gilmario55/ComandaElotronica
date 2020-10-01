package com.example.comandaelotrnica.model;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Cardapio implements Serializable {

    private String nome;
    private String categoria;
    private String tipoBebida;
    private String dataCastrato;
    private String idUsuario;
    private String foto;
    private double preco;
    private String descricao;
    private String key;

    public Cardapio() {
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
        return dataCastrato;
    }

    public void setDataCastrato(String dataCastrato) {
        this.dataCastrato = dataCastrato;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double valor) {
        this.preco = valor;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTipoBebida() {
        return tipoBebida;
    }

    public void setTipoBebida(String tipoBebida) {
        this.tipoBebida = tipoBebida;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
