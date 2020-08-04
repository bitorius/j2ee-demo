package com.gent00.test;

import com.gent00.HashOperation;

import static org.junit.jupiter.api.Assertions.*;

class HashOperationTest {

    @org.junit.jupiter.api.Test
    void generateHash() throws Exception {
        HashOperation hashOperation = new HashOperation();
        for (int x = 0; x < 10000000; x++) {
             hashOperation.generateHash("HelloWorld");
        }
    }
}