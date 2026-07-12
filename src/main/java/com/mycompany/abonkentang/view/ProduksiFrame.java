/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.abonkentang.view;

import com.mycompany.abonkentang.config.koneksi;
import com.mycompany.abonkentang.controller.ProduksiController;
import com.mycompany.abonkentang.model.Produksi;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Bintang Aziz Satrio - 10125042, Alfikho Azka Dinova - 10125107
 */
public class ProduksiFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ProduksiFrame.class.getName());
    private final ProduksiController controller;
    private DefaultTableModel tableModel;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private int idProduksiTerpilih = -1;
    private int idProdukTerpilih = -1;
    private final Map<Integer, String> petaProdukById = new LinkedHashMap<>();
    private List<Produksi> daftarProduksi = new ArrayList<>();

    /**
     * Creates new form ProduksiFrame
     */
    public ProduksiFrame() {
        initComponents();
        controller = new ProduksiController();
        getContentPane().setBackground(new java.awt.Color(51,51,51));
        muatPetaProdukById();
        initTable();
        loadData();
        txtTanggal.setText(dateFormat.format(new Date()));
        txtTanggal.setEditable(false);
        txtTanggal.setToolTipText("Klik untuk pilih tanggal");
        txtTanggal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pilihTanggal();
            }
        });
        txtIdProduk.setEditable(false);
        txtIdProduk.setToolTipText("Klik untuk pilih produk");
        txtIdProduk.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pilihProduk();
            }
        });
        lblProduk.setText("Produk               :");
        tblProduksi.setSelectionBackground(new java.awt.Color(47, 128, 237));
    }

    private void muatPetaProdukById() {
        petaProdukById.clear();
        String sql = "SELECT id_produk, kode_produk, nama_produk FROM produk";
        try (Connection conn = koneksi.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                petaProdukById.put(rs.getInt("id_produk"),
                        rs.getString("kode_produk") + " - " + rs.getString("nama_produk"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data produk: " + e.getMessage());
        }
    }

    private String namaProduk(int idProduk) {
        String label = petaProdukById.get(idProduk);
        return label != null ? label : "(Produk dengan ID " + idProduk + " tidak ditemukan)";
    }

    private void pilihProduk() {
        muatPetaProdukById();

        if (petaProdukById.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Belum ada data produk. Tambahkan produk terlebih dahulu di menu Produk.");
            return;
        }

        Map<String, Integer> petaIdByLabel = new LinkedHashMap<>();
        petaProdukById.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> petaIdByLabel.put(entry.getValue(), entry.getKey()));

        String dipilih = (String) JOptionPane.showInputDialog(
            this,
            "Pilih produk yang diproduksi:",
            "Pilih Produk",
            JOptionPane.PLAIN_MESSAGE,
            null,
            petaIdByLabel.keySet().toArray(),
            txtIdProduk.getText()
        );

        if (dipilih != null) {
            idProdukTerpilih = petaIdByLabel.get(dipilih);
            txtIdProduk.setText(dipilih);
        }
    }

    private void pilihTanggal() {
        Calendar bulanAwal = Calendar.getInstance();
        try {
            bulanAwal.setTime(dateFormat.parse(txtTanggal.getText()));
        } catch (ParseException e) {
        }
        tampilkanDialogKalender(bulanAwal);
    }

    private void tampilkanDialogKalender(Calendar bulanAwal) {
        JDialog dialog = new JDialog(this, "Pilih Tanggal", true);
        dialog.setLayout(new BorderLayout());

        Calendar kalender = (Calendar) bulanAwal.clone();
        kalender.set(Calendar.DAY_OF_MONTH, 1);

        JPanel panelHeader = new JPanel(new BorderLayout());
        JLabel labelBulan = new JLabel("", SwingConstants.CENTER);
        JButton btnPrev = new JButton("<");
        JButton btnNext = new JButton(">");
        panelHeader.add(btnPrev, BorderLayout.WEST);
        panelHeader.add(labelBulan, BorderLayout.CENTER);
        panelHeader.add(btnNext, BorderLayout.EAST);

        JPanel panelHari = new JPanel(new GridLayout(0, 7));

        Runnable[] refresh = new Runnable[1];
        refresh[0] = () -> {
            panelHari.removeAll();
            SimpleDateFormat formatBulan = new SimpleDateFormat("MMMM yyyy");
            labelBulan.setText(formatBulan.format(kalender.getTime()));

            String[] namaHari = {"Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab"};
            for (String h : namaHari) {
                JLabel l = new JLabel(h, SwingConstants.CENTER);
                l.setFont(l.getFont().deriveFont(java.awt.Font.BOLD));
                panelHari.add(l);
            }

            Calendar cursor = (Calendar) kalender.clone();
            cursor.set(Calendar.DAY_OF_MONTH, 1);
            int offset = cursor.get(Calendar.DAY_OF_WEEK) - 1;
            int jumlahHari = cursor.getActualMaximum(Calendar.DAY_OF_MONTH);

            for (int i = 0; i < offset; i++) {
                panelHari.add(new JLabel(""));
            }
            for (int tgl = 1; tgl <= jumlahHari; tgl++) {
                final int tglFinal = tgl;
                JButton btnTgl = new JButton(String.valueOf(tgl));
                btnTgl.addActionListener(e -> {
                    Calendar dipilih = (Calendar) kalender.clone();
                    dipilih.set(Calendar.DAY_OF_MONTH, tglFinal);
                    txtTanggal.setText(dateFormat.format(dipilih.getTime()));
                    dialog.dispose();
                });
                panelHari.add(btnTgl);
            }

            panelHari.revalidate();
            panelHari.repaint();
            dialog.pack();
        };

        btnPrev.addActionListener(e -> {
            kalender.add(Calendar.MONTH, -1);
            refresh[0].run();
        });
        btnNext.addActionListener(e -> {
            kalender.add(Calendar.MONTH, 1);
            refresh[0].run();
        });

        JButton btnHariIni = new JButton("Hari Ini");
        btnHariIni.addActionListener(e -> {
            txtTanggal.setText(dateFormat.format(new Date()));
            dialog.dispose();
        });

        dialog.add(panelHeader, BorderLayout.NORTH);
        dialog.add(panelHari, BorderLayout.CENTER);
        dialog.add(btnHariIni, BorderLayout.SOUTH);

        refresh[0].run();

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void initTable() {
        String[] header = {"ID Produksi", "Produk", "Jumlah Produksi", "Tanggal"};
        tableModel = new DefaultTableModel(header, 0);
        tblProduksi.setModel(tableModel);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        daftarProduksi = controller.tampilProduksi();
        for (Produksi p : daftarProduksi) {
            Object[] row = {
                p.getIdProduksi(), 
                namaProduk(p.getIdProduk()), 
                p.getJumlahProduksi(), 
                dateFormat.format(p.getTanggalProduksi())
            };
            tableModel.addRow(row);
        }
    }
    
    private void bersihForm() {
        txtIdProduk.setText("");
        txtIdJumlahProduksi.setText("");
        txtTanggal.setText(dateFormat.format(new Date()));
        idProduksiTerpilih = -1;
        idProdukTerpilih = -1;
    }
    
    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {                                          
        bersihForm();
        txtIdProduk.requestFocus();
    }                                         

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {                       
        if (idProdukTerpilih == -1 || 
            txtIdJumlahProduksi.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Pilih produk (klik kolom ID Produk) dan isi Jumlah Produksi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Produksi p = new Produksi();
            p.setIdProduk(idProdukTerpilih);
            
            int jumlah = Integer.parseInt(txtIdJumlahProduksi.getText().trim());
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

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {                                        
        if (idProduksiTerpilih == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data pada tabel terlebih dahulu yang ingin diubah!");
            return;
        }
        if (idProdukTerpilih == -1) {
            JOptionPane.showMessageDialog(this, "Pilih produk (klik kolom Produk) terlebih dahulu!");
            return;
        }

        try {
            Produksi p = new Produksi();
            p.setIdProduksi(idProduksiTerpilih);
            p.setIdProduk(idProdukTerpilih);
            p.setJumlahProduksi(Integer.parseInt(txtIdJumlahProduksi.getText().trim()));
            p.setTanggalProduksi(dateFormat.parse(txtTanggal.getText()));

            controller.ubahProduksi(p);
            loadData();
            bersihForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal mengubah data: " + e.getMessage());
        }
    }                                       

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {                                         
        if (idProduksiTerpilih == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data pada tabel yang ingin dihapus!");
            return;
        }
        int id = idProduksiTerpilih;

        int konfirmasi = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data produksi " + id + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            controller.hapusProduksi(id);
            loadData();
            bersihForm();
        }
    }                                        

    private void btnBersihActionPerformed(java.awt.event.ActionEvent evt) {                                          
        bersihForm();
    }                                         

    private void tblProduksiMouseClicked(java.awt.event.MouseEvent evt) {                                         
        int row = tblProduksi.getSelectedRow();
        if (row != -1 && row < daftarProduksi.size()) {
            Produksi p = daftarProduksi.get(row);

            idProduksiTerpilih = p.getIdProduksi();
            idProdukTerpilih = p.getIdProduk();
            txtIdProduk.setText(namaProduk(p.getIdProduk()));
            txtIdJumlahProduksi.setText(String.valueOf(p.getJumlahProduksi()));
            txtTanggal.setText(dateFormat.format(p.getTanggalProduksi()));
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblProduksi = new javax.swing.JTable();
        btnKembali = new com.mycompany.abonkentang.components.Button();
        jPanel1 = new javax.swing.JPanel();
        lblDataProduksi = new javax.swing.JLabel();
        btnHapus = new com.mycompany.abonkentang.components.Button();
        btnBersih = new com.mycompany.abonkentang.components.Button();
        btnSimpan = new com.mycompany.abonkentang.components.Button();
        btnEdit = new com.mycompany.abonkentang.components.Button();
        lblProduk = new javax.swing.JLabel();
        txtIdJumlahProduksi = new javax.swing.JTextField();
        btnTambah = new com.mycompany.abonkentang.components.Button();
        txtTanggal = new javax.swing.JTextField();
        lblJumlahProduksi = new javax.swing.JLabel();
        lblTanggal = new javax.swing.JLabel();
        txtIdProduk = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tblProduksi.setBackground(new java.awt.Color(70, 71, 174));
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

        btnKembali.setText("Kembali");
        btnKembali.setToolTipText("");
        btnKembali.addActionListener(this::btnKembaliActionPerformed);

        jPanel1.setBackground(new java.awt.Color(70, 71, 174));

        lblDataProduksi.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblDataProduksi.setText("DATA PRODUKSI");

        btnHapus.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHapus.setText("Hapus");

        btnBersih.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBersih.setText("Bersih");

        btnSimpan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSimpan.setText("Simpan");

        btnEdit.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEdit.setText("Edit");

        lblProduk.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblProduk.setText("ID Produk             :");

        btnTambah.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTambah.setText("Tambah");

        lblJumlahProduksi.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblJumlahProduksi.setText("Jumlah Produksi  :");

        lblTanggal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTanggal.setText("Tanggal                 :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(126, 126, 126)
                .addComponent(lblDataProduksi)
                .addContainerGap(142, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(127, 127, 127)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnEdit)
                                    .addComponent(btnTambah))
                                .addGap(26, 26, 26)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnSimpan)
                                    .addComponent(btnHapus)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(51, 51, 51)
                                .addComponent(btnBersih)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblProduk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtIdProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblJumlahProduksi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtIdJumlahProduksi, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblTanggal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDataProduksi)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(lblProduk))
                    .addComponent(txtIdProduk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblJumlahProduksi)
                    .addComponent(txtIdJumlahProduksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTanggal)
                    .addComponent(txtTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTambah)
                    .addComponent(btnSimpan))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEdit)
                    .addComponent(btnHapus))
                .addGap(18, 18, 18)
                .addComponent(btnBersih)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(btnKembali))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnKembali)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        // TODO add your handling code here:
        new MainFrame().setVisible(true);
    }//GEN-LAST:event_btnKembaliActionPerformed

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
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnTambah;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDataProduksi;
    private javax.swing.JLabel lblJumlahProduksi;
    private javax.swing.JLabel lblProduk;
    private javax.swing.JLabel lblTanggal;
    private javax.swing.JTable tblProduksi;
    private javax.swing.JTextField txtIdJumlahProduksi;
    private javax.swing.JTextField txtIdProduk;
    private javax.swing.JTextField txtTanggal;
    // End of variables declaration//GEN-END:variables
}
