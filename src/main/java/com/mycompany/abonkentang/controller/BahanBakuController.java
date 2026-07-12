/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.abonkentang.controller;
import com.mycompany.abonkentang.config.koneksi;
import com.mycompany.abonkentang.model.BahanBaku;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Alfikho Azka Dinova - 10125107
 */public class BahanBakuController {

    // Menampilkan semua data ke JTable
    public void tampilData(JTable table) {
        String sql = "SELECT * FROM bahan_baku ORDER BY nama_bahan";
        try (Connection conn = koneksi.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("Nama Bahan");
            model.addColumn("Satuan");
            model.addColumn("Stok");
            model.addColumn("Harga Satuan");

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_bahan"),
                    rs.getString("nama_bahan"),
                    rs.getString("satuan"),
                    (int) rs.getDouble("stok_bahan"),
                    "Rp " + String.format("%,.0f", rs.getDouble("harga_satuan"))
                };
                model.addRow(row);
            }

            table.setModel(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal menampilkan data: " + e.getMessage());
        }
    }

    // Menambah data baru
    public void simpanData(BahanBaku bb) {
        String sql = "INSERT INTO bahan_baku (nama_bahan, satuan, stok_bahan, harga_satuan) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection conn = koneksi.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bb.getNamaBahan());
            ps.setString(2, bb.getSatuan());
            ps.setDouble(3, bb.getStokBahan());
            ps.setDouble(4, bb.getHargaSatuan());

            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Data berhasil disimpan");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data: " + e.getMessage());
        }
    }

    // Menghapus data
    public void hapusData(int idBahan) {
        String sql = "DELETE FROM bahan_baku WHERE id_bahan=?";
        try (Connection conn = koneksi.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBahan);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Data berhasil dihapus");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus data: " + e.getMessage());
        }
    }

    // Pencarian berdasarkan nama bahan
    public void cariByNama(JTable table, String keyword) {
        String sql = "SELECT * FROM bahan_baku WHERE nama_bahan LIKE ? ORDER BY nama_bahan";
        try (Connection conn = koneksi.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("Nama Bahan");
            model.addColumn("Satuan");
            model.addColumn("Stok");
            model.addColumn("Harga Satuan");

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_bahan"),
                    rs.getString("nama_bahan"),
                    rs.getString("satuan"),
                    (int) rs.getDouble("stok_bahan"),
                    "Rp " + String.format("%,.0f", rs.getDouble("harga_satuan"))
                };
                model.addRow(row);
            }

            table.setModel(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal mencari data: " + e.getMessage());
        }
    }
    
    // Mengambil satu data berdasarkan ID, dipakai untuk keperluan edit
    public BahanBaku getById(int idBahan) {
        BahanBaku bb = null;
        String sql = "SELECT * FROM bahan_baku WHERE id_bahan = ?";
        try (Connection conn = koneksi.getKoneksi();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBahan);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                bb = new BahanBaku();
                bb.setIdBahan(rs.getInt("id_bahan"));
                bb.setNamaBahan(rs.getString("nama_bahan"));
                bb.setSatuan(rs.getString("satuan"));
                bb.setStokBahan(rs.getDouble("stok_bahan"));
                bb.setHargaSatuan(rs.getDouble("harga_satuan"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data: " + e.getMessage());
        }
        return bb;
    }
    
    public void updateData(BahanBaku bb) {
         String sql = "UPDATE bahan_baku "
                   + "SET nama_bahan=?, "
                   + "satuan=?, "
                   + "stok_bahan=?, "
                   + "harga_satuan=? "
                   + "WHERE id_bahan=?";

        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bb.getNamaBahan());
            ps.setString(2, bb.getSatuan());
            ps.setDouble(3, bb.getStokBahan());
            ps.setDouble(4, bb.getHargaSatuan());
            ps.setInt(5, bb.getIdBahan());

            ps.executeUpdate();

            JOptionPane.showMessageDialog(null,
                    "Data berhasil diperbarui.");

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null,
                    "Gagal memperbarui data : " + e.getMessage());

        }

    }
}
