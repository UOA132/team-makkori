import java.util.*;

public class Problem4_FromFixed {
    static final double EPS = 1e-8;
    static int N;

    static class Point {
        double x, y;
        Point(double x, double y) { this.x = x; this.y = y; }
        double dist(Point other) {
            return Math.hypot(x - other.x, y - other.y);
        }
        @Override public boolean equals(Object obj) {
            if (!(obj instanceof Point)) return false;
            Point p = (Point) obj;
            return Math.abs(x - p.x) < EPS && Math.abs(y - p.y) < EPS;
        }
        @Override public int hashCode() {
            return Objects.hash(Math.round(x * 1e5), Math.round(y * 1e5));
        }
    }

    static class Edge {
        int to;
        double cost;
        Edge(int to, double cost) {
            this.to = to; this.cost = cost;
        }
    }

    static List<Point> points = new ArrayList<>();
    static Map<Point, Integer> pointIndexMap = new HashMap<>();
    static Map<String, Integer> crossPointIndex = new HashMap<>();
    static List<List<Edge>> graph = new ArrayList<>();

    static void addPoint(Point p) {
        if (!pointIndexMap.containsKey(p)) {
            pointIndexMap.put(p, points.size());
            points.add(p);
            graph.add(new ArrayList<>());
        }
    }

    static void addEdge(Point a, Point b) {
        int ai = pointIndexMap.get(a), bi = pointIndexMap.get(b);
        double d = a.dist(b);
        graph.get(ai).add(new Edge(bi, d));
        graph.get(bi).add(new Edge(ai, d));
    }

    static String formatNode(int idx) {
        for (Map.Entry<String, Integer> e : crossPointIndex.entrySet()) {
            if (e.getValue() == idx) return e.getKey();
        }
        return Integer.toString(idx + 1);
    }

    static class Result {
        double[] dist;
        int[] prev;
        Result(double[] d, int[] p) { dist = d; prev = p; }
    }

    static Result dijkstra(int start) {
        int n = points.size();
        double[] dist = new double[n];
        int[] prev = new int[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(prev, -1);
        dist[start] = 0;

        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingDouble(e -> e.cost));
        pq.offer(new Edge(start, 0));

        while (!pq.isEmpty()) {
            Edge cur = pq.poll();
            if (cur.cost > dist[cur.to]) continue;
            for (Edge e : graph.get(cur.to)) {
                double nd = dist[cur.to] + e.cost;
                if (nd < dist[e.to]) {
                    dist[e.to] = nd;
                    prev[e.to] = cur.to;
                    pq.offer(new Edge(e.to, nd));
                }
            }
        }
        return new Result(dist, prev);
    }

    static Integer parseNode(String s) {
        try {
            if (s.startsWith("C")) {
                return crossPointIndex.getOrDefault(s, -1);
            } else {
                int idx = Integer.parseInt(s) - 1;
                return (0 <= idx && idx < points.size()) ? idx : null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    static boolean onSegment(Point a, Point b, Point p) {
        double cross = (p.x - a.x) * (b.y - a.y) - (p.y - a.y) * (b.x - a.x);
        if (Math.abs(cross) > EPS) return false;
        double dot = (p.x - a.x) * (b.x - a.x) + (p.y - a.y) * (b.y - a.y);
        if (dot < -EPS) return false;
        double len2 = (b.x - a.x)*(b.x - a.x) + (b.y - a.y)*(b.y - a.y);
        return dot - len2 < EPS;
    }

    static double pointParamOnSegment(Point a, Point b, Point p) {
        double dx = b.x - a.x, dy = b.y - a.y;
        if (Math.abs(dx) >= Math.abs(dy)) return (Math.abs(dx) < EPS) ? 0 : (p.x - a.x) / dx;
        else return (Math.abs(dy) < EPS) ? 0 : (p.y - a.y) / dy;
    }

    static Point intersect(Point p1, Point q1, Point p2, Point q2) {
        double dx1 = q1.x - p1.x, dy1 = q1.y - p1.y;
        double dx2 = q2.x - p2.x, dy2 = q2.y - p2.y;
        double det = dx1 * (-dy2) + dx2 * dy1;
        if (Math.abs(det) < EPS) return null;
        double s = ((p2.x - p1.x) * (-dy2) + dx2 * (p1.y - p2.y)) / det;
        double t = ((p2.x - p1.x) * (-dy1) + dx1 * (p1.y - p2.y)) / det;
        if (s <= EPS || s >= 1 - EPS || t <= EPS || t >= 1 - EPS) return null;
        return new Point(p1.x + s * dx1, p1.y + s * dy1);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        N = sc.nextInt();
        int M = sc.nextInt(), P = sc.nextInt(), Q = sc.nextInt();
        Point[] inputPoints = new Point[N];
        for (int i = 0; i < N; i++) {
            inputPoints[i] = new Point(sc.nextDouble(), sc.nextDouble());
            addPoint(inputPoints[i]);
        }

        int[][] edges = new int[M][2];
        for (int i = 0; i < M; i++) {
            edges[i][0] = sc.nextInt() - 1;
            edges[i][1] = sc.nextInt() - 1;
        }

        for (int i = 0; i < P; i++) sc.nextDouble(); // ignore

        List<Point> intersections = new ArrayList<>();
        for (int i = 0; i < M; i++) {
            Point a1 = inputPoints[edges[i][0]];
            Point a2 = inputPoints[edges[i][1]];
            for (int j = i + 1; j < M; j++) {
                Point b1 = inputPoints[edges[j][0]];
                Point b2 = inputPoints[edges[j][1]];
                Point inter = intersect(a1, a2, b1, b2);
                if (inter != null && !pointIndexMap.containsKey(inter)) {
                    intersections.add(inter);
                }
            }
        }
        intersections.sort((p1, p2) -> {
            if (Math.abs(p1.x - p2.x) > EPS) return Double.compare(p1.x, p2.x);
            return Double.compare(p1.y, p2.y);
        });
        for (Point p : intersections) {
            addPoint(p);
            String cname = "C" + (crossPointIndex.size() + 1);
            crossPointIndex.put(cname, pointIndexMap.get(p));
        }

        for (int i = 0; i < M; i++) {
            Point a = inputPoints[edges[i][0]];
            Point b = inputPoints[edges[i][1]];
            List<Point> seg = new ArrayList<>();
            seg.add(a); seg.add(b);
            for (Point p : intersections) {
                if (onSegment(a, b, p)) seg.add(p);
            }
            seg.sort(Comparator.comparingDouble(p -> pointParamOnSegment(a, b, p)));
            for (int j = 0; j + 1 < seg.size(); j++) {
                addEdge(seg.get(j), seg.get(j + 1));
            }
        }

        for (int i = 0; i < Q; i++) {
            String s = sc.next(), d = sc.next();
            sc.nextInt(); // k is ignored
            Integer si = parseNode(s), di = parseNode(d);
            if (si == null || di == null) {
                System.out.println("NA");
                continue;
            }
            Result res = dijkstra(si);
            if (res.dist[di] == Double.POSITIVE_INFINITY) {
                System.out.println("NA");
                continue;
            }
            System.out.printf("%.5f%n", res.dist[di]);
            List<Integer> path = new ArrayList<>();
            for (int at = di; at != -1; at = res.prev[at]) path.add(at);
            Collections.reverse(path);
            for (int j = 0; j < path.size(); j++) {
                if (j > 0) System.out.print(" ");
                System.out.print(formatNode(path.get(j)));
            }
            System.out.println();
        }
    }
}
