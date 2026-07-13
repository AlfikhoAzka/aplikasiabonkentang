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
    private boolean modeEdit = false;

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
        bersihForm();
        
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
        tblProduksi.setRowHeight(25);
        tblProduksi.setShowGrid(false);
        tblProduksi.setSelectionBackground(new java.awt.Color(47, 128, 237));
        tblProduksi.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
    }

    private void muatPetaProdukById() {
        petaProdukById.clear();
        String sql = "SELECT id_produk, kode_produk, nama_produk FROM produk";
        try (Connection conn = koneksi.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                petaProdukById.put(
                        rs.getInt("id_produk"),
                        rs.getString("nama_produk")
                );
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
        String[] header = {"ID", "Produk", "Jumlah Produksi", "Tanggal"};
        tableModel = new DefaultTableModel(header, 0);
        tblProduksi.setModel(tableModel);
        tableWidth();
    }
    
    private void tableWidth() {

        tblProduksi.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);

        tblProduksi.getColumnModel().getColumn(0).setPreferredWidth(35);
        tblProduksi.getColumnModel().getColumn(1).setPreferredWidth(190);
        tblProduksi.getColumnModel().getColumn(2).setPreferredWidth(95);
        tblProduksi.getColumnModel().getColumn(3).setPreferredWidth(95);
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

        tblProduksi.clearSelection();

        modeEdit = false;

        btnTambah.setText("Tambah");
        btnBersih.setText("Bersih");
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
        tblProduksi = new com.mycompany.abonkentang.components.Table();
        btnKembali = new com.mycompany.abonkentang.components.Button();
        jPanel1 = new com.mycompany.abonkentang.components.CardPanel();
        lblDataProduksi = new javax.swing.JLabel();
        btnEdit = new com.mycompany.abonkentang.components.Button();
        lblProduk = new javax.swing.JLabel();
        txtIdJumlahProduksi = new javax.swing.JTextField();
        btnTambah = new com.mycompany.abonkentang.components.Button();
        txtTanggal = new javax.swing.JTextField();
        lblJumlahProduksi = new javax.swing.JLabel();
        lblTanggal = new javax.swing.JLabel();
        txtIdProduk = new javax.swing.JTextField();
        btnHapus = new com.mycompany.abonkentang.components.Button();
        btnBersih = new com.mycompany.abonkentang.components.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tblProduksi.setBackground(new java.awt.Color(70, 71, 174));
        tblProduksi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        tblProduksi.setForeground(new java.awt.Color(255, 255, 255));
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
        tblProduksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProduksiMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProduksi);

        btnKembali.setText("Kembali");
        btnKembali.setToolTipText("");
        btnKembali.addActionListener(this::btnKembaliActionPerformed);

        jPanel1.setBackground(new java.awt.Color(70, 71, 174));

        lblDataProduksi.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblDataProduksi.setForeground(new java.awt.Color(255, 255, 255));
        lblDataProduksi.setText("DATA PRODUKSI");

        btnEdit.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEdit.setText("Edit");
        btnEdit.addActionListener(this::btnEditActionPerformed);

        lblProduk.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblProduk.setForeground(new java.awt.Color(255, 255, 255));
        lblProduk.setText("ID Produk             :");

        txtIdJumlahProduksi.addActionListener(this::txtIdJumlahProduksiActionPerformed);

        btnTambah.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTambah.setText("Tambah");
        btnTambah.addActionListener(this::btnTambahActionPerformed);

        txtTanggal.addActionListener(this::txtTanggalActionPerformed);

        lblJumlahProduksi.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblJumlahProduksi.setForeground(new java.awt.Color(255, 255, 255));
        lblJumlahProduksi.setText("Jumlah Produksi  :");

        lblTanggal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTanggal.setForeground(new java.awt.Color(255, 255, 255));
        lblTanggal.setText("Tanggal                 :");

        txtIdProduk.addActionListener(this::txtIdProdukActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                        .addComponent(txtTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addComponent(lblDataProduksi)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblDataProduksi)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblProduk)
                    .addComponent(txtIdProduk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblJumlahProduksi)
                    .addComponent(txtIdJumlahProduksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTanggal)
                    .addComponent(txtTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(106, Short.MAX_VALUE))
        );

        btnHapus.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHapus.setText("Hapus");
        btnHapus.addActionListener(this::btnHapusActionPerformed);

        btnBersih.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBersih.setText("Bersih");
        btnBersih.addActionListener(this::btnBersihActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnHapus)
                            .addComponent(btnBersih)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnKembali)))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(btnKembali)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addComponent(btnBersih, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(69, 69, 69)
                        .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(111, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        // TODO add your handling code here:
        new MainFrame().setVisible(true);
        dispose();
    }//GEN-LAST:event_btnKembaliActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        // TODO add your handling code here:
        if (!modeEdit) {

            if (idProdukTerpilih == -1) {
                JOptionPane.showMessageDialog(this,
                        "Pilih produk terlebih dahulu!");
                return;
            }

            if (txtIdJumlahProduksi.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Jumlah produksi harus diisi!");
                return;
            }

            try {

                Produksi p = new Produksi();

                p.setIdProduk(idProdukTerpilih);
                p.setJumlahProduksi(
                        Integer.parseInt(txtIdJumlahProduksi.getText().trim()));
                p.setTanggalProduksi(
                        dateFormat.parse(txtTanggal.getText()));

                controller.tambahProduksi(p);

                JOptionPane.showMessageDialog(this,
                        "Data produksi berhasil ditambahkan.");

                loadData();
                bersihForm();

            } catch (Exception e) {

                JOptionPane.showMessageDialog(this,
                        "Gagal menambah data : " + e.getMessage());

            }

        } else {

            try {

                Produksi p = new Produksi();

                p.setIdProduksi(idProduksiTerpilih);
                p.setIdProduk(idProdukTerpilih);
                p.setJumlahProduksi(
                        Integer.parseInt(txtIdJumlahProduksi.getText().trim()));
                p.setTanggalProduksi(
                        dateFormat.parse(txtTanggal.getText()));

                controller.ubahProduksi(p);

                JOptionPane.showMessageDialog(this,
                        "Data produksi berhasil diubah.");

                bersihForm();
                loadData();

            } catch (Exception e) {

                JOptionPane.showMessageDialog(this,
                        "Gagal mengubah data : " + e.getMessage());

            }

        }
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // TODO add your handling code here:
        if (idProduksiTerpilih == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data terlebih dahulu.");
            return;
        }

        Produksi p = null;

        for (Produksi data : daftarProduksi) {
            if (data.getIdProduksi() == idProduksiTerpilih) {
                p = data;
                break;
            }
        }

        if (p == null) {
            JOptionPane.showMessageDialog(this,
                    "Data tidak ditemukan.");
            return;
        }

        idProdukTerpilih = p.getIdProduk();

        txtIdProduk.setText(namaProduk(p.getIdProduk()));
        txtIdJumlahProduksi.setText(String.valueOf(p.getJumlahProduksi()));
        txtTanggal.setText(dateFormat.format(p.getTanggalProduksi()));

        modeEdit = true;

        btnTambah.setText("Simpan Perubahan");
        btnBersih.setText("Batal");
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
                if (idProduksiTerpilih == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data yang ingin dihapus!");
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(
                this,
                "Yakin ingin menghapus data ini?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION);

        if (konfirmasi == JOptionPane.YES_OPTION) {

            controller.hapusProduksi(idProduksiTerpilih);

            JOptionPane.showMessageDialog(this,
                    "Data produksi berhasil dihapus.");

            loadData();
            bersihForm();
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnBersihActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBersihActionPerformed
        // TODO add your handling code here:
            if (modeEdit) {
                bersihForm();

            } else {
                txtIdProduk.setText("");
                txtIdJumlahProduksi.setText("");
                txtTanggal.setText(dateFormat.format(new Date()));

                idProdukTerpilih = -1;
            }
    }//GEN-LAST:event_btnBersihActionPerformed

    private void txtIdProdukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdProdukActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdProdukActionPerformed

    private void txtTanggalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTanggalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTanggalActionPerformed

    private void txtIdJumlahProduksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdJumlahProduksiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdJumlahProduksiActionPerformed

    private void tblProduksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProduksiMouseClicked
        // TODO add your handling code here:
        int row = tblProduksi.getSelectedRow();

        if (row != -1) {
            Produksi p = daftarProduksi.get(row);

            idProduksiTerpilih = p.getIdProduksi();
        }
    }//GEN-LAST:event_tblProduksiMouseClicked

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
