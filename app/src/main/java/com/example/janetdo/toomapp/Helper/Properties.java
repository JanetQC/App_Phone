package com.example.janetdo.toomapp.Helper;

/**
 * Created by janetdo on 27.02.18.
 */

public class Properties {
        private static Properties mInstance=  null;

        public boolean isAdmin = false;

        protected Properties(){}

        public static synchronized Properties getInstance(){
            if(null == mInstance){
                mInstance = new Properties();
            }
            return mInstance;
        }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean bool){
        isAdmin = bool;
    }
}
