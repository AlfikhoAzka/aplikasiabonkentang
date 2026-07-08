/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.abonkentang.model;

/**
 *
 * @author Kelompok 6
 */
public class BahanBaku {

    private int idBahan;
    private String namaBahan;
    private String satuan;
    private double stokBahan;
    private double hargaSatuan;

    public BahanBaku() {
    }

    public BahanBaku(int idBahan, String namaBahan, String satuan, double stokBahan, double hargaSatuan) {
        this.idBahan = idBahan;
        this.namaBahan = namaBahan;
        this.satuan = satuan;
        this.stokBahan = stokBahan;
        this.hargaSatuan = hargaSatuan;
    }

    public int getIdBahan() {
        return idBahan;
    }

    public void setIdBahan(int idBahan) {
        this.idBahan = idBahan;
    }

    public String getNamaBahan() {
        return namaBahan;
    }

    public void setNamaBahan(String namaBahan) {
        this.namaBahan = namaBahan;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public double getStokBahan() {
        return stokBahan;
    }

    public void setStokBahan(double stokBahan) {
        this.stokBahan = stokBahan;
    }

    public double getHargaSatuan() {
        return hargaSatuan;
    }

    public void setHargaSatuan(double hargaSatuan) {
        this.hargaSatuan = hargaSatuan;
    }

    @Override
    public String toString() {
        return namaBahan;
    }
}
