/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.abonkentang.model;

import java.util.Date;

/**
 *
 * @author Kelompok 6
 */
public class Transaksi {
    private int idTransaksi;
    private Date tanggal;
    private int idUser;
    private double totalBayar;

    // Field bantu (bukan kolom tabel), diisi lewat JOIN ke tabel user untuk ditampilkan di tabel riwayat
    private String namaKasir;

    public Transaksi() {}

    // Constructor untuk transaksi baru (idTransaksi & tanggal otomatis dari database)
    public Transaksi(int idUser, double totalBayar) {
        this.idUser = idUser;
        this.totalBayar = totalBayar;
    }

    // Constructor lengkap, dipakai saat membaca data dari database
    public Transaksi(int idTransaksi, Date tanggal, int idUser, double totalBayar) {
        this.idTransaksi = idTransaksi;
        this.tanggal = tanggal;
        this.idUser = idUser;
        this.totalBayar = totalBayar;
    }

    public int getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(int idTransaksi) { this.idTransaksi = idTransaksi; }

    public Date getTanggal() { return tanggal; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public double getTotalBayar() { return totalBayar; }
    public void setTotalBayar(double totalBayar) { this.totalBayar = totalBayar; }

    public String getNamaKasir() { return namaKasir; }
    public void setNamaKasir(String namaKasir) { this.namaKasir = namaKasir; }
}
