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
        try {
            Connection conn = koneksi.getKoneksi();

            String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                user = new User(
                    rs.getInt("id_user"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("nama_lengkap"),
                    rs.getString("role")
                );
            }

        } catch (Exception e) {
            System.out.println("Gagal login: " + e.getMessage());
        }
        return user;
    }
}
