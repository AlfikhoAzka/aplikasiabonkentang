/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.abonkentang.controller;
import com.mycompany.abonkentang.config.koneksi;
import com.mycompany.abonkentang.model.LaporanProduksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alfikho Azka Dinova - 10125107
 */
public class LaporanController {

    public List<LaporanProduksi> getLaporanProduksi() {

        List<LaporanProduksi> list = new ArrayList<>();

        String sql =
                "SELECT pr.tanggal_produksi, " +
                "p.nama_produk, " +
                "pr.jumlah_produksi, " +
                "pr.keterangan " +
                "FROM produksi pr " +
                "JOIN produk p ON pr.id_produk = p.id_produk " +
                "ORDER BY pr.tanggal_produksi";

        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                LaporanProduksi lp = new LaporanProduksi();

                lp.setTanggalProduksi(rs.getDate("tanggal_produksi"));
                lp.setNamaProduk(rs.getString("nama_produk"));
                lp.setJumlahProduksi(rs.getInt("jumlah_produksi"));
                lp.setKeterangan(rs.getString("keterangan"));

                list.add(lp);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Gagal Memuat Laporan Produksi: " + e.getMessage(), e);
        }

        return list;
    }
}

