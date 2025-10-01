import java.util.*;
import java.io.*;

public class IntersectionFinder {

    static final double EPS = 1e-8;

    static class Point {
        double x, y;
        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Segment {
        int u, v;
        Segment(int u, int v) {
            this.u = u;
            this.v = v;
        }
    }

    public static boolean contains(List<Point> list, Point p) {
        for (Point q : list) {
            if (Math.abs(p.x - q.x) < EPS && Math.abs(p.y - q.y) < EPS) {
                return true;
            }
        }
        return false;
    }

    public static boolean getIntersection(Point A, Point B, Point C, Point D, Point[] result) {
        double dx1 = B.x - A.x, dy1 = B.y - A.y;
        double dx2 = D.x - C.x, dy2 = D.y - C.y;

        double det = dx1 * (-dy2) - dy1 * (-dx2);
        if (Math.abs(det) < EPS) return false;

        double dx = C.x - A.x, dy = C.y - A.y;
        double t1 = (dx * (-dy2) - dy * (-dx2)) / det;
        double t2 = (dx1 * dy - dy1 * dx) / det;

        if (t1 < -EPS || t1 > 1 + EPS || t2 < -EPS || t2 > 1 + EPS) return false;

        result[0] = new Point(A.x + t1 * dx1, A.y + t1 * dy1);
        return true;
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        int N = sc.nextInt();
        int M = sc.nextInt();
        int P = sc.nextInt(); // 無視
        int Q = sc.nextInt(); // 無視

        Point[] points = new Point[N];
        for (int i = 0; i < N; i++) {
            double x = sc.nextDouble(), y = sc.nextDouble();
            points[i] = new Point(x, y);
        }

        Segment[] segments = new Segment[M];
        for (int i = 0; i < M; i++) {
            int u = sc.nextInt() - 1, v = sc.nextInt() - 1;
            segments[i] = new Segment(u, v);
        }
        

        List<Point> intersections = new ArrayList<>();

        for (int i = 0; i < M; i++) {
            for (int j = i + 1; j < M; j++) {
                Point A = points[segments[i].u];
                Point B = points[segments[i].v];
                Point C = points[segments[j].u];
                Point D = points[segments[j].v];
                Point[] result = new Point[1];
                /*if (getIntersection(A, B, C, D, result)) {
                    if (!contains(intersections, result[0])) {
                        intersections.add(result[0]);
                    }
                }*/
                if (getIntersection(A, B, C, D, result)) {
                    Point inter = result[0];
                    // 重複チェック：交点リストにも入力点にも含まれていない場合だけ追加
                    if (!contains(intersections, inter) && !contains(Arrays.asList(points), inter)) {
                        intersections.add(inter);
                    }
                }
                
            }
        }

        intersections.sort((p1, p2) -> {
            if (Math.abs(p1.x - p2.x) > EPS) return Double.compare(p1.x, p2.x);
            return Double.compare(p1.y, p2.y);
        });

        for (int i = 0; i < intersections.size(); i++) {
            Point p = intersections.get(i);
            System.out.printf("%.5f %.5f%n", p.x, p.y);
        }
    }
}
