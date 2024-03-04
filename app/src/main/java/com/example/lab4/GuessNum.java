package com.example.lab4;

import java.util.Random;

public class GuessNum {
    static public int rndCompNum(int min, int max)
    {
        int diff = max - min;
        Random random = new Random();
        return random.nextInt(diff+1)+min;
    }
}
