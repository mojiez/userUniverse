package com.aiyichen.admindemo.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmUtilsTest {

//    private AlgorithmUtils algorithmUtils;

    @Test
    void minDistance() {
        String str1 = "kj大队";
        String str2 = "kj小队";
        String str3 = "yi小队";
        int score_12 = AlgorithmUtils.minDistance(str1, str2);
        int score_13 = AlgorithmUtils.minDistance(str1,str3);
        int score_23 = AlgorithmUtils.minDistance(str2,str3);
        System.out.println("12: " + score_12);
        System.out.println("13: " + score_13);
        System.out.println("23: " + score_23);

    }

    @Test
    void testMinDistance() {

    }
}