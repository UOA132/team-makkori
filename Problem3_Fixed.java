import java.util.*;
import java.awt.geom.*;

public class Problem3_Fixed {
    static class Point {
        double x, y;
        Point(double x, double y) {
            this.x = x; this.y = y;
        }
        double distance(Point o) {
            return Math.hypot(this.x - o.x, this.y - o.y);
        }
    }

    static class Edge {
        int to;
        double cost;
        Edge(int to, double cost) {
            this.to = to;
            this.cost = cost;
        }
    }

    static int N, M, Q;
    static List<Point> points = new ArrayList<>();
    static List<int[]> roads = new ArrayList<>();
    static List<List<Edge>> graph = new ArrayList<>();
    static Map<String, Integer> crossPointIndex = new LinkedHashMap<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        N = sc.nextInt();
        M = sc.nextInt();
        int P = sc.nextInt(); // always 0
        Q = sc.nextInt();

        for (int i = 0; i < N; i++) {
            points.add(new Point(sc.nextDouble(), sc.nextDouble()));
        }

        for (int i = 0; i < M; i++) {
            int a = sc.nextInt() - 1;
            int b = sc.nextInt() - 1;
            roads.add(new int[]{a, b});
        }

        findCrossPoints();
        buildGraph();

        List<String[]> queries = new ArrayList<>();
        for (int i = 0; i < Q; i++) {
            String s = sc.next();
            String d = sc.next();
            sc.next(); // dummy
            queries.add(new String[]{s, d});
        }

        List<String> results = new ArrayList<>();
        for (String[] q : queries) {
            Integer si = parseIndex(q[0]);
            Integer di = parseIndex(q[1]);

            if (si == null || di == null) {
                results.add("NA");
                continue;
            }

            double[] dist = dijkstra(si);
            if (dist[di] == Double.POSITIVE_INFINITY) {
                results.add("NA");
            } else {
                results.add(String.format("%.5f", dist[di]));
            }
        }

        for (String res : results) {
            System.out.println(res);
        }
    }

    static Integer parseIndex(String s) {
        if (s.startsWith("C")) {
            return crossPointIndex.getOrDefault(s, null);
        } else {
            int idx = Integer.parseInt(s) - 1;
            return (0 <= idx && idx < points.size()) ? idx : null;
        }
    }

    static void findCrossPoints() {
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < roads.size(); i++) {
            int[] r1 = roads.get(i);
            Point p1 = points.get(r1[0]);
            Point p2 = points.get(r1[1]);

            for (int j = i + 1; j < roads.size(); j++) {
                int[] r2 = roads.get(j);
                Point q1 = points.get(r2[0]);
                Point q2 = points.get(r2[1]);

                Point2D inter = getIntersection(p1, p2, q1, q2);
                    if (inter != null) {
                        String key = String.format("%.5f_%.5f", inter.getX(), inter.getY());

                        // 既にpointsリストに存在する点かどうかチェック
                        boolean alreadyExists = false;
                        for (Point pt : points) {
                            if (Math.abs(pt.x - inter.getX()) < 1e-6 && Math.abs(pt.y - inter.getY()) < 1e-6) {
                                alreadyExists = true;
                                break;
                            }
                        }

                        if (!seen.contains(key) && !alreadyExists) {
                            seen.add(key);
                            points.add(new Point(inter.getX(), inter.getY()));
                            String cname = "C" + (crossPointIndex.size() + 1);
                            crossPointIndex.put(cname, points.size() - 1);
                        }
                    }
                }
            }
        }
    

    static Point2D getIntersection(Point a1, Point a2, Point b1, Point b2) {
        double A1 = a2.y - a1.y, B1 = a1.x - a2.x, C1 = A1 * a1.x + B1 * a1.y;
        double A2 = b2.y - b1.y, B2 = b1.x - b2.x, C2 = A2 * b1.x + B2 * b1.y;
        double det = A1 * B2 - A2 * B1;
        if (Math.abs(det) < 1e-10) return null;

        double x = (B2 * C1 - B1 * C2) / det;
        double y = (A1 * C2 - A2 * C1) / det;

        Point p = new Point(x, y);
        return (onSegment(a1, a2, p) && onSegment(b1, b2, p)) ? new Point2D.Double(x, y) : null;
    }

    static boolean onSegment(Point a, Point b, Point p) {
        double total = a.distance(b);
        double d1 = a.distance(p), d2 = b.distance(p);
        return Math.abs(d1 + d2 - total) < 1e-6;
    }

    static void buildGraph() {
        int size = points.size();
        for (int i = 0; i < size; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] road : roads) {
            int a = road[0], b = road[1];
            Point p1 = points.get(a);
            Point p2 = points.get(b);

            List<Integer> onLine = new ArrayList<>();
            onLine.add(a);

            for (Map.Entry<String, Integer> entry : crossPointIndex.entrySet()) {
                int idx = entry.getValue();
                if (onSegment(p1, p2, points.get(idx))) {
                    onLine.add(idx);
                }
            }

            onLine.add(b);
            onLine.sort(Comparator.comparingDouble(i -> 
                points.get(i).x != points.get(i).x ? 
                points.get(i).y : 
                points.get(i).x + points.get(i).y
            ));

            for (int i = 0; i < onLine.size() - 1; i++) {
                int u = onLine.get(i), v = onLine.get(i + 1);
                double d = points.get(u).distance(points.get(v));
                graph.get(u).add(new Edge(v, d));
                graph.get(v).add(new Edge(u, d));
            }
        }
    }

    static double[] dijkstra(int start) {
        int n = points.size();
        double[] dist = new double[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        dist[start] = 0;

        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingDouble(e -> e.cost));
        pq.offer(new Edge(start, 0));

        while (!pq.isEmpty()) {
            Edge cur = pq.poll();
            if (cur.cost > dist[cur.to]) continue;
            for (Edge e : graph.get(cur.to)) {
                if (dist[e.to] > dist[cur.to] + e.cost) {
                    dist[e.to] = dist[cur.to] + e.cost;
                    pq.offer(new Edge(e.to, dist[e.to]));
                }
            }
        }

        return dist;
    }
}
