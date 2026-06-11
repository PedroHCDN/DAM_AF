package com.example.dam_af;

public class Place {
    private String nome;
    private String endereco;
    private String tipo;
    private String distancia;
    private double latitude;
    private double longitude;

    public Place(String nome, String endereco, String tipo, String distancia,  double latitude, double longitude) {
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
        this.distancia = distancia;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getTipo() { return tipo; }

    public String getDistancia() { return distancia; }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }
}
