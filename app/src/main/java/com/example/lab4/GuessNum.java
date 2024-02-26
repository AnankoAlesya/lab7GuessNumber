package com.example.lab4;

import java.util.Random;

public class GuessNum {
    static public int rndCompNum()
    {
        int min = 10;
        int max =99;
        int diff = max - min;
        Random random = new Random();
        return random.nextInt(diff+1)+min;
    }
}
