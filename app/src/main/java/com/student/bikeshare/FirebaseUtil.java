package com.student.bikeshare;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class FirebaseUtil {

    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static String getUserEmailId() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
    }

    public static boolean isLoggedIn() {
        return currentUserId() != null;
    }


    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }


}