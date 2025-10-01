import java.util.*;

public class Problem6 {
    static final double EPS = 1e-8;

    static class Point {
        double x, y;
        Point(double x, double y) { this.x = x; this.y = y; }
        double dist(Point p) { return Math.hypot(x - p.x, y - p.y); }
    }

    static class Edge {
        int to;
        double cost;
        Edge(int to, double cost) { this.to = to; this.cost = cost; }
    }

    static class Path implements Comparable<Path> {
        double cost;
        List<Integer> nodes;
        Path(double cost, List<Integer> nodes) {
            this.cost = cost;
            this.nodes = new ArrayList<>(nodes);
        }
        public int compareTo(Path o) {
            return Double.compare(this.cost, o.cost);
        }
        public String toString() {
            return String.format("%.5f\n%s", cost, String.join(" ", formatPath(nodes)));
        }
    }

    static List<Point> points = new ArrayList<>();
    static List<List<Edge>> graph = new ArrayList<>();
    static Map<String, Integer> crossIndex = new HashMap<>();
    static Map<Integer, String> reverseIndex = new HashMap<>();
    static int N;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        N = sc.nextInt();
        int M = sc.nextInt(), P = sc.nextInt(), Q = sc.nextInt();

        for (int i = 0; i < N; i++) {
            points.add(new Point(sc.nextDouble(), sc.nextDouble()));
        }

        List<int[]> segments = new ArrayList<>();
        for (int i = 0; i < M; i++) {
            int u = sc.nextInt() - 1, v = sc.nextInt() - 1;
            segments.add(new int[]{u, v});
        }

        // 交差点検出（既存点なら追加・命名せず、真の交点だけ追加・命名）
        for (int i = 0; i < M; i++) {
            for (int j = i + 1; j < M; j++) {
                int[] s1 = segments.get(i), s2 = segments.get(j);
                Point a = points.get(s1[0]), b = points.get(s1[1]);
                Point c = points.get(s2[0]), d = points.get(s2[1]);
                Point p = getIntersection(a, b, c, d);
                if (p != null) {
                    boolean exists = false;
                    for (int idx = 0; idx < points.size(); idx++) {
                        Point q = points.get(idx);
                        if (Math.abs(q.x - p.x) < EPS && Math.abs(q.y - p.y) < EPS) {
                            exists = true; // 既存点なら何もしない（その点を交点扱い）
                            break;
                        }
                    }
                    if (!exists) {
                        String cname = "C" + (crossIndex.size() + 1);
                        crossIndex.put(cname, points.size());
                        reverseIndex.put(points.size(), cname);
                        points.add(p);
                    }
                }
            }
        }

        int V = points.size();
        for (int i = 0; i < V; i++) graph.add(new ArrayList<>());

        for (int[] seg : segments) {
            Point a = points.get(seg[0]), b = points.get(seg[1]);
            List<Integer> list = new ArrayList<>();
            list.add(seg[0]);
            for (int i = 0; i < V; i++) {
                if (i != seg[0] && i != seg[1] && onSegment(a, b, points.get(i))) {
                    list.add(i);
                }
            }
            list.add(seg[1]);
            list.sort(Comparator.comparingDouble(i -> projection(a, b, points.get(i))));
            for (int i = 0; i < list.size() - 1; i++) {
                int u = list.get(i), vtx = list.get(i + 1);
                double dist = points.get(u).dist(points.get(vtx));
                graph.get(u).add(new Edge(vtx, dist));
                graph.get(vtx).add(new Edge(u, dist));
            }
        }

        List<String[]> queries = new ArrayList<>();
        for (int i = 0; i < Q; i++) {
            String s = sc.next(), t = sc.next();
            int k = sc.nextInt();
            queries.add(new String[]{s, t, String.valueOf(k)});
        }

        for (String[] query : queries) {
            processQuery(query);
        }
    }
    static void processQuery(String[] query) {
        int start = getIndex(query[0]);
        int goal = getIndex(query[1]);
        int k = Integer.parseInt(query[2]);

        if (start == -1 || goal == -1 || start >= graph.size() || goal >= graph.size()) {
            System.out.println("NA");
            return;
        }

        List<Path> paths = yenAlgorithm(start, goal, k);
        if (paths.isEmpty()) {
            System.out.println("NA");
        } else {
            for (Path p : paths) {
                System.out.println(p.toString());
            }
        }
    }

    static List<Path> yenAlgorithm(int start, int goal, int K) {
        List<Path> A = new ArrayList<>();
        PriorityQueue<Path> B = new PriorityQueue<>();
        Set<String> seen = new HashSet<>();

        Path first = dijkstraPath(graph, start, goal);  // ← 正しい呼び出し方

        if (first == null) return A;
        A.add(first);
        seen.add(first.nodes.toString());

        for (int k = 1; k < K; k++) {
            Path prev = A.get(k - 1);
            for (int i = 0; i < prev.nodes.size() - 1; i++) {
                int spurNode = prev.nodes.get(i);
                List<Integer> rootPath = new ArrayList<>(prev.nodes.subList(0, i + 1));

                List<List<Edge>> modGraph = cloneGraph(graph);
                for (Path p : A) {
                    if (p.nodes.size() > i && rootPath.equals(p.nodes.subList(0, i + 1))) {
                        int u = p.nodes.get(i), v = p.nodes.get(i + 1);
                        modGraph.get(u).removeIf(e -> e.to == v);
                    }
                }

                Set<Integer> blocked = new HashSet<>(rootPath.subList(0, i));
                for (int b : blocked) modGraph.get(b).clear();

                Path spur = dijkstraPath(modGraph, spurNode, goal);
                if (spur == null) continue;

                List<Integer> total = new ArrayList<>(rootPath);
                total.remove(total.size() - 1);
                total.addAll(spur.nodes);

                if (new HashSet<>(total).size() != total.size()) continue;
                if (seen.add(total.toString())) {
                    double cost = 0;
                    for (int j = 0; j < total.size() - 1; j++) {
                        int u = total.get(j), v = total.get(j + 1);
                        for (Edge e : graph.get(u)) {
                            if (e.to == v) {
                                cost += e.cost;
                                break;
                            }
                        }
                    }
                    B.add(new Path(cost, total));
                }
            }
            if (B.isEmpty()) break;
            A.add(B.poll());
        }

        return A;
    }

    static Path dijkstraPath(List<List<Edge>> g, int s, int t) {
        int n = g.size();
        double[] dist = new double[n];
        int[] prev = new int[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(prev, -1);
        dist[s] = 0;
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingDouble(e -> e.cost));
        pq.add(new Edge(s, 0));
        while (!pq.isEmpty()) {
            Edge cur = pq.poll();
            if (dist[cur.to] < cur.cost) continue;
            for (Edge e : g.get(cur.to)) {
                double d2 = dist[cur.to] + e.cost;
                if (d2 < dist[e.to]) {
                    dist[e.to] = d2;
                    prev[e.to] = cur.to;
                    pq.add(new Edge(e.to, d2));
                }
            }
        }
        if (dist[t] == Double.POSITIVE_INFINITY) return null;
        List<Integer> path = new ArrayList<>();
        for (int at = t; at != -1; at = prev[at]) path.add(at);
        Collections.reverse(path);
        return new Path(dist[t], path);
    }

    static List<List<Edge>> cloneGraph(List<List<Edge>> g) {
        List<List<Edge>> clone = new ArrayList<>();
        for (List<Edge> list : g) {
            List<Edge> copy = new ArrayList<>();
            for (Edge e : list) copy.add(new Edge(e.to, e.cost));
            clone.add(copy);
        }
        return clone;
    }

    static boolean onSegment(Point a, Point b, Point p) {
        return Math.abs(p.dist(a) + p.dist(b) - a.dist(b)) < 1e-6;
    }

    static Point getIntersection(Point A, Point B, Point C, Point D) {
        double dx1 = B.x - A.x, dy1 = B.y - A.y;
        double dx2 = D.x - C.x, dy2 = D.y - C.y;
        double det = dx1 * dy2 - dy1 * dx2;
        if (Math.abs(det) < EPS) return null;
        double t = ((C.x - A.x) * dy2 - (C.y - A.y) * dx2) / det;
        double u = ((C.x - A.x) * dy1 - (C.y - A.y) * dx1) / det;
        if (t < -EPS || t > 1 + EPS || u < -EPS || u > 1 + EPS) return null;
        return new Point(A.x + t * dx1, A.y + t * dy1);
    }

    static double projection(Point a, Point b, Point p) {
        double dx = b.x - a.x, dy = b.y - a.y;
        double len2 = dx * dx + dy * dy;
        double dot = (p.x - a.x) * dx + (p.y - a.y) * dy;
        return len2 == 0 ? 0 : dot / len2;
    }

    static int getIndex(String s) {
        if (s.startsWith("C")) return crossIndex.getOrDefault(s, -1);
        try {
            int idx = Integer.parseInt(s) - 1;
            return idx;
        } catch (Exception e) {
            return -1;
        }
    }

    static List<String> formatPath(List<Integer> path) {
        List<String> res = new ArrayList<>();
        for (int idx : path) {
            if (idx < N) res.add(String.valueOf(idx + 1));
            else res.add(reverseIndex.getOrDefault(idx, "?"));
        }
        return res;
    }
}