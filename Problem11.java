import java.util.*;
import java.awt.geom.*; // Point2D class for geometric operations if needed

public class Problem11 {
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

    // スイープラインアルゴリズムで使う線分を表すクラス (汎用)
    static class Segment {
        Point p1, p2;
        int id; // 元の道のID (0からM-1)

        Segment(Point p1, Point p2, int id) {
            this.id = id;
            // 内部的には、常にX座標が小さい方がp1になるように正規化すると、スイープライン処理が楽になる
            if (p1.x > p2.x + EPS || (Math.abs(p1.x - p2.x) < EPS && p1.y > p2.y + EPS)) {
                this.p1 = p2;
                this.p2 = p1;
            } else {
                this.p1 = p1;
                this.p2 = p2;
            }
        }
        
        // 特定のX座標におけるY座標を返す（スイープラインとの交点Y座標）
        public double getYatX(double x_coord) {
            if (Math.abs(p1.x - p2.x) < EPS) { // 垂直線の場合
                // 垂直線は通常Y座標範囲で使われるため、ここでは便宜的にp1のYを返す
                return p1.y; 
            }
            return p1.y + (p2.y - p1.y) * (x_coord - p1.x) / (p2.x - p1.x);
        }
    }

    // スイープラインイベント
    static class Event implements Comparable<Event> {
        double coord; // イベントのX座標
        int type; // 1: 線分の左端点, 0: 交差点, -1: 線分の右端点
        Segment segment1; // イベントに関連する線分
        Segment segment2; // 交差点の場合、もう一つの線分

        Event(double coord, int type, Segment seg1) {
            this.coord = coord;
            this.type = type;
            this.segment1 = seg1;
            this.segment2 = null;
        }

        Event(double coord, int type, Segment seg1, Segment seg2) {
            this.coord = coord;
            this.type = type;
            this.segment1 = seg1;
            this.segment2 = seg2;
        }

        @Override
        public int compareTo(Event o) {
            if (Math.abs(this.coord - o.coord) > EPS) {
                return Double.compare(this.coord, o.coord);
            }
            // typeの順序で処理: 右端点(-1) -> 交差点(0) -> 左端点(1)
            // 右端点を先に処理することで、リストから削除された線分が次の処理で干渉しないようにする
            return Integer.compare(this.type, o.type); 
        }
    }

    // アクティブセグメントリストのComparatorを保持するためのカスタムクラス
    static class SegmentComparator implements Comparator<Segment> {
        private double currentSweepX;

        public void setCurrentSweepX(double x) {
            this.currentSweepX = x;
        }

        @Override
        public int compare(Segment s1, Segment s2) {
            double y1 = s1.getYatX(currentSweepX);
            double y2 = s2.getYatX(currentSweepX);
            if (Math.abs(y1 - y2) > EPS) {
                return Double.compare(y1, y2);
            }
            // Y座標が同じ場合は、傾きなどで一貫した順序を決定する（重要）
            // ここでは簡易的にidで順序付けするが、本来は交差判定に影響しない安定した順序付けが必要
            return Integer.compare(s1.id, s2.id);
        }
    }

    /* ================= メインメソッド ================= */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // N:地点数, M:道の数, P:追加地点の数, Q:経路問い合わせの数
        int N_in, M_in, P_in, Q_in;
        N_in = sc.nextInt();
        M_in = sc.nextInt();
        P_in = sc.nextInt();
        Q_in = sc.nextInt();
        N_initial = N_in; // 初期地点数を保存
        P_in_global = P_in; // Pの値をグローバル変数に保持

        // 既存地点の読み込み
        for (int i = 0; i < N_in; i++) {
            Point p = new Point(sc.nextDouble(), sc.nextDouble());
            addPointToGlobalList(p);
        }

        // 既存の道の読み込み
        List<int[]> initialRoadsRaw = new ArrayList<>();
        List<Segment> allSegments = new ArrayList<>(); // 全ての線分オブジェクトを保持

        for (int i = 0; i < M_in; i++) {
            int u_idx = sc.nextInt() - 1; // 0-indexedに変換
            int v_idx = sc.nextInt() - 1; // 0-indexedに変換
            
            // 範囲チェックを強化: N_initial (初期地点の数) の範囲内にあることを確認
            // M個の道は、初期に与えられたN個の地点を繋ぐものなので、インデックスはN_initialまで
            if (u_idx < 0 || u_idx >= N_initial || v_idx < 0 || v_idx >= N_initial) {
                System.err.println("Error: Road points out of initial N bounds. Skipping road " + (i + 1));
                continue; // 無効な道はスキップ
            }

            initialRoadsRaw.add(new int[]{u_idx, v_idx});
            allSegments.add(new Segment(allPoints.get(u_idx), allPoints.get(v_idx), i)); // Segmentオブジェクトを作成
        }
        roadSegmentCounter = M_in; // 既存の道にM_inまでのIDを割り当てる

        // グラフの初期構築 (交差点の検出と辺の追加)
        buildInitialGraph(initialRoadsRaw, allSegments);

        // 追加地点の読み込みと処理 (小課題7)
        List<Point> newPointsToAdd = new ArrayList<>();
        for (int i = 0; i < P_in; i++) {
            newPointsToAdd.add(new Point(sc.nextDouble(), sc.nextDouble()));
        }
        for (Point newP : newPointsToAdd) {
            // 新しい地点をまずリストに追加し、インデックスを決定
            int newPointIdx = addPointToGlobalList(newP);
            connectNewPointToNetwork(newPointIdx);
        }
        
        // クエリの処理ループ: Qの回数ではなく、コマンドを識別して処理
        for (int qCount = 0; qCount < Q_in; qCount++) { // Q個のクエリが与えられる
            if (!sc.hasNext()) { // 入力が終了したかチェック
                break;
            }
            String command = sc.next();

            if (command.equals("distance")) {
                String s = sc.next();
                String d = sc.next();
                if (!sc.hasNextInt()) {
                    System.err.println("Error: Expected integer for 'k' after distance query for " + s + " " + d);
                    if (sc.hasNextLine()) {
                        sc.nextLine(); // 残りの行を読み飛ばす
                    }
                    continue;
                }
                int k = sc.nextInt();
                processQuery(new String[]{s, d, String.valueOf(k)});
            } else if (command.equals("detect_bridges")) { // 小課題8の機能
                printBridges();
            } else {
                // 未知のコマンド、またはデータ形式の不一致
                System.err.println("Unknown command or malformed input: " + command);
                // 例外を避けるため、残りの行を読み飛ばす
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
        if (s.startsWith("C")) { // 交差点の形式
            return crossPointNameToIndex.getOrDefault(s, -1);
        } else if (s.startsWith("P")) { // P地点の処理
            try {
                int pNum = Integer.parseInt(s.substring(1));
                int idx = N_initial + (pNum - 1);
                // P地点のインデックスが allPoints の範囲内であることを確認
                return (idx >= N_initial && idx < allPoints.size()) ? idx : -1;
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        try {
            int idx = Integer.parseInt(s) - 1;
            // 元の地点IDのチェック条件を allPoints.size() に変更
            // これにより、初期地点だけでなく、交差点やP地点を含めた全ノード数に対応
            return (idx >= 0 && idx < allPoints.size()) ? idx : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static String formatOutputNode(int idx) {
        if (idx < N_initial) { // 元の地点
            return String.valueOf(idx + 1);
        } else if (idx >= N_initial && idx < N_initial + P_in_global) { // P地点をP1, P2...と表示
            return "P" + (idx - N_initial + 1); 
        }
        // 交差点
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
        return Double.POSITIVE_INFINITY; // エッジが存在しない場合
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

    /* ================= 補助関数: 幾何計算 (汎用線分交差用) ================= */

    // 2つの線分 p1-q1 と p2-q2 の交点を求める (端点での接触は交差点とみなさない)
    static Point getIntersection(Point p1, Point q1, Point p2, Point q2) {
        // Line-Line Intersection using Cramer's Rule
        double A1 = q1.y - p1.y;
        double B1 = p1.x - q1.x;
        double C1 = A1 * p1.x + B1 * p1.y;

        double A2 = q2.y - p2.y;
        double B2 = p2.x - q2.x;
        double C2 = A2 * p2.x + B2 * p2.y;

        double det = A1 * B2 - A2 * B1; // 行列式 (平行なら0)

        if (Math.abs(det) < EPS) { // 行列式が0の場合解がない
            return null; // 平行または共線
        }

        double x = (B2 * C1 - B1 * C2) / det;
        double y = (A1 * C2 - A2 * C1) / det;

        // tとuが(0, 1)の範囲内にあるか（端点を含まない真の交差）
        // 小課題1の要件「ある道が別の道の端点のみで接する場合、その地点は交差地点とはみなさない」に合わせる
        if (onSegmentStrict(p1, q1, new Point(x,y)) && onSegmentStrict(p2, q2, new Point(x,y))) {
            return new Point(x, y);
        }
        return null;
    }

    // 点が線分上に厳密に存在する（端点を含まない）かチェック
    static boolean onSegmentStrict(Point a, Point b, Point p) {
        // 共線条件
        double crossProduct = (p.y - a.y) * (b.x - a.x) - (p.x - a.x) * (b.y - a.y);
        if (Math.abs(crossProduct) > EPS) {
            return false;
        }

        // x, y座標が線分の範囲内にあるか (端点を含まない)
        boolean x_in_range = (p.x > Math.min(a.x, b.x) + EPS && p.x < Math.max(a.x, b.x) - EPS);
        boolean y_in_range = (p.y > Math.min(a.y, b.y) + EPS && p.y < Math.max(a.y, b.y) - EPS);

        // 垂直線分または水平線分の場合、片方の座標のみで範囲チェック
        if (Math.abs(a.x - b.x) < EPS) { // 垂直線分
            return y_in_range;
        }
        if (Math.abs(a.y - b.y) < EPS) { // 水平線分
            return x_in_range;
        }

        return x_in_range && y_in_range;
    }

    // 点が線分上に存在するかどうか（端点を含む）
    static boolean onSegment(Point a, Point b, Point p) {
        // 外積が0に近いことを確認（共線条件）
        double crossProduct = (p.y - a.y) * (b.x - a.x) - (p.x - a.x) * (b.y - a.y);
        if (Math.abs(crossProduct) > EPS) {
            return false;
        }

        // 内積が0以上で、かつ終点までの内積が線分自身の長さの二乗以下であることを確認（線分内にあるか）
        double dotProduct = (p.x - a.x) * (b.x - a.x) + (p.y - a.y) * (b.y - a.y);
        double squaredLength = (b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y);
        return dotProduct >= -EPS && dotProduct <= squaredLength + EPS;
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
        
        t = Math.max(0, Math.min(1, t)); // tを[0, 1]にクランプ

        Point projectedPoint = new Point(a.x + t * dx, a.y + t * dy);
        return new double[]{t, p.distance(projectedPoint)};
    }

    /* ================= 小課題1&2 (Bentley-Ottmann): 交差点検出とグラフ構築 ================= */
    static void buildInitialGraph(List<int[]> initialRoadsRaw, List<Segment> allSegments) {
        List<Point> newIntersections = new ArrayList<>();
        Set<String> addedCrossPointKeys = new HashSet<>();

        // 1. イベントキューの作成と初期化
        PriorityQueue<Event> eventQueue = new PriorityQueue<>();
        for (Segment seg : allSegments) {
            eventQueue.add(new Event(seg.p1.x, 1, seg)); // 左端点イベント
            eventQueue.add(new Event(seg.p2.x, -1, seg)); // 右端点イベント
        }

        // 2. アクティブセグメントリスト (スイープライン上の線分をY座標順に保持)
        SegmentComparator segmentComparator = new SegmentComparator();
        TreeMap<Segment, Void> activeSegments = new TreeMap<>(segmentComparator);

        while (!eventQueue.isEmpty()) {
            Event event = eventQueue.poll();
            segmentComparator.setCurrentSweepX(event.coord); // スイープラインXを更新

            if (event.type == 1) { // 左端点イベント
                // アクティブリストに追加
                activeSegments.put(event.segment1, null);
                // 隣接する線分を取得
                Segment higher = activeSegments.higherKey(event.segment1);
                Segment lower = activeSegments.lowerKey(event.segment1);
                
                // 隣接する線分との交差をチェックし、交差点が見つかればイベントキューに追加
                if (higher != null) {
                    Point intersection = getIntersection(event.segment1.p1, event.segment1.p2, higher.p1, higher.p2);
                    if (intersection != null) {
                        eventQueue.add(new Event(intersection.x, 0, event.segment1, higher));
                    }
                }
                if (lower != null) {
                    Point intersection = getIntersection(event.segment1.p1, event.segment1.p2, lower.p1, lower.p2);
                    if (intersection != null) {
                        eventQueue.add(new Event(intersection.x, 0, event.segment1, lower));
                    }
                }
            } else if (event.type == -1) { // 右端点イベント
                // 削除前に隣接する線分を取得
                Segment higher = activeSegments.higherKey(event.segment1);
                Segment lower = activeSegments.lowerKey(event.segment1);
                activeSegments.remove(event.segment1); // リストから削除

                // 削除された線分の上下にあった線分同士の交差をチェック
                if (higher != null && lower != null) {
                    Point intersection = getIntersection(higher.p1, higher.p2, lower.p1, lower.p2);
                    if (intersection != null) {
                        // 交差点がすでにイベントキューにない場合のみ追加 (重複判定を避ける)
                        // ここでは簡易的に実装。Bentley-Ottmannでは重複する交差イベントを避けるためにさらに工夫が必要
                        eventQueue.add(new Event(intersection.x, 0, higher, lower));
                    }
                }
            } else { // 交差点イベント
                // 交差点を結果に追加
                Point intersectionPoint = getIntersection(event.segment1.p1, event.segment1.p2, event.segment2.p1, event.segment2.p2);
                if (intersectionPoint != null) { // 再度チェックしてnullでないことを確認
                    String key = String.format(Locale.US, "%.5f_%.5f", intersectionPoint.x, intersectionPoint.y);
                    if (!addedCrossPointKeys.contains(key) && !pointToIndexMap.containsKey(intersectionPoint)) {
                        newIntersections.add(intersectionPoint);
                        addedCrossPointKeys.add(key);
                    }
                }
                
                // 交差した線分の順序をアクティブセグメントリスト内で入れ替える
                // TreeMapではremove/putをすることでComparatorが再評価され、自動的に順序が入れ替わる
                activeSegments.remove(event.segment1);
                activeSegments.remove(event.segment2);
                activeSegments.put(event.segment1, null);
                activeSegments.put(event.segment2, null);

                // 新しい隣接ペアとの交差をチェック
                // event.segment1の上下の線分
                Segment higher1 = activeSegments.higherKey(event.segment1);
                Segment lower1 = activeSegments.lowerKey(event.segment1);
                if (higher1 != null) {
                    Point intersection = getIntersection(event.segment1.p1, event.segment1.p2, higher1.p1, higher1.p2);
                    if (intersection != null) eventQueue.add(new Event(intersection.x, 0, event.segment1, higher1));
                }
                if (lower1 != null) {
                    Point intersection = getIntersection(event.segment1.p1, event.segment1.p2, lower1.p1, lower1.p2);
                    if (intersection != null) eventQueue.add(new Event(intersection.x, 0, event.segment1, lower1));
                }
                // event.segment2の上下の線分
                Segment higher2 = activeSegments.higherKey(event.segment2);
                Segment lower2 = activeSegments.lowerKey(event.segment2);
                if (higher2 != null) {
                    Point intersection = getIntersection(event.segment2.p1, event.segment2.p2, higher2.p1, higher2.p2);
                    if (intersection != null) eventQueue.add(new Event(intersection.x, 0, event.segment2, higher2));
                }
                if (lower2 != null) {
                    Point intersection = getIntersection(event.segment2.p1, event.segment2.p2, lower2.p1, lower2.p2);
                    if (intersection != null) eventQueue.add(new Event(intersection.x, 0, event.segment2, lower2));
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

        // グラフ隣接リストの構築
        // 各既存の道を、その道上の地点（既存地点、新しく追加された交差点）で分割し、辺を追加
        for (Segment currentSeg : allSegments) {
            Point startPoint = currentSeg.p1;
            Point endPoint = currentSeg.p2;

            List<Integer> pointsOnRoad = new ArrayList<>();
            // Segmentクラスの正規化でp1, p2が入れ替わっている可能性があるので、
            // pointToIndexMapから取得することで、正しいインデックスを取得する
            pointsOnRoad.add(pointToIndexMap.get(startPoint));
            pointsOnRoad.add(pointToIndexMap.get(endPoint));

            // この道上にある全ての交差点および既存の地点を追加
            for (int i = 0; i < allPoints.size(); i++) {
                // onSegmentは端点を含む判定
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
                addEdge(u, v, dist, currentSeg.id); // 元の道のIDを辺に付与
            }
        }
    }

    /* ================= 小課題7: 最適な道の建設提案 ================= */
    static void connectNewPointToNetwork(int newPointIdx) {
        Point newPoint = allPoints.get(newPointIdx);
        double minDistance = Double.POSITIVE_INFINITY;
        Point connectionPoint = null;
        int targetSegId = -1; 
        int bestU = -1, bestV = -1; 

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
        // 小課題7のロジックは既存のProblem9.javaのものを維持
        for (int u = 0; u < graph.size(); u++) {
            for (Edge edge : graph.get(u)) {
                int v = edge.to;
                if (u >= v) continue;

                Point p1 = allPoints.get(u);
                Point p2 = allPoints.get(v);

                double[] projResult = projectPointToSegmentAndGetDistance(p1, p2, newPoint); // 新点から線分への射影点を計算
                double t_param = projResult[0];
                double distToSegment = projResult[1];

                if (distToSegment < minDistance - EPS) {
                    minDistance = distToSegment;
                    connectionPoint = new Point(p1.x + t_param * (p2.x - p1.x), p1.y + t_param * (p2.y - p1.y));
                    targetSegId = edge.segId;
                    bestU = u;
                    bestV = v;
                } else if (Math.abs(distToSegment - minDistance) < EPS) {
                    // 同距離の場合の優先順位付け: 小課題7の「より先にある道の方につなぐ」を解釈
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

        System.out.printf(Locale.US, "%.5f %.5f%n", connectionPoint.x, connectionPoint.y); // 接続点が出力される

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

        if (startNodeIdx == -1 || endNodeIdx == -1 || startNodeIdx >= allPoints.size() || endNodeIdx >= allPoints.size()) { // 不正な入力または到達不能な場合
            System.out.println("NA");
            return;
        }

        List<Path> foundPaths = yenAlgorithm(startNodeIdx, endNodeIdx, kValue); // K番目の最短経路を見つける (Yen's Algorithm)

        if (foundPaths.isEmpty()) {
            System.out.println("NA");
        } else {
            for (Path p : foundPaths) {
                System.out.printf(Locale.US, "%.5f%n", p.cost); // 距離の出力
                System.out.println(p.formatPathNodes(N_initial, indexToCrossPointName)); // 経路の出力
            }
        }
    }

    static Path dijkstraPath(List<List<Edge>> currentGraph, int start, int target) { // Dijkstra's Algorithm (単一始点最短経路)
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

    static List<Path> yenAlgorithm(int start, int goal, int K) { // Yen's Algorithm (K番目の最短経路)
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

                    if (new HashSet<>(totalPathNodes).size() == totalPathNodes.size()) { // サイクルを含む経路は除外（問題の要件「無駄な回り道を含む経路はk番目の最短路とはみなさない」）
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

    /* ================= 小課題8: 幹線道路（橋）検出 ================= */
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

        bridges.sort((b1, b2) -> { // 橋をソートして出力（端点のインデックスの小さい順、次に大きい方のインデックスの小さい順）
            if (b1[0] != b2[0]) return Integer.compare(b1[0], b2[0]);
            return Integer.compare(b1[1], b2[1]);
        });

        for (int[] bridge : bridges) {
            System.out.println(formatOutputNode(bridge[0]) + " " + formatOutputNode(bridge[1]));
        }
    }

    static int dfsFindBridges(int u, int parent, int time, int[] discoveryTime, int[] lowLinkValue, List<int[]> bridges) { // 橋を見つけるためのDFS
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