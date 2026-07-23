/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.abonkentang.model;
import java.util.Date;

/**
 *
 * @author Alfikho Azka Dinova - 10125107
 */
public class LaporanProduksi {

    private Date tanggalProduksi;
    private String namaProduk;
    private int jumlahProduksi;

    public Date getTanggalProduksi() {
        return tanggalProduksi;
    }

    public void setTanggalProduksi(Date tanggalProduksi) {
        this.tanggalProduksi = tanggalProduksi;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    public int getJumlahProduksi() {
        return jumlahProduksi;
    }

    public void setJumlahProduksi(int jumlahProduksi) {
        this.jumlahProduksi = jumlahProduksi;
    }
}
