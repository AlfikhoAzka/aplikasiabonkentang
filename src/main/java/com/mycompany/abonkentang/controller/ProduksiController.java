/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.abonkentang.controller;

import com.mycompany.abonkentang.config.koneksi;
import com.mycompany.abonkentang.model.Produksi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Bintang Aziz Satrio - 10125042
 */
public class ProduksiController {
    // 1. Tambah Data
    public void tambahProduksi(Produksi p) {
        String sql = "INSERT INTO produksi (id_produksi, id_produk, jumlah_produksi, tanggal) VALUES (?, ?, ?, ?)";
        try (Connection conn = koneksi.getKoneksi(); // Menyesuaikan kelas koneksi Anda
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, p.getIdProduksi());
            ps.setString(2, p.getIdProduk());
            ps.setInt(3, p.getJumlahProduksi());
            ps.setDate(4, new java.sql.Date(p.getTanggalProduksi().getTime()));
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data Produksi Berhasil Ditambahkan!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal Tambah Data: " + e.getMessage());
        }
    }

    // 2. Tampil Data
    public List<Produksi> tampilProduksi() {
        List<Produksi> list = new ArrayList<>();
        String sql = "SELECT * FROM produksi";
        try (Connection conn = koneksi.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                Produksi p = new Produksi(
                    rs.getString("id_produksi"),
                    rs.getString("id_produk"),
                    rs.getInt("jumlah_produksi"),
                    rs.getDate("tanggal")
                );
                list.add(p);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal Memuat Data: " + e.getMessage());
        }
        return list;
    }

    // 3. Ubah Data
    public void ubahProduksi(Produksi p) {
        String sql = "UPDATE produksi SET id_produk=?, jumlah_produksi=?, tanggal=? WHERE id_produksi=?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, p.getIdProduk());
            ps.setInt(2, p.getJumlahProduksi());
            ps.setDate(3, new java.sql.Date(p.getTanggalProduksi().getTime()));
            ps.setString(4, p.getIdProduksi());
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data Produksi Berhasil Diubah!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal Ubah Data: " + e.getMessage());
        }
    }

    // 4. Hapus Data
    public void hapusProduksi(String idProduksi) {
        String sql = "DELETE FROM produksi WHERE id_produksi=?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, idProduksi);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data Produksi Berhasil Dihapus!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal Hapus Data: " + e.getMessage());
        }
    }

    // 5. Cari Data (Berdasarkan ID atau ID Produk)
    public List<Produksi> cariProduksi(String keyword, String kategori) {
        List<Produksi> list = new ArrayList<>();
        String sql = "SELECT * FROM produksi WHERE " + kategori + " LIKE ?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produksi p = new Produksi(
                        rs.getString("id_produksi"),
                        rs.getString("id_produk"),
                        rs.getInt("jumlah_produksi"),
                        rs.getDate("tanggal")
                    );
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Pencarian Gagal: " + e.getMessage());
        }
        return list;
    }
}
