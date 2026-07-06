/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.abonkentang.config;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Kelompok 6
 */
public class koneksi {

    private static final String URL =
        "jdbc:mysql://localhost:3306/abonkentang"
        + "?useSSL=false"
        + "&allowPublicKeyRetrieval=true"
        + "&serverTimezone=Asia/Jakarta";
    private static final String USER = "root";
    private static final String PASS = ""; // sesuaikan password mysql/mariadb kamu

    public static Connection getKoneksi() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
        }
        return conn;
    }
}