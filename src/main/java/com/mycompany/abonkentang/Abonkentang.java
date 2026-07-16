/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.abonkentang;

import com.mycompany.abonkentang.view.LoginFrame;

/**
 *
 * @author Kelompok 6
 */
public class Abonkentang {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(Abonkentang.class.getName());

    public static void main(String[] args) {
        /* Set the Nimbus look and feel */
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

        /* Buka halaman Login sebagai titik masuk aplikasi */
        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}