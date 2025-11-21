import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.*;

public class secret {
    
    
    public static BigInteger baseToDecimal(String value, int base) {
        return new BigInteger(value, base);
    }
    
    public static BigInteger calculateConstant(List<Point> points) {
        int k = points.size();
        BigInteger constant = BigInteger.ZERO;
        
        for (int i = 0; i < k; i++) {
            BigInteger li = BigInteger.ONE;  // Lagrange basis polynomial
            
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    // Calculate L_i(0) = product of (-xj)/(xi - xj)
                    BigInteger numerator = BigInteger.ZERO.subtract(points.get(j).x);
                    BigInteger denominator = points.get(i).x.subtract(points.get(j).x);
                    li = li.multiply(numerator).divide(denominator);
                }
            }
            
            // Add weighted y value
            constant = constant.add(points.get(i).y.multiply(li));
        }
        
        return constant;
    }
    
    public static void main(String[] args) {
        try {
            // Read the JSON file
            String jsonContent = new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get("testcase.json")));
            
            // Parse n and k values
            Pattern nPattern = Pattern.compile("\"n\"\\s*:\\s*(\\d+)");
            Pattern kPattern = Pattern.compile("\"k\"\\s*:\\s*(\\d+)");
            
            Matcher nMatcher = nPattern.matcher(jsonContent);
            Matcher kMatcher = kPattern.matcher(jsonContent);
            
            int n = 0, k = 0;
            if (nMatcher.find()) n = Integer.parseInt(nMatcher.group(1));
            if (kMatcher.find()) k = Integer.parseInt(kMatcher.group(1));
            
            // Extract all points from JSON
            List<Point> points = new ArrayList<>();
            Pattern pointPattern = Pattern.compile(
                "\"(\\d+)\"\\s*:\\s*\\{[^}]*\"base\"\\s*:\\s*\"(\\d+)\"[^}]*\"value\"\\s*:\\s*\"([^\"]+)\""
            );
            
            Matcher pointMatcher = pointPattern.matcher(jsonContent);
            
            while (pointMatcher.find() && points.size() < k) {
                int x = Integer.parseInt(pointMatcher.group(1));
                int base = Integer.parseInt(pointMatcher.group(2));
                String value = pointMatcher.group(3);
                
                BigInteger xCoord = BigInteger.valueOf(x);
                BigInteger yCoord = baseToDecimal(value, base);
                
                points.add(new Point(xCoord, yCoord));
            }
            
            // Calculate and print the constant term
            BigInteger result = calculateConstant(points);
            System.out.println(result);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    // Simple Point class to hold coordinates
    static class Point {
        BigInteger x;
        BigInteger y;
        
        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
}
