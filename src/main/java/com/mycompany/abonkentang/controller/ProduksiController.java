/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.abonkentang.controller;

import com.mycompany.abonkentang.config.koneksi;
import com.mycompany.abonkentang.model.Produksi;
import com.mycompany.abonkentang.model.BahanBaku;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 *
 * @author Bintang Aziz Satrio - 10125042, Alfikho Azka Dinova - 10125107
 */
public class ProduksiController {

    public void tambahProduksi(Produksi p, Map<Integer, Double> bahanDipakai) {
        String sqlInsert = "INSERT INTO produksi (id_produk, jumlah_produksi, tanggal_produksi) VALUES (?, ?, ?)";

        try (Connection conn = koneksi.getKoneksi()) {
            conn.setAutoCommit(false);
        try {
            int idProduksiBaru;
            try (PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, p.getIdProduk());
                ps.setInt(2, p.getJumlahProduksi());
                ps.setDate(3, new java.sql.Date(p.getTanggalProduksi().getTime()));
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("Gagal mengambil ID produksi baru.");
                    }
                    idProduksiBaru = rs.getInt(1);
                }
            }

            konsumsiBahanBaku(conn, idProduksiBaru, bahanDipakai);

            tambahStokProduk(conn, p.getIdProduk(), p.getJumlahProduksi());

                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();
                    throw new RuntimeException("Gagal Tambah Data: " + e.getMessage(), e);
                } finally {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Gagal terhubung ke database: " + e.getMessage(), e);
            }
        }

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
            throw new RuntimeException("Gagal Memuat Data: " + e.getMessage(), e);
        }
        return list;
    }

    public void ubahProduksi(Produksi p, Map<Integer, Double> bahanDipakai) {
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

                batalkanKonsumsiBahanBaku(conn, p.getIdProduksi());
                konsumsiBahanBaku(conn, p.getIdProduksi(), bahanDipakai);

                tambahStokProduk(conn, idProdukLama, -jumlahLama);
                tambahStokProduk(conn, p.getIdProduk(), p.getJumlahProduksi());

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Gagal Ubah Data: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal terhubung ke database: " + e.getMessage(), e);
        }
    }

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

                batalkanKonsumsiBahanBaku(conn, idProduksi);

                try (PreparedStatement ps = conn.prepareStatement(sqlDelete)) {
                    ps.setInt(1, idProduksi);
                    ps.executeUpdate();
                }

                tambahStokProduk(conn, idProduk, -jumlah);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Gagal Hapus Data: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal terhubung ke database: " + e.getMessage(), e);
        }
    }
    
    private void konsumsiBahanBaku(Connection conn, int idProduksi, Map<Integer, Double> bahanDipakai) throws SQLException {
        if (bahanDipakai == null || bahanDipakai.isEmpty()) {
            return;
        }

        for (Map.Entry<Integer, Double> entry : bahanDipakai.entrySet()) {
            int idBahan = entry.getKey();
            double dibutuhkan = entry.getValue();

            String sqlCek = "SELECT stok_bahan, nama_bahan FROM bahan_baku WHERE id_bahan = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlCek)) {
                ps.setInt(1, idBahan);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Bahan baku dengan ID " + idBahan + " tidak ditemukan.");
                    }
                    double stokTersedia = rs.getDouble("stok_bahan");
                    if (stokTersedia < dibutuhkan) {
                        throw new SQLException("Stok bahan '" + rs.getString("nama_bahan")
                            + "' tidak cukup. Dibutuhkan " + dibutuhkan + ", tersedia " + stokTersedia + ".");
                    }
                }
            }
        }

        String sqlDetail = "INSERT INTO detail_produksi_bahan (id_produksi, id_bahan, jumlah_dipakai) VALUES (?, ?, ?)";
        String sqlKurangi = "UPDATE bahan_baku SET stok_bahan = stok_bahan - ? WHERE id_bahan = ?";

        for (Map.Entry<Integer, Double> entry : bahanDipakai.entrySet()) {
            int idBahan = entry.getKey();
            double dibutuhkan = entry.getValue();

            try (PreparedStatement ps = conn.prepareStatement(sqlDetail)) {
                ps.setInt(1, idProduksi);
                ps.setInt(2, idBahan);
                ps.setDouble(3, dibutuhkan);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlKurangi)) {
                ps.setDouble(1, dibutuhkan);
                ps.setInt(2, idBahan);
                ps.executeUpdate();
            }
        }
    }

    private void batalkanKonsumsiBahanBaku(Connection conn, int idProduksi) throws SQLException {
        String sqlAmbil = "SELECT id_bahan, jumlah_dipakai FROM detail_produksi_bahan WHERE id_produksi = ?";
        List<double[]> pemakaian = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sqlAmbil)) {
            ps.setInt(1, idProduksi);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pemakaian.add(new double[]{ rs.getInt("id_bahan"), rs.getDouble("jumlah_dipakai") });
                }
            }
        }

        String sqlKembalikan = "UPDATE bahan_baku SET stok_bahan = stok_bahan + ? WHERE id_bahan = ?";
        for (double[] d : pemakaian) {
            try (PreparedStatement ps = conn.prepareStatement(sqlKembalikan)) {
                ps.setDouble(1, d[1]);
                ps.setInt(2, (int) d[0]);
                ps.executeUpdate();
            }
        }

        String sqlHapusDetail = "DELETE FROM detail_produksi_bahan WHERE id_produksi = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlHapusDetail)) {
            ps.setInt(1, idProduksi);
            ps.executeUpdate();
        }
    }
    
    public Map<Integer, Double> getBahanDipakai(int idProduksi) {
        Map<Integer, Double> hasil = new LinkedHashMap<>();
        String sql = "SELECT id_bahan, jumlah_dipakai FROM detail_produksi_bahan WHERE id_produksi = ?";

        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProduksi);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hasil.put(rs.getInt("id_bahan"), rs.getDouble("jumlah_dipakai"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil data bahan baku produksi: " + e.getMessage(), e);
        }

        return hasil;
    }

    public List<BahanBaku> daftarBahanBaku() {
        List<BahanBaku> list = new ArrayList<>();
        String sql = "SELECT * FROM bahan_baku ORDER BY nama_bahan";
        try (Connection conn = koneksi.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new BahanBaku(
                    rs.getInt("id_bahan"),
                    rs.getString("nama_bahan"),
                    rs.getString("satuan"),
                    rs.getDouble("stok_bahan"),
                    rs.getDouble("harga_satuan")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal memuat data bahan baku: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Produksi> cariProduksi(String keyword, String kolom) {
        List<Produksi> list = new ArrayList<>();
        String sql;

        if (kolom.equals("nama_produk")) {
            sql = "SELECT pr.* FROM produksi pr "
                + "JOIN produk p ON p.id_produk = pr.id_produk "
                + "WHERE p.nama_produk LIKE ? ORDER BY pr.tanggal_produksi DESC";
        } else if (kolom.equals("tanggal_produksi")) {
            sql = "SELECT * FROM produksi WHERE tanggal_produksi LIKE ? ORDER BY tanggal_produksi DESC";
        } else {
            throw new IllegalArgumentException("Kolom pencarian tidak valid: " + kolom);
        }

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
            throw new RuntimeException("Pencarian gagal: " + e.getMessage(), e);
        }
        return list;
    }

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
