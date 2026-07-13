/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.abonkentang.config;

import com.mycompany.abonkentang.model.User;

/**
 * Menyimpan data user yang sedang login selama aplikasi berjalan.
 * Dipakai antar frame (misalnya untuk mengisi id_user saat menyimpan transaksi)
 * tanpa perlu mengoper parameter user ke setiap constructor frame.
 *
 * @author Kelompok 6
 */
public class Sesi {

    private static User userLogin;

    private Sesi() {
        // Tidak boleh diinstansiasi, hanya dipakai secara static
    }

    public static void setUser(User user) {
        userLogin = user;
    }

    public static User getUser() {
        return userLogin;
    }

    public static int getIdUser() {
        return userLogin != null ? userLogin.getIdUser() : -1;
    }

    public static void logout() {
        userLogin = null;
    }
}
