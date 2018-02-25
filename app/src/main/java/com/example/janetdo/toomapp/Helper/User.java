package com.example.janetdo.toomapp.Helper;

/**
 * Created by janetdo on 28.12.17.
 */

public class User {
    private static boolean isUser = false;

    public static void setIsUser(boolean isUser) {
        User.isUser = isUser;
    }

    public static boolean isUser() {
        return isUser;
    }
}
