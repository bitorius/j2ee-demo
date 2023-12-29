package com.gent00;

import org.apache.commons.lang3.RandomUtils;

import java.util.*;

public class MemoryLeak {
    public static void main(String args[]) {

        System.out.println("Time to use memory");
        List<Map<Integer, Integer>> maps = new ArrayList<>();

        for (int x = 0; x < 9000000; x++) {
            HashMap map = new HashMap<Integer, Integer>();
            for (int y = 0; y < 4096; y++) {
                map.put(1, RandomUtils.nextInt());
            }
            maps.add(map);
        }
    }


}
