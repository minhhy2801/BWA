package capstone.bwa.demo.utils;

import java.util.Random;

public class RandomNumUtils {
    // Generates a random int with n digits
    public static String generateRandomDigits(int n) {
        int m = (int) Math.pow(10, n - 1);
        return m + new Random().nextInt(9 * m) + "";
    }
}
