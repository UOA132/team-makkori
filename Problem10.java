import java.util.*;
import java.awt.geom.*;
import java.io.File; // File操作のために必要
import java.io.FileInputStream; // FileInputStreamのために必要
import java.io.FileNotFoundException; // FileNotFoundExceptionのために必要

public class Problem10 {
    /* ----------------- 定数 ----------------- */
    static final double EPS = 1e-8; // 浮動小数点比較のための許容誤差

    /* ----------------- 基本クラス ----------------- */
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
            // 座標値が非常に大きくなるため、ハッシュコードの精度に注意
            // EPSを考慮して丸めることで、浮動小数点誤差による異なるハッシュを回避
            return Objects.hash(Math.round(x / EPS), Math.round(y / EPS)); 
        }
    }

    static class Edge {
        int to;
        double cost;
        int segId; // どの線分から派生したか

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
                if (idx < N_original) { // 元の地点
                    res.add(String.valueOf(idx + 1));
                } else { // 交差点または追加地点
                    res.add(reverseIndex.getOrDefault(idx, String.valueOf(idx + 1)));
                }
            }
            return String.join(" ", res);
        }
    }

    /* ----------------- グローバル変数 ----------------- */
    static int N_initial; // 初期地点の数
    static List<Point> allPoints = new ArrayList<>(); // すべての地点（初期地点、交差点、追加地点）
    static List<List<Edge>> graph = new ArrayList<>();
    static Map<String, Integer> crossPointNameToIndex = new LinkedHashMap<>(); // 交差点名（C1, C2...）からインデックスへ
    static Map<Integer, String> indexToCrossPointName = new HashMap<>(); // インデックスから交差点名へ
    static Map<Point, Integer> pointToIndexMap = new HashMap<>(); // Pointオブジェクトからそのインデックスへ
    static int roadSegmentCounter; // 道のセグメントIDを生成するためのカウンター
    static int P_in_global; // mainメソッドで読み込んだPの値を保持

    // スイープラインアルゴリズムで使う線分を表すクラス (水平/垂直を区別)
    static class Segment {
        Point p1, p2;
        int id; // 元の道のID
        boolean isHorizontal;

        Segment(Point p1, Point p2, int id) {
            this.id = id;
            // 浮動小数点誤差を考慮して水平/垂直を判定
            if (Math.abs(p1.y - p2.y) < EPS) { // 水平
                this.isHorizontal = true;
                this.p1 = p1.x < p2.x ? p1 : p2; // p1を左端に
                this.p2 = p1.x < p2.x ? p2 : p1;
            } else if (Math.abs(p1.x - p2.x) < EPS) { // 垂直
                this.isHorizontal = false;
                this.p1 = p1.y < p2.y ? p1 : p2; // p1を下端に
                this.p2 = p1.y < p2.y ? p2 : p1;
            } else {
                // 小課題10では発生しないはず
                throw new IllegalArgumentException("Segment is not horizontal or vertical.");
            }
        }
    }

    // スイープラインイベント
    static class Event implements Comparable<Event> {
        double coord; // イベントの発生座標 (X座標)
        int type; // 1: 垂直線開始, 0: 水平線, -1: 垂直線終了
        Segment segment;

        Event(double coord, int type, Segment segment) {
            this.coord = coord;
            this.type = type;
            this.segment = segment;
        }

        @Override
        public int compareTo(Event o) {
            if (Math.abs(this.coord - o.coord) > EPS) {
                return Double.compare(this.coord, o.coord);
            }
            return Integer.compare(this.type, o.type); // typeの順序で処理: 開始->水平->終了
        }
    }


    /* ================= メインメソッド ================= */
    public static void main(String[] args) {
        Scanner sc = null;
        try {
            // ★ ここで読み込むファイルを直接指定 ★
            // テストしたいファイルパスに修正してください
            File inputFile = new File("generated_test_data/input_10_1_1.txt");
            // File inputFile = new File("generated_test_data/input_10_2_1.txt"); // 別ファイルの場合

            sc = new Scanner(new FileInputStream(inputFile));
            // コメント行 (#で始まる行) をスキップする正規表現を区切り文字に追加
            // \s+ は1つ以上の空白文字
            // |#.* は # から行の終わりまですべてを区切り文字にする (コメントを無視する)
            // \R は改行文字のいずれかを意味する
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

        List<int[]> initialRoadsRaw = new ArrayList<>();
        for (int i = 0; i < M_in; i++) {
            initialRoadsRaw.add(new int[]{sc.nextInt() - 1, sc.nextInt() - 1});
        }
        roadSegmentCounter = M_in;

        buildInitialGraph(initialRoadsRaw); // 修正されたグラフ構築

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
                    if (sc.hasNextLine()) {
                        sc.nextLine();
                    }
                    continue;
                }
                int k = sc.nextInt();
                processQuery(new String[]{s, d, String.valueOf(k)});
            } else if (command.equals("detect_bridges")) {
                printBridges();
            } else {
                System.err.println("Unknown command or malformed input: " + command);
                if (sc.hasNextLine()) {
                    sc.nextLine();
                }
            }
        }
        sc.close();
    }

    /* ================= 補助関数: 点の管理 ================= */
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

    /* ================= 補助関数: グラフ操作 ================= */
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

    /* ================= 補助関数: 幾何計算 ================= */
    static boolean onSegment(Point a, Point b, Point p) {
        double crossProduct = (p.y - a.y) * (b.x - a.x) - (p.x - a.x) * (b.y - a.y);
        if (Math.abs(crossProduct) > EPS) {
            return false;
        }
        double dotProduct = (p.x - a.x) * (b.x - a.x) + (p.y - a.y) * (b.y - a.y);
        double squaredLength = (b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y);
        return dotProduct >= -EPS && dotProduct <= squaredLength + EPS;
    }

    // 水平/垂直線分の交差判定 (専用)
    static Point getIntersectionHV(Point p1, Point q1, Point p2, Point q2) {
        // 線分1 (p1-q1), 線分2 (p2-q2)
        // 必ず片方が水平、もう片方が垂直であることを想定

        boolean isSeg1Horizontal = Math.abs(p1.y - q1.y) < EPS;
        boolean isSeg2Horizontal = Math.abs(p2.y - q2.y) < EPS;

        if (isSeg1Horizontal == isSeg2Horizontal) {
            // 両方水平または両方垂直 - 交差は通常ない (重なる場合は別途考慮が必要だが、小課題1の要件から端点のみの接触は除外)
            return null;
        }

        Point h1, h2, v1, v2; // 水平線分の端点, 垂直線分の端点

        if (isSeg1Horizontal) {
            h1 = p1.x < q1.x ? p1 : q1; // h1は左端
            h2 = p1.x < q1.x ? q1 : p1; // h2は右端
            v1 = p2.y < q2.y ? p2 : q2; // v1は下端
            v2 = p2.y < q2.y ? q2 : p2; // v2は上端
        } else { // isSeg2Horizontal
            h1 = p2.x < q2.x ? p2 : q2;
            h2 = p2.x < q2.x ? q2 : p2;
            v1 = p1.y < q1.y ? p1 : q1;
            v2 = p1.y < q1.y ? q1 : p1;
        }

        // 交差条件: 水平線のY座標が垂直線のY範囲内、かつ垂直線のX座標が水平線のX範囲内
        if (v1.x > h1.x - EPS && v1.x < h2.x + EPS && // 垂直線のXが水平線のX範囲内 (端点を含まない)
            h1.y > v1.y - EPS && h1.y < v2.y + EPS) { // 水平線のYが垂直線のY範囲内 (端点を含まない)
            
            Point intersection = new Point(v1.x, h1.y);

            // 小課題1の要件「ある道が別の道の端点のみで接する場合、その地点は交差地点とはみなさない」
            // 交差点がどちらかの線分の端点である場合はnullを返す
            if ((Math.abs(intersection.x - p1.x) < EPS && Math.abs(intersection.y - p1.y) < EPS) ||
                (Math.abs(intersection.x - q1.x) < EPS && Math.abs(intersection.y - q1.y) < EPS) ||
                (Math.abs(intersection.x - p2.x) < EPS && Math.abs(intersection.y - p2.y) < EPS) ||
                (Math.abs(intersection.x - q2.x) < EPS && Math.abs(intersection.y - q2.y) < EPS)) {
                return null;
            }
            return intersection;
        }
        return null;
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

    /* ================= 小課題1&2 (改良版): 交差点検出とグラフ構築 ================= */
    static void buildInitialGraph(List<int[]> initialRoadsRaw) {
        // 1. 線分オブジェクトの作成と水平/垂直の分類
        List<Segment> horizontalSegments = new ArrayList<>();
        List<Segment> verticalSegments = new ArrayList<>();
        List<Segment> allSegments = new ArrayList<>(); // 全ての線分を保持

        for (int i = 0; i < initialRoadsRaw.size(); i++) {
            int[] road = initialRoadsRaw.get(i);
            Point p1 = allPoints.get(road[0]);
            Point p2 = allPoints.get(road[1]);
            Segment seg = new Segment(p1, p2, i); // 元の道のIDを保持
            allSegments.add(seg);

            if (seg.isHorizontal) {
                horizontalSegments.add(seg);
            } else {
                verticalSegments.add(seg);
            }
        }

        // 2. スイープラインアルゴリズムによる交差点の検出
        List<Point> newIntersections = new ArrayList<>();
        Set<String> addedCrossPointKeys = new HashSet<>();

        List<Event> events = new ArrayList<>();
        // 垂直線分の開始/終了イベント (x座標)
        for (Segment vSeg : verticalSegments) {
            events.add(new Event(vSeg.p1.x, 1, vSeg)); // 垂直線開始 (下端のx座標)
            events.add(new Event(vSeg.p1.x, -1, vSeg)); // 垂直線終了 (上端のx座標)
        }
        // 水平線分のイベント (x座標の左右端)
        for (Segment hSeg : horizontalSegments) {
            events.add(new Event(hSeg.p1.x, 0, hSeg)); // 水平線開始 (左端のx座標)
            events.add(new Event(hSeg.p2.x, 0, hSeg)); // 水平線終了 (右端のx座標)
        }
        Collections.sort(events);

        // アクティブな水平線分をY座標で管理するデータ構造
        // TreapやSegment Treeが理想的だが、ここではTreeMapで簡易的に実装
        // Y座標 -> そのY座標にある水平線分のリスト
        TreeMap<Double, List<Segment>> activeHorizontalSegments = new TreeMap<>(); 

        for (Event event : events) {
            if (event.segment.isHorizontal) {
                // 水平線分の開始/終了
                if (event.coord == event.segment.p1.x) { // 開始
                    activeHorizontalSegments.computeIfAbsent(event.segment.p1.y, k -> new ArrayList<>()).add(event.segment);
                } else { // 終了
                    activeHorizontalSegments.get(event.segment.p1.y).remove(event.segment);
                    if (activeHorizontalSegments.get(event.segment.p1.y).isEmpty()) {
                        activeHorizontalSegments.remove(event.segment.p1.y);
                    }
                }
            } else { // 垂直線分のイベント
                // 垂直線分と交差する水平線分を探す
                // 垂直線分はx座標がevent.coord
                // 垂直線分のY範囲はevent.segment.p1.y から event.segment.p2.y (p1は下端)

                if (event.type == 1) { // 垂直線開始 (走査線が垂直線の左端に達した)
                    // 垂直線が交差するY範囲内の水平線分を検索
                    for (Map.Entry<Double, List<Segment>> entry : activeHorizontalSegments.subMap(event.segment.p1.y - EPS, true, event.segment.p2.y + EPS, true).entrySet()) {
                        for (Segment hSeg : entry.getValue()) {
                            Point intersection = getIntersectionHV(hSeg.p1, hSeg.p2, event.segment.p1, event.segment.p2);
                            if (intersection != null) {
                                String key = String.format(Locale.US, "%.5f_%.5f", intersection.x, intersection.y);
                                if (!addedCrossPointKeys.contains(key) && !pointToIndexMap.containsKey(intersection)) {
                                    newIntersections.add(intersection);
                                    addedCrossPointKeys.add(key);
                                }
                            }
                        }
                    }
                }
            }
        }

        // 検出された交点をソートし、C1, C2... のように命名してリストに追加
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

        // 3. グラフ隣接リストの構築
        // 各既存の道を、その道上の地点（既存地点、新しく追加された交差点）で分割し、辺を追加
        for (int segId = 0; segId < allSegments.size(); segId++) {
            Segment currentSeg = allSegments.get(segId);
            Point startPoint = currentSeg.p1;
            Point endPoint = currentSeg.p2;

            List<Integer> pointsOnRoad = new ArrayList<>();
            pointsOnRoad.add(pointToIndexMap.get(startPoint)); // 道の始点
            pointsOnRoad.add(pointToIndexMap.get(endPoint)); // 道の終点

            // この道上にある全ての交差点および既存の地点を追加
            for (int i = 0; i < allPoints.size(); i++) {
                // allPointsには元の地点、交差点、追加地点が含まれる。
                // ただし、ここで追加したいのは元々の道（segId）上にある、既存の地点でも交差点でもない地点。
                // 実際には交差点も考慮すべきなので、allPointsを走査
                if (i != pointToIndexMap.get(startPoint) && i != pointToIndexMap.get(endPoint) && 
                    onSegment(startPoint, endPoint, allPoints.get(i))) {
                    pointsOnRoad.add(i);
                }
            }

            // 道上の点を順序付けてソート（線分の始点からの投影パラメータで）
            pointsOnRoad.sort(Comparator.comparingDouble(idx ->
                    getProjectionParameter(startPoint, endPoint, allPoints.get(idx))));

            // 隣接する点間に辺を追加
            for (int i = 0; i < pointsOnRoad.size() - 1; i++) {
                int u = pointsOnRoad.get(i);
                int v = pointsOnRoad.get(i + 1);
                double dist = allPoints.get(u).distance(allPoints.get(v));
                addEdge(u, v, dist, segId);
            }
        }
    }

    /* ================= 小課題7: 最適な道の建設提案 ================= */
    static void connectNewPointToNetwork(int newPointIdx) {
        Point newPoint = allPoints.get(newPointIdx);
        double minDistance = Double.POSITIVE_INFINITY;
        Point connectionPoint = null;
        int targetSegId = -1; // 接続先の道路セグメントのID (幹線道路検出用)
        int bestU = -1, bestV = -1; // 接続する線分の両端点

        // 既存のすべての点との距離をチェック
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

        // 既存のすべての道路セグメントとの最短距離をチェック
        // Note: graphに含まれる辺は、すでに分割された微小なセグメントであることに注意。
        // 元のroadsRawから線分を再構築してチェックするのがより正確。
        List<Segment> currentSegments = new ArrayList<>();
        for(int i = 0; i < roadSegmentCounter; i++) { // roadSegmentCounterは現在の全線分数
            // ここでの Segment の再構築は、元の roadsRaw からではなく、グラフの辺から行われる必要があるかもしれない
            // または、buildInitialGraph で生成した allSegments を保持して利用する
            // 簡略化のため、ここではグラフの辺を走査する
            // ただし、グラフの辺は既に交差点で分割されているため、元の線分として扱うには情報が足りない場合がある
            // 小課題7のロジックを正確に再現するためには、元の線分リストが必要。
            // ここではProblem9.javaのロジックを維持し、グラフの辺（分割されたセグメント）を走査する。
            // 問題文の「P個の地点を道路網につなげる」という記述を、既存の「線分」または「地点」につなげると解釈。
            // ここは既存のProblem9.javaのロジックをそのまま引き継ぐ。
        }

        // Problem9.java の connectNewPointToNetwork ロジックをそのまま利用
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
                    if (targetSegId == -1 || edge.segId < targetSegId) { // 小さいsegId優先
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

    /* ================= 小課題3, 4, 5, 6: 経路クエリ処理 ================= */
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