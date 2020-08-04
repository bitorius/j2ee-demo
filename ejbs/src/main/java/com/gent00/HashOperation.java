package com.gent00;

import javax.ejb.Stateless;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Stateless
public class HashOperation {
    HashUtil hashUtil = new HashUtil();

    public HashOperation() throws NoSuchAlgorithmException {
    }


    public void generateHash(String input) throws Exception {
        hashUtil.generateHash(input);
    }

}
