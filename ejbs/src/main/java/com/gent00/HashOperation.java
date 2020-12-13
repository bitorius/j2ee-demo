package com.gent00;

import javax.ejb.Stateless;
import java.security.NoSuchAlgorithmException;

@Stateless
public class HashOperation {
    HashUtil hashUtil = new HashUtil();

    public HashOperation() throws NoSuchAlgorithmException {
    }


    public long generateHash(String input) throws Exception {
        return generateHash(input, 1);
    }

    public long generateHash(String input, int count) throws Exception {
        long before = System.currentTimeMillis();
        for (int z = 0; z < count; z++) {
            hashUtil.generateHash(input);
        }
        return (System.currentTimeMillis() - before);
    }

}
