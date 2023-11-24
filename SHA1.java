import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class SHA1 {
    public static String sha1(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String input = "03-A-1012;1110;579.83";
        System.out.print("Ingresa la dificultad: ");
        int difficulty = sc.nextInt();
        long startTime;
        long endTime;
        startTime = System.currentTimeMillis();

        while(true) {
            String nonce = String.valueOf((int) (Math.random() * 1000000000));
            String newInput = input + nonce;
            System.out.println(input + " + " + nonce + " = " + newInput);
            String inputHash = sha1(newInput);
            System.out.println("Hash " + inputHash);
            
            if (inputHash.startsWith("0".repeat(difficulty))) {
                System.out.println("Nonce: " + nonce);
                System.out.println("Hash: " + inputHash);
                break;
            }
        }
        endTime = System.currentTimeMillis();
        System.out.println("Tiempo: " + (endTime - startTime) + " ms");
    }
}
