/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.abonkentang.view;

import com.mycompany.abonkentang.controller.ProduksiController;
import com.mycompany.abonkentang.model.Produksi;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Bintang Aziz Satrio - 10125042
 */
public class ProduksiFrame extends javax.swing.JFrame {
    
    private final ProduksiController controller;
    private DefaultTableModel tableModel;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * Creates new form ProduksiFrame
     */
    public ProduksiFrame() {
        initComponents();
        controller = new ProduksiController();
        initTable();
        loadData();
        txtTanggal.setText(dateFormat.format(new Date())); // Mengisi tanggal otomatis hari ini
        txtTanggal.setEditable(false); // Validasi: tanggal diisi sistem agar tidak salah format
    }
    
    private void initTable() {
        String[] header = {"ID Produksi", "ID Produk", "Jumlah Produksi", "Tanggal"};
        tableModel = new DefaultTableModel(header, 0);
        tblProduksi.setModel(tableModel);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Produksi> list = controller.tampilProduksi();
        for (Produksi p : list) {
            Object[] row = {
                p.getIdProduksi(), 
                p.getIdProduk(), 
                p.getJumlahProduksi(), 
                dateFormat.format(p.getTanggalProduksi())
            };
            tableModel.addRow(row);
        }
    }
    
    private void bersihForm() {
        txtIdProduksi.setText("");
        txtIdProduk.setText("");
        txtJumlahProduksi.setText("");
        txtTanggal.setText(dateFormat.format(new Date()));
        txtIdProduksi.setEditable(true);
    }
    
    // =========================================================================
    // LOGIKA TOMBOL AKSI (Sesuaikan dengan Event ActionPerformed di NetBeans)
    // =========================================================================

    // 1. Tombol Tambah (Menyiapkan form untuk input baru)
    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {                                          
        bersihForm();
        txtIdProduksi.requestFocus();
    }                                         

    // 2. Tombol Simpan (Validasi & Insert ke Database)
    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // Validasi batasan input tidak boleh kosong (Aturan Aturan 3.d)
        if (txtIdProduksi.getText().trim().isEmpty() || 
            txtIdProduk.getText().trim().isEmpty() || 
            txtJumlahProduksi.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Semua kolom data wajib diisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Produksi p = new Produksi();
            p.setIdProduksi(txtIdProduksi.getText().trim());
            p.setIdProduk(txtIdProduk.getText().trim());
            
            // Validasi logika: Jumlah produksi harus berupa angka positif
            int jumlah = Integer.parseInt(txtJumlahProduksi.getText().trim());
            if (jumlah <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah produksi harus lebih dari 0!", "Validasi Logika", JOptionPane.WARNING_MESSAGE);
                return;
            }
            p.setJumlahProduksi(jumlah);
            p.setTanggalProduksi(new Date());

            controller.tambahProduksi(p);
            loadData();
            bersihForm();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Kolom 'Jumlah Produksi' wajib diisi dengan angka!", "Error Tipe Data", JOptionPane.ERROR_MESSAGE);
        }
    }                                         

    // 3. Tombol Edit (Ubah Data)
    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {                                        
        if (txtIdProduksi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data pada tabel terlebih dahulu yang ingin diubah!");
            return;
        }

        try {
            Produksi p = new Produksi();
            p.setIdProduksi(txtIdProduksi.getText().trim());
            p.setIdProduk(txtIdProduk.getText().trim());
            p.setJumlahProduksi(Integer.parseInt(txtJumlahProduksi.getText().trim()));
            p.setTanggalProduksi(dateFormat.parse(txtTanggal.getText()));

            controller.ubahProduksi(p);
            loadData();
            bersihForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal mengubah data: " + e.getMessage());
        }
    }                                       

    // 4. Tombol Hapus
    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {                                         
        String id = txtIdProduksi.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data pada tabel yang ingin dihapus!");
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data produksi " + id + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            controller.hapusProduksi(id);
            loadData();
            bersihForm();
        }
    }                                        

    // 5. Tombol Bersih
    private void btnBersihActionPerformed(java.awt.event.ActionEvent evt) {                                          
        bersihForm();
    }                                         

    // 6. Event ketika baris tabel diklik (Mengisi Form Otomatis)
    private void tblProduksiMouseClicked(java.awt.event.MouseEvent evt) {                                         
        int row = tblProduksi.getSelectedRow();
        if (row != -1) {
            txtIdProduksi.setText(tableModel.getValueAt(row, 0).toString());
            txtIdProduk.setText(tableModel.getValueAt(row, 1).toString());
            txtJumlahProduksi.setText(tableModel.getValueAt(row, 2).toString());
            txtTanggal.setText(tableModel.getValueAt(row, 3).toString());
            
            txtIdProduksi.setEditable(false); // ID Utama dikunci saat mode edit agar tidak melanggar relasi DB
        }
    }                                        
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtIdProduksi = new javax.swing.JTextField();
        txtIdProduk = new javax.swing.JTextField();
        txtIdJumlahProduksi = new javax.swing.JTextField();
        txtTanggal = new javax.swing.JTextField();
        btnTambah = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBersih = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProduksi = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("DATA PRODUKSI");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("ID Produksi          :");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("ID Produk             :");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Jumlah Produksi :");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Tanggal                 :");

        btnTambah.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTambah.setText("Tambah");

        btnSimpan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSimpan.setText("Simpan");

        btnEdit.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEdit.setText("Edit");

        btnHapus.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHapus.setText("Hapus");

        btnBersih.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBersih.setText("Bersih");

        tblProduksi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        tblProduksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID Produksi", "ID Produk", "Jumlah Produksi", "Tanggal"
            }
        ));
        jScrollPane1.setViewportView(tblProduksi);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(txtIdProduk))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(txtIdJumlahProduksi))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(txtTanggal, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(txtIdProduksi)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(btnTambah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 138, Short.MAX_VALUE)
                        .addComponent(btnSimpan)
                        .addGap(131, 131, 131)))
                .addComponent(btnEdit)
                .addGap(127, 127, 127)
                .addComponent(btnHapus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 209, Short.MAX_VALUE)
                .addComponent(btnBersih)
                .addGap(176, 176, 176))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(379, 379, 379)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 839, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtIdProduksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtIdProduk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtIdJumlahProduksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTambah)
                    .addComponent(btnSimpan)
                    .addComponent(btnEdit)
                    .addComponent(btnHapus)
                    .addComponent(btnBersih))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new ProduksiFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBersih;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnTambah;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblProduksi;
    private javax.swing.JTextField txtIdJumlahProduksi;
    private javax.swing.JTextField txtIdProduk;
    private javax.swing.JTextField txtIdProduksi;
    private javax.swing.JTextField txtTanggal;
    // End of variables declaration//GEN-END:variables
}
