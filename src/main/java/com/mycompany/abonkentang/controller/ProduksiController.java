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
 * @author Bintang Aziz Satrio - 10125042, Alfikho Azka Dinova - 10125107
 */
public class ProduksiController {

    // Kolom yang boleh dipakai untuk pencarian (whitelist, mencegah SQL Injection)
    private static final List<String> KOLOM_DIIZINKAN = List.of("id_produksi", "id_produk");

    // 1. Tambah Data (id_produksi tidak diisi manual karena AUTO_INCREMENT di database)
    public void tambahProduksi(Produksi p) {
        String sqlInsert = "INSERT INTO produksi (id_produk, jumlah_produksi, tanggal_produksi) VALUES (?, ?, ?)";

        try (Connection conn = koneksi.getKoneksi()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                    ps.setInt(1, p.getIdProduk());
                    ps.setInt(2, p.getJumlahProduksi());
                    ps.setDate(3, new java.sql.Date(p.getTanggalProduksi().getTime()));
                    ps.executeUpdate();
                }

                tambahStokProduk(conn, p.getIdProduk(), p.getJumlahProduksi());

                conn.commit();
                JOptionPane.showMessageDialog(null, "Data Produksi Berhasil Ditambahkan! Stok produk otomatis bertambah.");
            } catch (SQLException e) {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Gagal Tambah Data: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal terhubung ke database: " + e.getMessage());
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
                    rs.getInt("id_produksi"),
                    rs.getInt("id_produk"),
                    rs.getInt("jumlah_produksi"),
                    rs.getDate("tanggal_produksi")
                );
                list.add(p);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal Memuat Data: " + e.getMessage());
        }
        return list;
    }

    // 3. Ubah Data (stok_produk disesuaikan: stok produk lama dikurangi, stok produk baru ditambah)
    public void ubahProduksi(Produksi p) {
        String sqlAmbilLama = "SELECT id_produk, jumlah_produksi FROM produksi WHERE id_produksi = ?";
        String sqlUpdate = "UPDATE produksi SET id_produk=?, jumlah_produksi=?, tanggal_produksi=? WHERE id_produksi=?";

        try (Connection conn = koneksi.getKoneksi()) {
            conn.setAutoCommit(false);
            try {
                int idProdukLama;
                int jumlahLama;
                try (PreparedStatement ps = conn.prepareStatement(sqlAmbilLama)) {
                    ps.setInt(1, p.getIdProduksi());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Data produksi dengan ID " + p.getIdProduksi() + " tidak ditemukan.");
                        }
                        idProdukLama = rs.getInt("id_produk");
                        jumlahLama = rs.getInt("jumlah_produksi");
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                    ps.setInt(1, p.getIdProduk());
                    ps.setInt(2, p.getJumlahProduksi());
                    ps.setDate(3, new java.sql.Date(p.getTanggalProduksi().getTime()));
                    ps.setInt(4, p.getIdProduksi());
                    ps.executeUpdate();
                }

                // Batalkan efek stok dari data produksi yang lama, lalu terapkan efek stok yang baru
                tambahStokProduk(conn, idProdukLama, -jumlahLama);
                tambahStokProduk(conn, p.getIdProduk(), p.getJumlahProduksi());

                conn.commit();
                JOptionPane.showMessageDialog(null, "Data Produksi Berhasil Diubah! Stok produk otomatis disesuaikan.");
            } catch (SQLException e) {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Gagal Ubah Data: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal terhubung ke database: " + e.getMessage());
        }
    }

    // 4. Hapus Data (stok_produk dikurangi sejumlah produksi yang dihapus)
    public void hapusProduksi(int idProduksi) {
        String sqlAmbilLama = "SELECT id_produk, jumlah_produksi FROM produksi WHERE id_produksi = ?";
        String sqlDelete = "DELETE FROM produksi WHERE id_produksi=?";

        try (Connection conn = koneksi.getKoneksi()) {
            conn.setAutoCommit(false);
            try {
                int idProduk;
                int jumlah;
                try (PreparedStatement ps = conn.prepareStatement(sqlAmbilLama)) {
                    ps.setInt(1, idProduksi);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Data produksi dengan ID " + idProduksi + " tidak ditemukan.");
                        }
                        idProduk = rs.getInt("id_produk");
                        jumlah = rs.getInt("jumlah_produksi");
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(sqlDelete)) {
                    ps.setInt(1, idProduksi);
                    ps.executeUpdate();
                }

                tambahStokProduk(conn, idProduk, -jumlah);

                conn.commit();
                JOptionPane.showMessageDialog(null, "Data Produksi Berhasil Dihapus! Stok produk otomatis dikurangi.");
            } catch (SQLException e) {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Gagal Hapus Data: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal terhubung ke database: " + e.getMessage());
        }
    }

    // 5. Cari Data (Berdasarkan ID Produksi atau ID Produk)
    public List<Produksi> cariProduksi(String keyword, String kolom) {
        List<Produksi> list = new ArrayList<>();

        // Whitelist nama kolom agar tidak bisa disuntik lewat parameter "kolom"
        if (!KOLOM_DIIZINKAN.contains(kolom)) {
            JOptionPane.showMessageDialog(null, "Kolom pencarian tidak valid: " + kolom);
            return list;
        }

        String sql = "SELECT * FROM produksi WHERE " + kolom + " LIKE ?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produksi p = new Produksi(
                        rs.getInt("id_produksi"),
                        rs.getInt("id_produk"),
                        rs.getInt("jumlah_produksi"),
                        rs.getDate("tanggal_produksi")
                    );
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Pencarian Gagal: " + e.getMessage());
        }
        return list;
    }

    /**
     * Menambah (atau mengurangi, kalau delta negatif) jumlah_stok di stok_produk
     * untuk id_produk tertentu. Kalau baris stok belum ada, dibuatkan baris baru.
     * Method ini bagian dari transaksi yang sama dengan pemanggilnya (conn TIDAK di-commit/close di sini).
     */
    private void tambahStokProduk(Connection conn, int idProduk, int delta) throws SQLException {
        String sqlCek = "SELECT id_stok FROM stok_produk WHERE id_produk = ? LIMIT 1";
        Integer idStok = null;

        try (PreparedStatement ps = conn.prepareStatement(sqlCek)) {
            ps.setInt(1, idProduk);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idStok = rs.getInt("id_stok");
                }
            }
        }

        if (idStok != null) {
            String sqlUpdate = "UPDATE stok_produk SET jumlah_stok = jumlah_stok + ? WHERE id_stok = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setInt(1, delta);
                ps.setInt(2, idStok);
                ps.executeUpdate();
            }
        } else {
            String sqlInsert = "INSERT INTO stok_produk (id_produk, jumlah_stok) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setInt(1, idProduk);
                ps.setInt(2, delta);
                ps.executeUpdate();
            }
        }
    }
}
