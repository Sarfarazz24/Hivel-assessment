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
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;
            
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    numerator = numerator.multiply(points.get(j).x.negate());
                    denominator = denominator.multiply(points.get(i).x.subtract(points.get(j).x));
                }
            }
            
            BigInteger term = points.get(i).y.multiply(numerator).divide(denominator);
            constant = constant.add(term);
        }
        
        return constant;
    }
    
    public static void main(String[] args) {
        try {
            String jsonContent = new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get("testcase.json")));
            
            Pattern nPattern = Pattern.compile("\"n\"\\s*:\\s*(\\d+)");
            Pattern kPattern = Pattern.compile("\"k\"\\s*:\\s*(\\d+)");
            
            Matcher nMatcher = nPattern.matcher(jsonContent);
            Matcher kMatcher = kPattern.matcher(jsonContent);
            
            int n = 0, k = 0;
            if (nMatcher.find()) n = Integer.parseInt(nMatcher.group(1));
            if (kMatcher.find()) k = Integer.parseInt(kMatcher.group(1));
            
            List<Point> points = new ArrayList<>();
            
            Pattern pointPattern = Pattern.compile(
                "\"(\\d+)\"\\s*:\\s*\\{[^}]*\"base\"\\s*:\\s*\"(\\d+)\"[^}]*\"value\"\\s*:\\s*\"([a-zA-Z0-9]+)\""
            );
            
            Matcher pointMatcher = pointPattern.matcher(jsonContent);
            
            while (pointMatcher.find() && points.size() < k) {
                int xValue = Integer.parseInt(pointMatcher.group(1));
                int base = Integer.parseInt(pointMatcher.group(2));
                String value = pointMatcher.group(3);
                
                BigInteger x = BigInteger.valueOf(xValue);
                BigInteger y = baseToDecimal(value, base);
                
                points.add(new Point(x, y));
            }
            
            BigInteger result = calculateConstant(points);
            System.out.println(result);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    static class Point {
        BigInteger x;
        BigInteger y;
        
        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
}
