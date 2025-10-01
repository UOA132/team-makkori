import java.util.*;
import java.awt.geom.*; 
import java.io.File; 
import java.io.FileInputStream; 
import java.io.FileNotFoundException; 

public class Problem9 {
    static final double EPS = 1e-8;

    static class Point {
        double x, y;

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        double distance(Point o) {
            return Math.hypot(this.x - o.x, this.y - o.y);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Point)) return false;
            Point p = (Point) obj;
            return Math.abs(x - p.x) < EPS && Math.abs(y - p.y) < EPS;
        }

        @Override
        public int hashCode() {
            return Objects.hash(Math.round(x / EPS), Math.round(y / EPS));
        }
    }

    static class Edge {
        int to;
        double cost;
        int segId;

        Edge(int to, double cost) {
            this(to, cost, -1);
        }

        Edge(int to, double cost, int segId) {
            this.to = to;
            this.cost = cost;
            this.segId = segId;
        }
    }

    static class Path implements Comparable<Path> {
        double cost;
        List<Integer> nodes;

        Path(double c, List<Integer> ns) {
            cost = c;
            nodes = new ArrayList<>(ns);
        }

        @Override
        public int compareTo(Path o) {
            if (Math.abs(this.cost - o.cost) > EPS) {
                return Double.compare(this.cost, o.cost);
            }
            for (int i = 0; i < Math.min(this.nodes.size(), o.nodes.size()); i++) {
                int cmp = Integer.compare(this.nodes.get(i), o.nodes.get(i));
                if (cmp != 0) return cmp;
            }
            return Integer.compare(this.nodes.size(), o.nodes.size());
        }

        public String formatPathNodes(int N_original, Map<Integer, String> reverseIndex) {
            List<String> res = new ArrayList<>();
            for (int idx : nodes) {
                if (idx < N_original) {
                    res.add(String.valueOf(idx + 1));
                } else {
                    res.add(reverseIndex.getOrDefault(idx, String.valueOf(idx + 1)));
                }
            }
            return String.join(" ", res);
        }
    }

    static int N_initial;
    static List<Point> allPoints = new ArrayList<>();
    static List<List<Edge>> graph = new ArrayList<>();
    static Map<String, Integer> crossPointNameToIndex = new LinkedHashMap<>();
    static Map<Integer, String> indexToCrossPointName = new HashMap<>();
    static Map<Point, Integer> pointToIndexMap = new HashMap<>();
    static int roadSegmentCounter;
    static int P_in_global;

    public static void main(String[] args) {
        Scanner sc = null;
        try {
            File inputFile = new File("generated_test_data/input_9_1_1.txt"); 
            
            sc = new Scanner(new FileInputStream(inputFile));
            sc.useDelimiter("\\s+|#.*\\R"); 
        } catch (FileNotFoundException e) {
            System.err.println("エラー: ファイルが見つかりません: " + e.getMessage());
            return;
        }

        int N_in, M_in, P_in, Q_in;
        N_in = sc.nextInt();
        M_in = sc.nextInt();
        P_in = sc.nextInt();
        Q_in = sc.nextInt();
        N_initial = N_in;
        P_in_global = P_in;

        for (int i = 0; i < N_in; i++) {
            Point p = new Point(sc.nextDouble(), sc.nextDouble());
            addPointToGlobalList(p);
        }

        List<int[]> initialRoads = new ArrayList<>();
        for (int i = 0; i < M_in; i++) {
            initialRoads.add(new int[]{sc.nextInt() - 1, sc.nextInt() - 1});
        }
        roadSegmentCounter = M_in;

        buildInitialGraph(initialRoads);

        List<Point> newPointsToAdd = new ArrayList<>();
        for (int i = 0; i < P_in; i++) {
            newPointsToAdd.add(new Point(sc.nextDouble(), sc.nextDouble())); 
        }
        for (Point newP : newPointsToAdd) {
            int newPointIdx = addPointToGlobalList(newP);
            connectNewPointToNetwork(newPointIdx);
        }
        
        for (int qCount = 0; qCount < Q_in; qCount++) {
            if (!sc.hasNext()) {
                break;
            }
            String command = sc.next();

            if (command.equals("distance")) {
                String s = sc.next();
                String d = sc.next();
                if (!sc.hasNextInt()) {
                    System.err.println("Error: Expected integer for 'k' after distance query for " + s + " " + d);
                    continue; 
                }
                int k = sc.nextInt();
                processQuery(new String[]{s, d, String.valueOf(k)});
            } else if (command.equals("detect_bridges")) {
                printBridges();
            } else {
                System.err.println("Unknown command or malformed input: " + command);
            }
        }
        sc.close();
    }

    static int addPointToGlobalList(Point p) {
        if (!pointToIndexMap.containsKey(p)) {
            pointToIndexMap.put(p, allPoints.size());
            allPoints.add(p);
            graph.add(new ArrayList<>());
            return allPoints.size() - 1;
        }
        return pointToIndexMap.get(p);
    }

    static int getPointIndex(String s) {
        if (s.startsWith("C")) {
            return crossPointNameToIndex.getOrDefault(s, -1);
        } else if (s.startsWith("P")) {
            try {
                int pNum = Integer.parseInt(s.substring(1));
                int idx = N_initial + (pNum - 1);
                return (idx >= N_initial && idx < allPoints.size()) ? idx : -1;
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        try {
            int idx = Integer.parseInt(s) - 1;
            return (idx >= 0 && idx < N_initial) ? idx : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static String formatOutputNode(int idx) {
        if (idx < N_initial) {
            return String.valueOf(idx + 1);
        } else if (idx >= N_initial && idx < N_initial + P_in_global) {
            return "P" + (idx - N_initial + 1); 
        }
        return indexToCrossPointName.getOrDefault(idx, String.valueOf(idx + 1));
    }

    static void addEdge(int u, int v, double cost, int segId) {
        graph.get(u).add(new Edge(v, cost, segId));
        graph.get(v).add(new Edge(u, cost, segId));
    }

    static void removeEdge(int u, int v, int segId) {
        graph.get(u).removeIf(e -> e.to == v && e.segId == segId);
        graph.get(v).removeIf(e -> e.to == u && e.segId == segId);
    }

    static double getEdgeCost(int u, int v) {
        for (Edge e : graph.get(u)) {
            if (e.to == v) return e.cost;
        }
        return Double.POSITIVE_INFINITY;
    }

    static List<List<Edge>> cloneGraph(List<List<Edge>> originalGraph) {
        List<List<Edge>> clonedGraph = new ArrayList<>();
        for (List<Edge> adjList : originalGraph) {
            List<Edge> clonedAdjList = new ArrayList<>();
            for (Edge edge : adjList) {
                clonedAdjList.add(new Edge(edge.to, edge.cost, edge.segId));
            }
            clonedGraph.add(clonedAdjList);
        }
        return clonedGraph;
    }

    static boolean onSegment(Point a, Point b, Point p) {
        double crossProduct = (p.y - a.y) * (b.x - a.x) - (p.x - a.x) * (b.y - a.y);
        if (Math.abs(crossProduct) > EPS) {
            return false;
        }

        double dotProduct = (p.x - a.x) * (b.x - a.x) + (p.y - a.y) * (b.y - a.y);
        double squaredLength = (b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y);
        return dotProduct >= -EPS && dotProduct <= squaredLength + EPS;
    }

    static Point getIntersection(Point p1, Point q1, Point p2, Point q2) {
        double dx1 = q1.x - p1.x, dy1 = q1.y - p1.y;
        double dx2 = q2.x - p2.x, dy2 = q2.y - p2.y;

        double determinant = dx1 * dy2 - dy1 * dx2;

        if (Math.abs(determinant) < EPS) {
            return null;
        }

        double t = ((p2.x - p1.x) * dy2 - (p2.y - p1.y) * dx2) / determinant;
        double u = ((p2.x - p1.x) * dy1 - (p2.y - p1.y) * dx1) / determinant;

        if (t <= EPS || t >= 1 - EPS || u <= EPS || u >= 1 - EPS) {
            return null;
        }

        return new Point(p1.x + t * dx1, p1.y + t * dy1);
    }

    static double getProjectionParameter(Point a, Point b, Point p) {
        double dx = b.x - a.x, dy = b.y - a.y;
        double lenSq = dx * dx + dy * dy;
        if (lenSq < EPS) return 0;
        return ((p.x - a.x) * dx + (p.y - a.y) * dy) / lenSq;
    }

    static double[] projectPointToSegmentAndGetDistance(Point a, Point b, Point p) {
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        double lenSq = dx * dx + dy * dy;

        double t = 0;
        if (lenSq > EPS) {
            t = ((p.x - a.x) * dx + (p.y - a.y) * dy) / lenSq;
        }
        
        t = Math.max(0, Math.min(1, t));

        Point projectedPoint = new Point(a.x + t * dx, a.y + t * dy);
        return new double[]{t, p.distance(projectedPoint)};
    }

    static void buildInitialGraph(List<int[]> initialRoads) {
        List<Point> newIntersections = new ArrayList<>();
        Set<String> addedCrossPointKeys = new HashSet<>();

        for (int i = 0; i < initialRoads.size(); i++) {
            int[] r1 = initialRoads.get(i);
            Point p1_road1 = allPoints.get(r1[0]);
            Point p2_road1 = allPoints.get(r1[1]);

            for (int j = i + 1; j < initialRoads.size(); j++) {
                int[] r2 = initialRoads.get(j);
                Point p1_road2 = allPoints.get(r2[0]);
                Point p2_road2 = allPoints.get(r2[1]);

                Point intersection = getIntersection(p1_road1, p2_road1, p1_road2, p2_road2);
                
                if (intersection != null) {
                    String key = String.format(Locale.US, "%.5f_%.5f", intersection.x, intersection.y);
                    if (!addedCrossPointKeys.contains(key) && !pointToIndexMap.containsKey(intersection)) {
                        newIntersections.add(intersection);
                        addedCrossPointKeys.add(key);
                    }
                }
            }
        }

        newIntersections.sort((pt1, pt2) -> {
            if (Math.abs(pt1.x - pt2.x) > EPS) {
                return Double.compare(pt1.x, pt2.x);
            }
            return Double.compare(pt1.y, pt2.y);
        });

        for (Point p : newIntersections) {
            int idx = addPointToGlobalList(p);
            String cName = "C" + (crossPointNameToIndex.size() + 1);
            crossPointNameToIndex.put(cName, idx);
            indexToCrossPointName.put(idx, cName);
        }

        for (int segId = 0; segId < initialRoads.size(); segId++) {
            int[] road = initialRoads.get(segId);
            Point startPoint = allPoints.get(road[0]);
            Point endPoint = allPoints.get(road[1]);

            List<Integer> pointsOnRoad = new ArrayList<>();
            pointsOnRoad.add(road[0]); 
            pointsOnRoad.add(road[1]); 

            for (int i = 0; i < allPoints.size(); i++) {
                if (i != road[0] && i != road[1] && onSegment(startPoint, endPoint, allPoints.get(i))) {
                    pointsOnRoad.add(i);
                }
            }

            pointsOnRoad.sort(Comparator.comparingDouble(idx ->
                    getProjectionParameter(startPoint, endPoint, allPoints.get(idx))));

            for (int i = 0; i < pointsOnRoad.size() - 1; i++) {
                int u = pointsOnRoad.get(i);
                int v = pointsOnRoad.get(i + 1);
                double dist = allPoints.get(u).distance(allPoints.get(v));
                addEdge(u, v, dist, segId);
            }
        }
    }

    static void connectNewPointToNetwork(int newPointIdx) {
        Point newPoint = allPoints.get(newPointIdx);
        double minDistance = Double.POSITIVE_INFINITY;
        Point connectionPoint = null;
        int targetSegId = -1; 
        int bestU = -1, bestV = -1; 

        for (int i = 0; i < allPoints.size(); i++) {
            if (i == newPointIdx) continue;

            Point existingPoint = allPoints.get(i);
            double dist = newPoint.distance(existingPoint);
            if (dist < minDistance - EPS) { 
                minDistance = dist;
                connectionPoint = existingPoint;
                targetSegId = -1;
                bestU = i;
                bestV = -1;
            } else if (Math.abs(dist - minDistance) < EPS) {
                if (connectionPoint == null || pointToIndexMap.get(existingPoint) < pointToIndexMap.get(connectionPoint)) {
                    connectionPoint = existingPoint;
                    targetSegId = -1;
                    bestU = i;
                    bestV = -1;
                }
            }
        }

        for (int u = 0; u < graph.size(); u++) {
            for (Edge edge : graph.get(u)) {
                int v = edge.to;
                if (u >= v) continue;

                Point p1 = allPoints.get(u);
                Point p2 = allPoints.get(v);

                double[] projResult = projectPointToSegmentAndGetDistance(p1, p2, newPoint); 
                double t_param = projResult[0];
                double distToSegment = projResult[1];

                if (distToSegment < minDistance - EPS) {
                    minDistance = distToSegment;
                    connectionPoint = new Point(p1.x + t_param * (p2.x - p1.x), p1.y + t_param * (p2.y - p1.y));
                    targetSegId = edge.segId;
                    bestU = u;
                    bestV = v;
                } else if (Math.abs(distToSegment - minDistance) < EPS) {
                    if (targetSegId == -1 || edge.segId < targetSegId) { 
                        minDistance = distToSegment;
                        connectionPoint = new Point(p1.x + t_param * (p2.x - p1.x), p1.y + t_param * (p2.y - p1.y));
                        targetSegId = edge.segId;
                        bestU = u;
                        bestV = v;
                    }
                }
            }
        }

        System.out.printf(Locale.US, "%.5f %.5f%n", connectionPoint.x, connectionPoint.y); 

        int actualConnectionPointIdx = addPointToGlobalList(connectionPoint);
        addEdge(newPointIdx, actualConnectionPointIdx, newPoint.distance(allPoints.get(actualConnectionPointIdx)), roadSegmentCounter++);

        if (targetSegId != -1 && bestU != -1 && bestV != -1) {
            if (actualConnectionPointIdx != bestU && actualConnectionPointIdx != bestV) {
                removeEdge(bestU, bestV, targetSegId);
                addEdge(bestU, actualConnectionPointIdx, allPoints.get(bestU).distance(allPoints.get(actualConnectionPointIdx)), targetSegId);
                addEdge(actualConnectionPointIdx, bestV, allPoints.get(actualConnectionPointIdx).distance(allPoints.get(bestV)), targetSegId);
            }
        }
    }

    static void processQuery(String[] query) {
        int startNodeIdx = getPointIndex(query[0]);
        int endNodeIdx = getPointIndex(query[1]);
        int kValue = Integer.parseInt(query[2]);

        if (startNodeIdx == -1 || endNodeIdx == -1 || startNodeIdx >= allPoints.size() || endNodeIdx >= allPoints.size()) { 
            System.out.println("NA");
            return;
        }

        List<Path> foundPaths = yenAlgorithm(startNodeIdx, endNodeIdx, kValue); 

        if (foundPaths.isEmpty()) {
            System.out.println("NA");
        } else {
            for (Path p : foundPaths) {
                System.out.printf(Locale.US, "%.5f%n", p.cost); 
                System.out.println(p.formatPathNodes(N_initial, indexToCrossPointName)); 
            }
        }
    }

    static Path dijkstraPath(List<List<Edge>> currentGraph, int start, int target) { 
        int numNodes = currentGraph.size();
        double[] distances = new double[numNodes];
        int[] predecessors = new int[numNodes];
        Arrays.fill(distances, Double.POSITIVE_INFINITY);
        Arrays.fill(predecessors, -1);
        distances[start] = 0;

        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingDouble(e -> e.cost));
        pq.add(new Edge(start, 0));

        while (!pq.isEmpty()) {
            Edge current = pq.poll();

            if (current.cost > distances[current.to] + EPS) {
                continue;
            }

            for (Edge neighbor : currentGraph.get(current.to)) {
                double newDist = distances[current.to] + neighbor.cost;
                if (newDist < distances[neighbor.to] - EPS) {
                    distances[neighbor.to] = newDist;
                    predecessors[neighbor.to] = current.to;
                    pq.add(new Edge(neighbor.to, newDist));
                }
            }
        }

        if (Double.isInfinite(distances[target])) {
            return null;
        }

        List<Integer> pathNodes = new ArrayList<>();
        for (int at = target; at != -1; at = predecessors[at]) {
            pathNodes.add(at);
        }
        Collections.reverse(pathNodes);

        return new Path(distances[target], pathNodes);
    }

    static List<Path> yenAlgorithm(int start, int goal, int K) { 
        List<Path> A_paths = new ArrayList<>();
        PriorityQueue<Path> B_candidates = new PriorityQueue<>();

        Path firstPath = dijkstraPath(graph, start, goal);
        if (firstPath == null) {
            return A_paths;
        }
        A_paths.add(firstPath);

        for (int k = 1; k < K; k++) {
            Path previousPath = A_paths.get(k - 1);

            for (int i = 0; i < previousPath.nodes.size() - 1; i++) {
                int spurNode = previousPath.nodes.get(i);
                List<Integer> rootPath = previousPath.nodes.subList(0, i + 1);

                List<List<Edge>> tempGraph = cloneGraph(graph);

                for (Path p : A_paths) {
                    if (p.nodes.size() > i && rootPath.equals(p.nodes.subList(0, i + 1))) {
                        int u = p.nodes.get(i);
                        int v = p.nodes.get(i + 1);
                        tempGraph.get(u).removeIf(e -> e.to == v);
                    }
                }

                for (int j = 0; j < i; j++) {
                    int blockedNode = rootPath.get(j);
                    tempGraph.get(blockedNode).clear();
                }

                Path spurPath = dijkstraPath(tempGraph, spurNode, goal);

                if (spurPath != null) {
                    List<Integer> totalPathNodes = new ArrayList<>(rootPath);
                    totalPathNodes.addAll(spurPath.nodes.subList(1, spurPath.nodes.size()));

                    if (new HashSet<>(totalPathNodes).size() == totalPathNodes.size()) { 
                        double totalCost = 0;
                        for (int j = 0; j < totalPathNodes.size() - 1; j++) {
                            totalCost += getEdgeCost(totalPathNodes.get(j), totalPathNodes.get(j + 1));
                        }
                        B_candidates.add(new Path(totalCost, totalPathNodes));
                    }
                }
            }

            if (B_candidates.isEmpty()) {
                break;
            }

            Path nextPath = B_candidates.poll();
            A_paths.add(nextPath);
        }
        return A_paths;
    }

    static void printBridges() {
        int numNodes = allPoints.size();
        int[] discoveryTime = new int[numNodes];
        int[] lowLinkValue = new int[numNodes];
        Arrays.fill(discoveryTime, -1);

        List<int[]> bridges = new ArrayList<>();

        int time = 0;
        for (int i = 0; i < numNodes; i++) {
            if (discoveryTime[i] == -1) {
                dfsFindBridges(i, -1, time, discoveryTime, lowLinkValue, bridges);
            }
        }

        bridges.sort((b1, b2) -> { 
            if (b1[0] != b2[0]) return Integer.compare(b1[0], b2[0]);
            return Integer.compare(b1[1], b2[1]);
        });

        for (int[] bridge : bridges) {
            System.out.println(formatOutputNode(bridge[0]) + " " + formatOutputNode(bridge[1]));
        }
    }

    static int dfsFindBridges(int u, int parent, int time, int[] discoveryTime, int[] lowLinkValue, List<int[]> bridges) { 
        discoveryTime[u] = lowLinkValue[u] = ++time;

        for (Edge edge : graph.get(u)) {
            int v = edge.to;

            if (v == parent) {
                continue;
            }

            if (discoveryTime[v] == -1) {
                time = dfsFindBridges(v, u, time, discoveryTime, lowLinkValue, bridges);
                lowLinkValue[u] = Math.min(lowLinkValue[u], lowLinkValue[v]);

                if (lowLinkValue[v] > discoveryTime[u] + EPS) {
                    bridges.add(new int[]{Math.min(u, v), Math.max(u, v)});
                }
            } else {
                lowLinkValue[u] = Math.min(lowLinkValue[u], discoveryTime[v]);
            }
        }
        return time;
    }
}