package com.example.comandaelotrnica.model;

public class ItemComanda {

    private String idCardapio;
    private String nomeItem;
    private String statusItem;
    private int quantidade;
    private double precoTotal;
    private double precoUnitario;

    public ItemComanda() {
    }

    public String getIdCardapio() {
        return idCardapio;
    }

    public void setIdCardapio(String idCardapio) {
        this.idCardapio = idCardapio;
    }

    public String getNomeItem() {
        return nomeItem;
    }

    public void setNomeItem(String nomeItem) {
        this.nomeItem = nomeItem;
    }

    public String getStatusItem() {
        return statusItem;
    }

    public void setStatusItem(String statusItem) {
        this.statusItem = statusItem;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getPrecoTotal() {
        return precoTotal;
    }

    public void setPrecoTotal(double preco) {
        this.precoTotal = preco;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
}
