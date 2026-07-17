/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.abonkentang.controller;
import com.mycompany.abonkentang.config.koneksi;
import com.mycompany.abonkentang.model.Stok;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alfikho Azka Dinova - 10125107
 */
public class StokController {

    public List<Stok> getDataStok() {

        List<Stok> list = new ArrayList<>();

        String sql =
                "SELECT s.id_stok, " +
                "p.nama_produk, " +
                "p.satuan, " +
                "s.jumlah_stok, " +
                "s.tanggal_update " +
                "FROM stok_produk s " +
                "JOIN produk p ON s.id_produk = p.id_produk " +
                "ORDER BY p.nama_produk";

        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Stok stok = new Stok();

                stok.setIdStok(rs.getInt("id_stok"));
                stok.setNamaProduk(rs.getString("nama_produk"));
                stok.setSatuan(rs.getString("satuan"));
                stok.setJumlahStok(rs.getInt("jumlah_stok"));
                stok.setTanggalUpdate(rs.getTimestamp("tanggal_update"));

                list.add(stok);
            }

        } catch (Exception e) {
            System.out.println("Gagal mengambil data stok : " + e.getMessage());
        }

        return list;
    }
    
    
   public List<Stok> cariStok(String keyword) {

        List<Stok> list = new ArrayList<>();

        String sql =
                "SELECT s.id_stok, " +
                "p.nama_produk, " +
                "p.satuan, " +
                "s.jumlah_stok, " +
                "s.tanggal_update " +
                "FROM stok_produk s " +
                "JOIN produk p ON s.id_produk = p.id_produk " +
                "WHERE p.nama_produk LIKE ? " +
                "ORDER BY p.nama_produk";

        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    Stok stok = new Stok();

                    stok.setIdStok(rs.getInt("id_stok"));
                    stok.setNamaProduk(rs.getString("nama_produk"));
                    stok.setSatuan(rs.getString("satuan"));
                    stok.setJumlahStok(rs.getInt("jumlah_stok"));
                    stok.setTanggalUpdate(rs.getTimestamp("tanggal_update"));

                    list.add(stok);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Pencarian gagal: " + e.getMessage(), e);
        }

        return list;
    }
}
