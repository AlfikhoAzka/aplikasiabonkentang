/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.abonkentang.model;

import java.util.Date;
/**
 *
 * @author Bintang Aziz Satrio - 10125042
 */
public class Produksi {
    private String idProduksi;
    private String idProduk;
    private int jumlahProduksi;
    private Date tanggalProduksi;

    // Constructor Kosong
    public Produksi() {}

    // Constructor Lengkap
    public Produksi(String idProduksi, String idProduk, int jumlahProduksi, Date tanggalProduksi) {
        this.idProduksi = idProduksi;
        this.idProduk = idProduk;
        this.jumlahProduksi = jumlahProduksi;
        this.tanggalProduksi = tanggalProduksi;
    }

    // Getter dan Setter
    public String getIdProduksi() { return idProduksi; }
    public void setIdProduksi(String idProduksi) { this.idProduksi = idProduksi; }

    public String getIdProduk() { return idProduk; }
    public void setIdProduk(String idProduk) { this.idProduk = idProduk; }

    public int getJumlahProduksi() { return jumlahProduksi; }
    public void setJumlahProduksi(int jumlahProduksi) { this.jumlahProduksi = jumlahProduksi; }

    public Date getTanggalProduksi() { return tanggalProduksi; }
    public void setTanggalProduksi(Date tanggalProduksi) { this.tanggalProduksi = tanggalProduksi; }
}
