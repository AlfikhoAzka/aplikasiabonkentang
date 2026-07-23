/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.abonkentang.view;
import com.mycompany.abonkentang.config.Sesi;
import com.mycompany.abonkentang.controller.TransaksiController;
import com.mycompany.abonkentang.model.DetailTransaksi;
import com.mycompany.abonkentang.model.Produk;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Alfikho Azka Dinova - 10125107
 */
public class TransaksiFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TransaksiFrame.class.getName());
       private static final String PLACEHOLDER_CARI = "Cari";

    private final TransaksiController controller = new TransaksiController();
    private final DecimalFormat rupiah = new DecimalFormat("Rp #,###");
    private DefaultTableModel modelProduk;
    private DefaultTableModel modelKeranjang;
    private List<Produk> hasilPencarian = new ArrayList<>();
    private List<DetailTransaksi> keranjang = new ArrayList<>();

    private Produk produkTerpilih = null;

    /**
     * Creates new form TransaksiFrame
     */
    public TransaksiFrame() {
        initComponents();
        spnJumlah.setModel(new javax.swing.SpinnerNumberModel(1, 1, 9999, 1));
        getContentPane().setBackground(new java.awt.Color(51,51,51));
        btnReset.setForeground(java.awt.Color.WHITE);
        initTabelProduk();
        initTabelKeranjang();
        pasangPlaceholderPencarian();
        pasangListenerTambahan();
        muatSemuaProduk();
        perbaruiTotal();
    }

    private void initTabelProduk() {
        modelProduk = new DefaultTableModel(
                new Object[]{"Kode", "Nama Produk", "Harga", "Stok"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblProduk.setModel(modelProduk);
        tblProduk.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblProduk.getColumnModel().getColumn(1).setPreferredWidth(220);
        tblProduk.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblProduk.getColumnModel().getColumn(3).setPreferredWidth(50);
    }

    private void initTabelKeranjang() {
        modelKeranjang = new DefaultTableModel(
                new Object[]{"Produk", "Jumlah", "Harga Satuan", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblKeranjang.setModel(modelKeranjang);
    }

    private void pasangPlaceholderPencarian() {
        txtCari.setForeground(new java.awt.Color(153, 153, 153));
        txtCari.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtCari.getText().equals(PLACEHOLDER_CARI)) {
                    txtCari.setText("");
                    txtCari.setForeground(java.awt.Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtCari.getText().isEmpty()) {
                    txtCari.setText(PLACEHOLDER_CARI);
                    txtCari.setForeground(new java.awt.Color(153, 153, 153));
                }
            }
        });
    }

    private void pasangListenerTambahan() {
        txtCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                jalankanPencarian();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                jalankanPencarian();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                jalankanPencarian();
            }
        });

        tblProduk.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tblProduk.getSelectedRow();
                if (row >= 0 && row < hasilPencarian.size()) {
                    produkTerpilih = hasilPencarian.get(row);
                }
            }
        });

        tblKeranjang.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    }

    private void muatSemuaProduk() {
        try {
            hasilPencarian = controller.getSemuaProduk();
            tampilkanHasilPencarian(hasilPencarian);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data produk: " + e.getMessage());
        }
    }

    private void jalankanPencarian() {
        String keyword = txtCari.getText().trim();
        if (keyword.isEmpty() || keyword.equals(PLACEHOLDER_CARI)) {
            muatSemuaProduk();
            return;
        }
        try {
            hasilPencarian = controller.cariProduk(keyword);
            tampilkanHasilPencarian(hasilPencarian);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Pencarian gagal: " + e.getMessage());
        }
    }

    private void tampilkanHasilPencarian(List<Produk> daftar) {
        modelProduk.setRowCount(0);
        for (Produk p : daftar) {
            modelProduk.addRow(new Object[]{
                p.getKodeProduk(),
                p.getNamaProduk(),
                rupiah.format(p.getHargaJual()),
                p.getStok()
            });
        }
        produkTerpilih = null;
    }

    private void tampilkanKeranjang() {
        modelKeranjang.setRowCount(0);
        for (DetailTransaksi item : keranjang) {
            modelKeranjang.addRow(new Object[]{
                item.getNamaProduk(),
                item.getJumlah(),
                rupiah.format(item.getHargaSatuan()),
                rupiah.format(item.getSubtotal())
            });
        }
        perbaruiTotal();
    }

    private void perbaruiTotal() {
        double total = 0;
        for (DetailTransaksi item : keranjang) {
            total += item.getSubtotal();
        }
        lblTotal.setText("TOTAL: " + rupiah.format(total));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblTransaksiPenjualan = new com.mycompany.abonkentang.components.TitleLabel();
        txtCari = new javax.swing.JTextField();
        lblTotal = new com.mycompany.abonkentang.components.Label();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProduk = new com.mycompany.abonkentang.components.Table();
        lblJumlah = new com.mycompany.abonkentang.components.Label();
        btnTambahKeranjang = new com.mycompany.abonkentang.components.Button();
        btnReset = new com.mycompany.abonkentang.components.Button();
        btnSimpanTransaksi = new com.mycompany.abonkentang.components.Button();
        lblKeranjang = new com.mycompany.abonkentang.components.TitleLabel();
        btnHapusItem = new com.mycompany.abonkentang.components.Button();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKeranjang = new com.mycompany.abonkentang.components.Table();
        btnKembali = new com.mycompany.abonkentang.components.Button();
        spnJumlah = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));

        jPanel1.setBackground(new java.awt.Color(70, 71, 174));

        lblTransaksiPenjualan.setBackground(new java.awt.Color(70, 71, 174));
        lblTransaksiPenjualan.setForeground(new java.awt.Color(255, 255, 255));
        lblTransaksiPenjualan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTransaksiPenjualan.setText("Transaksi Penjualan");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(lblTransaksiPenjualan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lblTransaksiPenjualan)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        txtCari.setText("Cari");

        lblTotal.setForeground(new java.awt.Color(255, 255, 255));
        lblTotal.setText("TOTAL");

        tblProduk.setBackground(new java.awt.Color(70, 71, 174));
        tblProduk.setFont(new java.awt.Font("Gadugi", 1, 12)); // NOI18N
        tblProduk.setForeground(new java.awt.Color(255, 255, 255));
        tblProduk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Kode", "Nama Produk", "Harga", "Stok"
            }
        ));
        jScrollPane2.setViewportView(tblProduk);

        lblJumlah.setForeground(new java.awt.Color(255, 255, 255));
        lblJumlah.setText("Jumlah:");

        btnTambahKeranjang.setText("Tambah ke keranjang");
        btnTambahKeranjang.addActionListener(this::btnTambahKeranjangActionPerformed);

        btnReset.setBackground(new java.awt.Color(102, 102, 102));
        btnReset.setForeground(new java.awt.Color(0, 0, 0));
        btnReset.setText("Reset Keranjang");
        btnReset.addActionListener(this::btnResetActionPerformed);

        btnSimpanTransaksi.setBackground(new java.awt.Color(70, 71, 174));
        btnSimpanTransaksi.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpanTransaksi.setText("Simpan Transaksi");
        btnSimpanTransaksi.addActionListener(this::btnSimpanTransaksiActionPerformed);

        lblKeranjang.setForeground(new java.awt.Color(255, 255, 255));
        lblKeranjang.setText("Keranjang");

        btnHapusItem.setText("Hapus Item Terpilih");
        btnHapusItem.addActionListener(this::btnHapusItemActionPerformed);

        tblKeranjang.setBackground(new java.awt.Color(70, 71, 174));
        tblKeranjang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Produk", "Jumlah", "Harga Satuan", "Subtotal"
            }
        ));
        jScrollPane1.setViewportView(tblKeranjang);

        btnKembali.setBackground(new java.awt.Color(102, 102, 102));
        btnKembali.setForeground(new java.awt.Color(255, 255, 255));
        btnKembali.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/angle-left.png"))); // NOI18N
        btnKembali.setText("Kembali");
        btnKembali.addActionListener(this::btnKembaliActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSimpanTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(72, 72, 72))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(87, 87, 87)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblJumlah)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spnJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(80, 80, 80)
                                .addComponent(btnTambahKeranjang, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                                .addComponent(txtCari, javax.swing.GroupLayout.Alignment.LEADING))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(58, 58, 58)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotal)
                    .addComponent(lblKeranjang)
                    .addComponent(btnHapusItem, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(75, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblKeranjang))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnHapusItem, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnTambahKeranjang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(1, 1, 1))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblJumlah)
                        .addComponent(lblTotal)
                        .addComponent(spnJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnSimpanTransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(btnReset, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(27, 27, 27))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTambahKeranjangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahKeranjangActionPerformed
        // TODO add your handling code here:
        if (produkTerpilih == null) {
            JOptionPane.showMessageDialog(this, "Pilih dulu produk dari tabel pencarian di sebelah kiri.");
            return;
        }
        
        int jumlah = (Integer) spnJumlah.getValue();
        int jumlahSudahDiKeranjang = 0;
        DetailTransaksi itemYangSudahAda = null;
        for (DetailTransaksi item : keranjang) {
            if (item.getIdProduk() == produkTerpilih.getIdProduk()) {
                jumlahSudahDiKeranjang += item.getJumlah();
                itemYangSudahAda = item;
            }
        }

        if (jumlahSudahDiKeranjang + jumlah > produkTerpilih.getStok()) {
            JOptionPane.showMessageDialog(this,
                    "Stok \"" + produkTerpilih.getNamaProduk() + "\" tidak cukup. "
                    + "Sisa stok: " + produkTerpilih.getStok() + ", sudah di keranjang: " + jumlahSudahDiKeranjang);
            return;
        }

        if (itemYangSudahAda != null) {
            itemYangSudahAda.setJumlah(itemYangSudahAda.getJumlah() + jumlah);
        } else {
            keranjang.add(new DetailTransaksi(
                    produkTerpilih.getIdProduk(),
                    produkTerpilih.getKodeProduk(),
                    produkTerpilih.getNamaProduk(),
                    produkTerpilih.getHargaJual(),
                    jumlah
            ));
        }

        tampilkanKeranjang();
        spnJumlah.setValue(1);
    }//GEN-LAST:event_btnTambahKeranjangActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        // TODO add your handling code here:
        if (keranjang.isEmpty()) {
            return;
        }
        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Kosongkan seluruh keranjang?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            keranjang.clear();
            tampilkanKeranjang();
            spnJumlah.setValue(1);
        }
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnSimpanTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanTransaksiActionPerformed
        // TODO add your handling code here:
        if (keranjang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang masih kosong.");
            return;
        }

        int idUser = Sesi.getIdUser();
        if (idUser == -1) {
            JOptionPane.showMessageDialog(this, "Sesi login tidak ditemukan. Silakan login ulang.");
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Simpan transaksi ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int idTransaksi = controller.simpanTransaksi(idUser, keranjang);
            JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan (ID Transaksi: " + idTransaksi + ").");

            keranjang.clear();
            tampilkanKeranjang();
            muatSemuaProduk();
            spnJumlah.setValue(1);
            produkTerpilih = null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_btnSimpanTransaksiActionPerformed

    private void btnHapusItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusItemActionPerformed
        // TODO add your handling code here:
        int row = tblKeranjang.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu item di keranjang yang ingin dihapus.");
            return;
        }
        keranjang.remove(row);
        tampilkanKeranjang();
    }//GEN-LAST:event_btnHapusItemActionPerformed

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        // TODO add your handling code here:
        new MainFrame().setVisible(true);
        dispose();
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
        java.awt.EventQueue.invokeLater(() -> new TransaksiFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHapusItem;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSimpanTransaksi;
    private javax.swing.JButton btnTambahKeranjang;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblJumlah;
    private javax.swing.JLabel lblKeranjang;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTransaksiPenjualan;
    private javax.swing.JSpinner spnJumlah;
    private javax.swing.JTable tblKeranjang;
    private javax.swing.JTable tblProduk;
    private javax.swing.JTextField txtCari;
    // End of variables declaration//GEN-END:variables
}
