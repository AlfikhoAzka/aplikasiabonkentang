/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.abonkentang.model;

/**
 * Merepresentasikan satu baris item di keranjang / detail_transaksi.
 * Dipakai baik untuk item yang belum disimpan (keranjang di memori)
 * maupun item yang sudah dibaca dari database.
 *
 * @author Alfikho Azka
 */
public class DetailTransaksi {

    private int idDetail;
    private int idTransaksi;
    private int idProduk;

    // Field bantu (bukan kolom tabel), diisi dari JOIN ke tabel produk supaya mudah ditampilkan di tabel keranjang
    private String kodeProduk;
    private String namaProduk;
    private double hargaSatuan;

    private int jumlah;
    private double subtotal;

    public DetailTransaksi() {
    }

    // Constructor untuk item baru di keranjang (sebelum transaksi disimpan)
    public DetailTransaksi(int idProduk, String kodeProduk, String namaProduk, double hargaSatuan, int jumlah) {
        this.idProduk = idProduk;
        this.kodeProduk = kodeProduk;
        this.namaProduk = namaProduk;
        this.hargaSatuan = hargaSatuan;
        this.jumlah = jumlah;
        this.subtotal = hargaSatuan * jumlah;
    }

    public int getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(int idDetail) {
        this.idDetail = idDetail;
    }

    public int getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(int idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public int getIdProduk() {
        return idProduk;
    }

    public void setIdProduk(int idProduk) {
        this.idProduk = idProduk;
    }

    public String getKodeProduk() {
        return kodeProduk;
    }

    public void setKodeProduk(String kodeProduk) {
        this.kodeProduk = kodeProduk;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    public double getHargaSatuan() {
        return hargaSatuan;
    }

    public void setHargaSatuan(double hargaSatuan) {
        this.hargaSatuan = hargaSatuan;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
        this.subtotal = this.hargaSatuan * jumlah;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
