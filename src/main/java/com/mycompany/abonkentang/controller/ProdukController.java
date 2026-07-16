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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rassya Haikal Firdaus - 10125037, Alfikho Azka Dinova - 10125107
 */
public class ProdukController {

    public List<Produk> getDataProduk() {
        List<Produk> list = new ArrayList<>();
        String sql = "SELECT * FROM produk";

        try (Connection conn = koneksi.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Produk produk = new Produk();
                produk.setIdProduk(rs.getInt("id_produk"));
                produk.setKodeProduk(rs.getString("kode_produk"));
                produk.setNamaProduk(rs.getString("nama_produk"));
                produk.setKategori(rs.getString("kategori"));
                produk.setSatuan(rs.getString("satuan"));
                produk.setHargaJual(rs.getDouble("harga_jual"));
                list.add(produk);
            }

        } catch (SQLException e) {
            System.out.println("Gagal mengambil data: " + e.getMessage());
        }
        return list;
    }

    public boolean simpanProduk(Produk produk) {

        String sql = "INSERT INTO produk(kode_produk,nama_produk,kategori,satuan,harga_jual) VALUES (?,?,?,?,?)";

        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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

    public boolean updateProduk(String kodeProdukLama, Produk produk) {
        String sql = "UPDATE produk SET kode_produk=?, nama_produk=?, kategori=?, satuan=?, harga_jual=? "
                + "WHERE kode_produk=?";

        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, produk.getKodeProduk());
            ps.setString(2, produk.getNamaProduk());
            ps.setString(3, produk.getKategori());
            ps.setString(4, produk.getSatuan());
            ps.setDouble(5, produk.getHargaJual());
            ps.setString(6, kodeProdukLama);

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Gagal mengubah data : " + e.getMessage());
            return false;
        }
    }

    public boolean hapusProduk(String kodeProduk) {
        String sql = "DELETE FROM produk WHERE kode_produk=?";

        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, kodeProduk);

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Gagal menghapus data : " + e.getMessage());
            return false;
        }
    }
    
    public java.util.List<Produk> cariProduk(String keyword, String kolom) {
        java.util.List<Produk> list = new ArrayList<>();
        java.util.List<String> kolomDiizinkan = java.util.List.of("kode_produk", "nama_produk", "kategori");

        if (!kolomDiizinkan.contains(kolom)) {
            throw new IllegalArgumentException("Kolom pencarian tidak valid: " + kolom);
        }

        String sql = "SELECT * FROM produk WHERE " + kolom + " LIKE ? ORDER BY nama_produk";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produk p = new Produk();
                    p.setIdProduk(rs.getInt("id_produk"));
                    p.setKodeProduk(rs.getString("kode_produk"));
                    p.setNamaProduk(rs.getString("nama_produk"));
                    p.setKategori(rs.getString("kategori"));
                    p.setSatuan(rs.getString("satuan"));
                    p.setHargaJual(rs.getDouble("harga_jual"));
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Pencarian gagal: " + e.getMessage(), e);
        }
        return list;
    }

}