/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.abonkentang;

import com.mycompany.abonkentang.config.koneksi;
import java.sql.Connection;

/**
 *
 * @author Kelompok 6
 */
public class Abonkentang {

    public static void main(String[] args) {
        Connection conn = koneksi.getKoneksi();
        if (conn != null) {
            System.out.println("Koneksi berhasil!");
        } else {
            System.out.println("Koneksi gagal.");
        }
    }
}
