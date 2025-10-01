import java.util.Scanner;

public class Findcross {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        int N = sc.nextInt();
        int M = sc.nextInt();
        sc.nextInt(); // P (今回は使用しない)
        sc.nextInt(); // Q (今回は使用しない)
        
        int[][] points = new int[N][2];
        for (int i = 0; i < N; i++) {
            points[i][0] = sc.nextInt();
            points[i][1] = sc.nextInt();
        }
        
        int[][] roads = new int[M][2];
        for (int i = 0; i < M; i++) {
            roads[i][0] = sc.nextInt() - 1; 
            roads[i][1] = sc.nextInt() - 1;
        }
        
        double[] ans = findcross(
            points[roads[0][0]][0], points[roads[0][0]][1],
            points[roads[0][1]][0], points[roads[0][1]][1],
            points[roads[1][0]][0], points[roads[1][0]][1],
            points[roads[1][1]][0], points[roads[1][1]][1]
        );
        
        if (ans == null) {
            System.out.println("NA");
        } else {
            if (ans[0] == (int)ans[0]) {
                System.out.print((int)ans[0]);
            } else {
                System.out.printf("%.5f", ans[0]);
            }
            System.out.print(" ");
            if (ans[1] == (int)ans[1]) {
                System.out.print((int)ans[1]);
            } else {
                System.out.printf("%.5f", ans[1]);
            }
            System.out.println();
        }
    }
    
    static double[] findcross(int x1, int y1, int x2, int y2,
                              int x3, int y3, int x4, int y4) {
        //線型方程式//
        double a1 = y2 - y1;
        double b1 = x1 - x2;
        double c1 = a1 * x1 + b1 * y1;
        
        double a2 = y4 - y3;
        double b2 = x3 - x4;
        double c2 = a2 * x3 + b2 * y3;
        
        double determinant = a1 * b2 - a2 * b1; //determinant = 行列式//
        
        if (determinant == 0) {
            return null;
        } else {
            double x = (b2 * c1 - b1 * c2) / determinant;
            double y = (a1 * c2 - a2 * c1) / determinant;
            
            if (checkOnroad(x1, y1, x2, y2, x, y) && checkOnroad(x3, y3, x4, y4, x, y)) {
                if (overlapEnd(x1, y1, x2, y2, x3, y3, x4, y4, x, y)) {
                    return null;
                }
                return new double[]{x, y};
            } else {
                return null;
            }
        }
    }
    
    static boolean checkOnroad(int x1, int y1, int x2, int y2, double x, double y) {
        // xが範囲内にあるか確認
    boolean xRange = (x >= Math.min(x1, x2) && x <= Math.max(x1, x2));
    // yが範囲内にあるか確認
    boolean yRange = (y >= Math.min(y1, y2) && y <= Math.max(y1, y2));
    
    return xRange && yRange;
    }//交点が線分の間に含まれているか //
    
    static boolean overlapEnd(int x1, int y1, int x2, int y2,
                             int x3, int y3, int x4, int y4,
                             double x, double y) {
        boolean onRoad1Endpoint = (almostEqual(x, x1) && almostEqual(y, y1)) ||
                               (almostEqual(x, x2) && almostEqual(y, y2));
        boolean onRoad2Endpoint = (almostEqual(x, x3) && almostEqual(y, y3)) ||
                               (almostEqual(x, x4) && almostEqual(y, y4));
        return onRoad1Endpoint && onRoad2Endpoint;
    }//交点が端の点と重なっているか確認　//
    
    static boolean almostEqual(double a, double b) {
        return Math.abs(a - b) < 1e-8;
    }
}