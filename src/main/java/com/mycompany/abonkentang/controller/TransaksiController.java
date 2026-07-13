/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.abonkentang.controller;

import com.mycompany.abonkentang.config.koneksi;
import com.mycompany.abonkentang.model.DetailTransaksi;
import com.mycompany.abonkentang.model.Produk;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alfikho Azka Dinova - 10125107
 */
public class TransaksiController {

    private static final String SQL_DASAR_PRODUK =
            "SELECT p.id_produk, p.kode_produk, p.nama_produk, p.kategori, p.satuan, p.harga_jual, "
            + "COALESCE(s.jumlah_stok, 0) AS jumlah_stok "
            + "FROM produk p LEFT JOIN stok_produk s ON s.id_produk = p.id_produk ";

    public List<Produk> getSemuaProduk() {
        List<Produk> list = new ArrayList<>();
        String sql = SQL_DASAR_PRODUK + "ORDER BY p.nama_produk";

        try (Connection conn = koneksi.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowKeProduk(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal memuat data produk: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Produk> cariProduk(String keyword) {
        List<Produk> list = new ArrayList<>();
        String sql = SQL_DASAR_PRODUK + "WHERE p.kode_produk LIKE ? OR p.nama_produk LIKE ? ORDER BY p.nama_produk";

        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String pola = "%" + keyword + "%";
            ps.setString(1, pola);
            ps.setString(2, pola);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowKeProduk(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Pencarian produk gagal: " + e.getMessage(), e);
        }
        return list;
    }

    private Produk mapRowKeProduk(ResultSet rs) throws SQLException {
        Produk p = new Produk();
        p.setIdProduk(rs.getInt("id_produk"));
        p.setKodeProduk(rs.getString("kode_produk"));
        p.setNamaProduk(rs.getString("nama_produk"));
        p.setKategori(rs.getString("kategori"));
        p.setSatuan(rs.getString("satuan"));
        p.setHargaJual(rs.getDouble("harga_jual"));
        p.setStok(rs.getInt("jumlah_stok"));
        return p;
    }

    public int simpanTransaksi(int idUser, List<DetailTransaksi> keranjang) {
        if (keranjang == null || keranjang.isEmpty()) {
            throw new IllegalArgumentException("Keranjang masih kosong.");
        }

        double totalBayar = 0;
        for (DetailTransaksi item : keranjang) {
            totalBayar += item.getSubtotal();
        }

        String sqlInsertTransaksi = "INSERT INTO transaksi (id_user, total_bayar) VALUES (?, ?)";
        String sqlInsertDetail = "INSERT INTO detail_transaksi (id_transaksi, id_produk, jumlah, subtotal) VALUES (?, ?, ?, ?)";
        String sqlCekStok = "SELECT id_stok, jumlah_stok FROM stok_produk WHERE id_produk = ? FOR UPDATE";
        String sqlUpdateStok = "UPDATE stok_produk SET jumlah_stok = jumlah_stok - ? WHERE id_stok = ?";

        try (Connection conn = koneksi.getKoneksi()) {
            conn.setAutoCommit(false);
            int idTransaksiBaru;
            try {
                // 1. Simpan header transaksi
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertTransaksi, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, idUser);
                    ps.setDouble(2, totalBayar);
                    ps.executeUpdate();

                    try (ResultSet rsKey = ps.getGeneratedKeys()) {
                        if (rsKey.next()) {
                            idTransaksiBaru = rsKey.getInt(1);
                        } else {
                            throw new SQLException("Gagal mendapatkan id_transaksi baru.");
                        }
                    }
                }

                // 2. Simpan tiap item + kurangi stok, sambil validasi stok cukup atau tidak
                for (DetailTransaksi item : keranjang) {
                    try (PreparedStatement ps = conn.prepareStatement(sqlInsertDetail)) {
                        ps.setInt(1, idTransaksiBaru);
                        ps.setInt(2, item.getIdProduk());
                        ps.setInt(3, item.getJumlah());
                        ps.setDouble(4, item.getSubtotal());
                        ps.executeUpdate();
                    }

                    Integer idStok = null;
                    double stokTersedia = 0;
                    try (PreparedStatement ps = conn.prepareStatement(sqlCekStok)) {
                        ps.setInt(1, item.getIdProduk());
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                idStok = rs.getInt("id_stok");
                                stokTersedia = rs.getDouble("jumlah_stok");
                            }
                        }
                    }

                    if (idStok == null) {
                        throw new SQLException("Stok untuk produk \"" + item.getNamaProduk() + "\" belum tercatat.");
                    }
                    if (stokTersedia < item.getJumlah()) {
                        throw new SQLException("Stok \"" + item.getNamaProduk() + "\" tidak cukup. "
                                + "Sisa stok: " + stokTersedia + ", diminta: " + item.getJumlah());
                    }

                    try (PreparedStatement ps = conn.prepareStatement(sqlUpdateStok)) {
                        ps.setInt(1, item.getJumlah());
                        ps.setInt(2, idStok);
                        ps.executeUpdate();
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Gagal menyimpan transaksi: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
            return idTransaksiBaru;
        } catch (SQLException e) {
            throw new RuntimeException("Gagal terhubung ke database: " + e.getMessage(), e);
        }
    }
}
