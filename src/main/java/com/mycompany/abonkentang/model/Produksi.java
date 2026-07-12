/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.abonkentang.model;

import java.util.Date;
/**
 *
 * @author Bintang Aziz Satrio - 10125042, Alfikho Azka Dinova - 10125107
 */
public class Produksi {
    private int idProduksi;
    private int idProduk;
    private int jumlahProduksi;
    private Date tanggalProduksi;

    // Constructor Kosong
    public Produksi() {}

    // Constructor Lengkap (tanpa idProduksi, karena kolomnya AUTO_INCREMENT)
    public Produksi(int idProduk, int jumlahProduksi, Date tanggalProduksi) {
        this.idProduk = idProduk;
        this.jumlahProduksi = jumlahProduksi;
        this.tanggalProduksi = tanggalProduksi;
    }

    // Constructor Lengkap (dipakai saat membaca data dari database, idProduksi sudah ada)
    public Produksi(int idProduksi, int idProduk, int jumlahProduksi, Date tanggalProduksi) {
        this.idProduksi = idProduksi;
        this.idProduk = idProduk;
        this.jumlahProduksi = jumlahProduksi;
        this.tanggalProduksi = tanggalProduksi;
    }

    // Getter dan Setter
    public int getIdProduksi() { return idProduksi; }
    public void setIdProduksi(int idProduksi) { this.idProduksi = idProduksi; }

    public int getIdProduk() { return idProduk; }
    public void setIdProduk(int idProduk) { this.idProduk = idProduk; }

    public int getJumlahProduksi() { return jumlahProduksi; }
    public void setJumlahProduksi(int jumlahProduksi) { this.jumlahProduksi = jumlahProduksi; }

    public Date getTanggalProduksi() { return tanggalProduksi; }
    public void setTanggalProduksi(Date tanggalProduksi) { this.tanggalProduksi = tanggalProduksi; }
}
