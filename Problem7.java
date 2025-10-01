import java.util.*;

public class Problem7 {
    static final double EPS = 1e-8;

    static class Point {
        double x, y;
        Point(double x, double y) { this.x = x; this.y = y; }
        double dist(Point p) {
            return Math.hypot(x - p.x, y - p.y);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int N = sc.nextInt(), M = sc.nextInt(), P = sc.nextInt(), Q = sc.nextInt();
        List<Point> nodes = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            nodes.add(new Point(sc.nextDouble(), sc.nextDouble()));
        }

        int[][] edges = new int[M][2];
        for (int i = 0; i < M; i++) {
            edges[i][0] = sc.nextInt() - 1;
            edges[i][1] = sc.nextInt() - 1;
        }

        List<Point> queries = new ArrayList<>();
        for (int i = 0; i < P; i++) {
            queries.add(new Point(sc.nextDouble(), sc.nextDouble()));
        }

        for (Point newPoint : queries) {
            Point best = null;
            double bestDist = Double.POSITIVE_INFINITY;

            // 既存ノードと一致チェック
            for (Point p : nodes) {
                if (newPoint.dist(p) < EPS) {
                    best = p;
                    bestDist = 0;
                    break;
                }
            }

            if (best == null) {
                // 線分との最近点を探す
                for (int i = 0; i < edges.length; i++) {
                    Point a = nodes.get(edges[i][0]);
                    Point b = nodes.get(edges[i][1]);
                    Point proj = projectPointToSegment(a, b, newPoint);
                    double d = newPoint.dist(proj);
                    if (d + EPS < bestDist) {
                        bestDist = d;
                        best = proj;
                    }
                }
            }

            System.out.printf("%.5f %.5f\n", best.x, best.y);
            nodes.add(newPoint);  // 新点をネットワークに追加
        }
    }

    // 点pから線分abへの射影点（最近点）を求める
    static Point projectPointToSegment(Point a, Point b, Point p) {
        double dx = b.x - a.x, dy = b.y - a.y;
        double lenSq = dx * dx + dy * dy;
        if (lenSq < EPS) return a;
        double t = ((p.x - a.x) * dx + (p.y - a.y) * dy) / lenSq;
        t = Math.max(0, Math.min(1, t));
        return new Point(a.x + t * dx, a.y + t * dy);
    }
}