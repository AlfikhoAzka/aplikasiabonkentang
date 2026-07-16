/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.abonkentang.view;

import com.mycompany.abonkentang.config.koneksi;
import com.mycompany.abonkentang.controller.ProduksiController;
import com.mycompany.abonkentang.model.Produksi;
import com.mycompany.abonkentang.model.BahanBaku;
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
        txtCariProduksi.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtCariProduksi.getText().equals("Cari Produksi")) {
                    txtCariProduksi.setText("");
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtCariProduksi.getText().trim().isEmpty()) {
                    txtCariProduksi.setText("Cari Produksi");
                }
            }
        });
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
    
    private Map<Integer, Double> pilihBahanBaku() {
        List<BahanBaku> daftarBahan = controller.daftarBahanBaku();
        Map<Integer, Double> hasil = new LinkedHashMap<>();

        if (daftarBahan.isEmpty()) {
            return hasil;
        }

        while (true) {
            javax.swing.JComboBox<BahanBaku> cboBahan = new javax.swing.JComboBox<>(daftarBahan.toArray(new BahanBaku[0]));
            javax.swing.JTextField txtJumlah = new javax.swing.JTextField();

            Object[] isi = { "Bahan baku:", cboBahan, "Jumlah dipakai:", txtJumlah };

            int pilihan = JOptionPane.showConfirmDialog(this, isi,
                "Bahan Baku yang Dipakai (opsional)", JOptionPane.OK_CANCEL_OPTION);

            if (pilihan != JOptionPane.OK_OPTION) {
                break;
            }

            try {
                double jumlah = Double.parseDouble(txtJumlah.getText().trim());
                if (jumlah <= 0) {
                    JOptionPane.showMessageDialog(this, "Jumlah harus lebih besar dari 0!");
                    continue;
                }
                BahanBaku bb = (BahanBaku) cboBahan.getSelectedItem();
                hasil.merge(bb.getIdBahan(), jumlah, Double::sum);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka!");
                continue;
            }

            int lagi = JOptionPane.showConfirmDialog(this,
                "Tambah bahan baku lain?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (lagi != JOptionPane.YES_OPTION) {
                break;
            }
        }

        return hasil;
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

        btnEdit = new com.mycompany.abonkentang.components.Button();
        lblProduk = new com.mycompany.abonkentang.components.Label();
        txtIdJumlahProduksi = new javax.swing.JTextField();
        btnTambah = new com.mycompany.abonkentang.components.Button();
        txtTanggal = new javax.swing.JTextField();
        lblJumlahProduksi = new com.mycompany.abonkentang.components.Label();
        lblTanggal = new com.mycompany.abonkentang.components.Label();
        txtIdProduk = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProduksi = new com.mycompany.abonkentang.components.Table();
        btnKembali = new com.mycompany.abonkentang.components.Button();
        btnHapus = new com.mycompany.abonkentang.components.Button();
        btnBersih = new com.mycompany.abonkentang.components.Button();
        jPanel2 = new javax.swing.JPanel();
        lblDataProduksi = new com.mycompany.abonkentang.components.TitleLabel();
        txtCariProduksi = new javax.swing.JTextField();
        cmbKolomCariProduksi = new com.mycompany.abonkentang.components.ComboBox<>();
        btnCariProdukisi = new com.mycompany.abonkentang.components.Button();
        btnRefresh = new com.mycompany.abonkentang.components.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnEdit.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEdit.setText("Edit");
        btnEdit.addActionListener(this::btnEditActionPerformed);

        lblProduk.setForeground(new java.awt.Color(255, 255, 255));
        lblProduk.setText("ID Produk");

        txtIdJumlahProduksi.addActionListener(this::txtIdJumlahProduksiActionPerformed);

        btnTambah.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTambah.setText("Tambah");
        btnTambah.addActionListener(this::btnTambahActionPerformed);

        txtTanggal.addActionListener(this::txtTanggalActionPerformed);

        lblJumlahProduksi.setForeground(new java.awt.Color(255, 255, 255));
        lblJumlahProduksi.setText("Jumlah Produksi");

        lblTanggal.setForeground(new java.awt.Color(255, 255, 255));
        lblTanggal.setText("Tanggal");

        txtIdProduk.addActionListener(this::txtIdProdukActionPerformed);

        tblProduksi.setBackground(new java.awt.Color(70, 71, 174));
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

        btnHapus.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHapus.setText("Hapus");
        btnHapus.addActionListener(this::btnHapusActionPerformed);

        btnBersih.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBersih.setText("Bersih");
        btnBersih.addActionListener(this::btnBersihActionPerformed);

        jPanel2.setBackground(new java.awt.Color(17, 46, 129));

        lblDataProduksi.setForeground(new java.awt.Color(255, 255, 255));
        lblDataProduksi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDataProduksi.setText("DATA PRODUKSI");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblDataProduksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(lblDataProduksi)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        txtCariProduksi.setText("Cari Produksi");

        cmbKolomCariProduksi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nama Produk", "Tanggal Produksi" }));

        btnCariProdukisi.setText("Cari");
        btnCariProdukisi.addActionListener(this::btnCariProdukisiActionPerformed);

        btnRefresh.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(this::btnRefreshActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblProduk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtIdProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblJumlahProduksi)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, Short.MAX_VALUE)
                                .addComponent(txtIdJumlahProduksi, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblTanggal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(66, 66, 66))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnKembali)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtCariProduksi, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbKolomCariProduksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCariProdukisi))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnHapus)
                    .addComponent(btnBersih)
                    .addComponent(btnRefresh))
                .addGap(42, 42, 42))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(109, 109, 109)
                        .addComponent(btnBersih, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCariProduksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbKolomCariProduksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCariProdukisi))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(76, 76, 76)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblProduk)
                            .addComponent(txtIdProduk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblJumlahProduksi)
                            .addComponent(txtIdJumlahProduksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTanggal)
                            .addComponent(txtTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnTambah)
                            .addComponent(btnEdit))))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        // TODO add your handling code here:
        new MainFrame().setVisible(true);
        dispose();
    }//GEN-LAST:event_btnKembaliActionPerformed

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

    private void tblProduksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProduksiMouseClicked
        // TODO add your handling code here:
        int row = tblProduksi.getSelectedRow();

        if (row != -1) {
            Produksi p = daftarProduksi.get(row);

            idProduksiTerpilih = p.getIdProduksi();
        }
    }//GEN-LAST:event_tblProduksiMouseClicked

    private void txtTanggalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTanggalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTanggalActionPerformed

    private void txtIdProdukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdProdukActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdProdukActionPerformed

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

                int jumlahProduksi = Integer.parseInt(txtIdJumlahProduksi.getText().trim());
                if (jumlahProduksi <= 0) {
                    JOptionPane.showMessageDialog(this,
                        "Jumlah produksi harus lebih besar dari 0!");
                    return;
                }

                Produksi p = new Produksi();

                p.setIdProduk(idProdukTerpilih);
                p.setJumlahProduksi(jumlahProduksi);
                p.setTanggalProduksi(
                    dateFormat.parse(txtTanggal.getText()));

                Map<Integer, Double> bahanDipakai = pilihBahanBaku();
                controller.tambahProduksi(p, bahanDipakai);

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

                if (txtIdJumlahProduksi.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Jumlah produksi harus diisi!");
                    return;
                }

                int jumlahProduksi = Integer.parseInt(txtIdJumlahProduksi.getText().trim());
                if (jumlahProduksi <= 0) {
                    JOptionPane.showMessageDialog(this,
                        "Jumlah produksi harus lebih besar dari 0!");
                    return;
                }

                Produksi p = new Produksi();

                p.setIdProduksi(idProduksiTerpilih);
                p.setIdProduk(idProdukTerpilih);
                p.setJumlahProduksi(jumlahProduksi);
                p.setTanggalProduksi(
                    dateFormat.parse(txtTanggal.getText()));

                Map<Integer, Double> bahanDipakai = pilihBahanBaku();
                controller.ubahProduksi(p, bahanDipakai);

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

    private void txtIdJumlahProduksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdJumlahProduksiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdJumlahProduksiActionPerformed

    private void btnCariProdukisiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariProdukisiActionPerformed
        // TODO add your handling code here:
        String keyword = txtCariProduksi.getText().trim();
        String pilihan = (String) cmbKolomCariProduksi.getSelectedItem();

        if (keyword.isEmpty() || keyword.equals("Cari Produksi")) {
            loadData();
            return;
        }

        String kolom = "Tanggal Produksi".equals(pilihan) ? "tanggal_produksi" : "nama_produk";

        try {
            List<Produksi> hasil = controller.cariProduksi(keyword, kolom);
            tableModel.setRowCount(0);
            for (Produksi p : hasil) {
                Object[] row = {
                    p.getIdProduksi(),
                    namaProduk(p.getIdProduk()),
                    p.getJumlahProduksi(),
                    dateFormat.format(p.getTanggalProduksi())
                };
                tableModel.addRow(row);
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_btnCariProdukisiActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        loadData();
        txtCariProduksi.setText("Cari Produksi");
    }//GEN-LAST:event_btnRefreshActionPerformed

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
    private javax.swing.JButton btnCariProdukisi;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnTambah;
    private javax.swing.JComboBox<String> cmbKolomCariProduksi;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDataProduksi;
    private javax.swing.JLabel lblJumlahProduksi;
    private javax.swing.JLabel lblProduk;
    private javax.swing.JLabel lblTanggal;
    private javax.swing.JTable tblProduksi;
    private javax.swing.JTextField txtCariProduksi;
    private javax.swing.JTextField txtIdJumlahProduksi;
    private javax.swing.JTextField txtIdProduk;
    private javax.swing.JTextField txtTanggal;
    // End of variables declaration//GEN-END:variables
}
