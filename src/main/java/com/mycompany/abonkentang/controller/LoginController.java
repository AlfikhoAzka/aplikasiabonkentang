/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.abonkentang.controller;

import com.mycompany.abonkentang.config.koneksi;
import com.mycompany.abonkentang.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Bintang Aziz Satrio - 10125042
 */
public class LoginController {

    public User login(String username, String password) {
        User user = null;
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";

        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                        rs.getInt("id_user"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("nama_lengkap"),
                        rs.getString("role")
                    );
                }
            }

        } catch (Exception e) {
            System.out.println("Gagal login: " + e.getMessage());
        }
        return user;
    }
    
    public boolean isUsernameTersedia(String username) {
        String sql = "SELECT id_user FROM user WHERE username = ?";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return !rs.next();
            }
        } catch (Exception e) {
            System.out.println("Gagal cek username: " + e.getMessage());
            return false;
        }
    }

    public boolean daftar(String username, String password, String role) {
    String namaLengkap = username;
        if (!isUsernameTersedia(username)) {
            return false;
        }
        String sql = "INSERT INTO user (username, password, nama_lengkap, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, namaLengkap);
            ps.setString(4, role);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Gagal daftar: " + e.getMessage());
            return false;
        }
    }
}
