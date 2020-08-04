package com.gent00;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
    MessageDigest md ;

    public HashUtil()  {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public  void generateHash(String input) throws Exception{
        md.reset();
        md.update(input.getBytes());
        byte[] digest = md.digest();
    }
}
