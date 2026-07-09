/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.abonkentang.controller;

import com.mycompany.abonkentang.config.koneksi;
import com.mycompany.abonkentang.model.Produk;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

/**
 *
 * @author Kelompok 6
 */
public class ProdukController {
    
    private Connection conn;

    public ProdukController() {
        conn = koneksi.getKoneksi();
    }
    public ResultSet getDataProduk() {
        try {
            Statement st = conn.createStatement();
            String sql = "SELECT * FROM produk";
            return st.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Gagal mengambil data: " + e.getMessage());
            return null;
        }
    }   
    public boolean simpanProduk(Produk produk) {

    try {

        String sql = "INSERT INTO produk(kode_produk,nama_produk,kategori,satuan,harga_jual) VALUES (?,?,?,?,?)";

        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, produk.getKodeProduk());
        ps.setString(2, produk.getNamaProduk());
        ps.setString(3, produk.getKategori());
        ps.setString(4, produk.getSatuan());
        ps.setDouble(5, produk.getHargaJual());

        ps.executeUpdate();

        return true;

    } catch (SQLException e) {

        System.out.println("Gagal menyimpan data : " + e.getMessage());

        return false;
    }
}  

} 