package com.example.dam_af;

public class LocalPlace {
    private int id;
    private String nome;
    private String categoria;
    private String observacao;
    private double latitude;
    private double longitude;

    public LocalPlace(
            int id,
            String nome,
            String categoria,
            String observacao,
            double latitude,
            double longitude) {

        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.observacao = observacao;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getObservacao() {
        return observacao;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
