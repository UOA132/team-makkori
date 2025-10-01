import java.util.*;


public class Problem8 {

    /* ----------------- 定数 ----------------- */
    static final double EPS = 1e-8;

    /* ----------------- 基本クラス ----------------- */
    static class Point {
        double x, y;
        Point(double x, double y){ this.x = x; this.y = y; }
        double dist(Point p){ return Math.hypot(x - p.x, y - p.y); }
    }
    static class Edge {
        int to; double cost; int segId;
        Edge(int t,double c,int s){ this.to=t; this.cost=c; this.segId=s; }
        Edge(int t,double c){ this(t,c,-1); }
    }
    static class Path implements Comparable<Path>{
        double cost; List<Integer> nodes;
        Path(double c,List<Integer> ns){ cost=c; nodes=new ArrayList<>(ns); }
        public int compareTo(Path o){ return Double.compare(cost,o.cost); }
        // ← 修正済み
        public String toString(){
            return String.format(Locale.US,"Cost: %.5f%nPath: %s%n",
                    cost,String.join(" ",formatPath(nodes)));
        }
    }

    /* ----------------- グローバル ----------------- */
    static int N;                               // 元の地点数
    static final List<Point> points = new ArrayList<>();
    static final List<List<Edge>> graph = new ArrayList<>();
    static final Map<String,Integer>  crossIndex   = new HashMap<>();
    static final Map<Integer,String>  reverseIndex = new HashMap<>();
    static int segCounter;                      // 追加道用 segId

    /* ================= メイン ================= */
    public static void main(String[] args){
        Locale.setDefault(Locale.US);
        Scanner sc = new Scanner(System.in);

        /* --- 入力 --- */
        N = sc.nextInt();
        int M = sc.nextInt(), P = sc.nextInt(), Q = sc.nextInt();
        segCounter = M;

        for(int i=0;i<N;i++)
            points.add(new Point(sc.nextDouble(),sc.nextDouble()));

        List<int[]> rawSeg = new ArrayList<>();
        for(int i=0;i<M;i++)
            rawSeg.add(new int[]{sc.nextInt()-1, sc.nextInt()-1});

        List<Point> addPts = new ArrayList<>();
        for(int i=0;i<P;i++)
            addPts.add(new Point(sc.nextDouble(), sc.nextDouble()));

        List<String[]> queries = new ArrayList<>();
        for(int i=0;i<Q;i++)
            queries.add(new String[]{sc.next(), sc.next(), String.valueOf(sc.nextInt())});

        /* --- グラフ構築 --- */
        buildInitialGraph(rawSeg);

        /* --- 追加地点処理 (小課題7) --- */
        for(Point np : addPts){
            int idx = points.size();
            points.add(np); graph.add(new ArrayList<>());
            connectNewPoint(idx);      // 座標出力もここで
        }

        /* --- 経路クエリ (小課題5‑6) --- */
        for(String[] q : queries) processQuery(q);

        /* --- 幹線道路（橋）を常に出力 (小課題8) --- */
        printBridges();
    }

    /* =============================================================
       1. 交差点検出＋初期グラフ構築
       ============================================================= */
    static void buildInitialGraph(List<int[]> segs){

        /* 1‑A 交差点検出 */
        for(int i=0;i<segs.size();i++){
            for(int j=i+1;j<segs.size();j++){
                int[] s1=segs.get(i), s2=segs.get(j);
                Point a=points.get(s1[0]), b=points.get(s1[1]);
                Point c=points.get(s2[0]), d=points.get(s2[1]);
                Point p=getIntersection(a,b,c,d);
                if(p==null) continue;

                if(findExistingPoint(p.x,p.y)==-1){ // 新しい真の交点
                    String cname="C"+(crossIndex.size()+1);
                    int idx=points.size();
                    crossIndex.put(cname,idx);
                    reverseIndex.put(idx,cname);
                    points.add(p);
                }
            }
        }

        /* 1‑B グラフ隣接リスト用意 */
        for(int i=0;i<points.size();i++) graph.add(new ArrayList<>());

        /* 1‑C 各線分を端点＋“すべての中間点”で分割 */
        for(int segId=0; segId<segs.size(); segId++){
            int[] s=segs.get(segId);
            Point a=points.get(s[0]), b=points.get(s[1]);

            List<Integer> list=new ArrayList<>();
            list.add(s[0]); list.add(s[1]);

            // ★ 全頂点を走査 ← 修正点
            for(int idx=0; idx<points.size(); idx++){
                if(idx!=s[0] && idx!=s[1] && onSegment(a,b,points.get(idx)))
                    list.add(idx);
            }
            list.sort(Comparator.comparingDouble(
                    i->projection(a,b,points.get(i))));

            for(int i=0;i<list.size()-1;i++){
                int u=list.get(i), v=list.get(i+1);
                double d=points.get(u).dist(points.get(v));
                addEdge(u,v,d,segId);
            }
        }
    }

    /* =============================================================
       2. 追加地点を最短で接続 (小課題7)
       ============================================================= */
    static void connectNewPoint(int newIdx){
        Point np=points.get(newIdx);
        double best=Double.POSITIVE_INFINITY, bestT=0;
        int bestU=-1,bestV=-1,bestSeg=Integer.MAX_VALUE;

        for(int u=0;u<graph.size();u++){
            for(Edge e:graph.get(u)){
                if(u<e.to){ // 向きを一回だけ
                    Point a=points.get(u), b=points.get(e.to);
                    double[] prj=projParamDist(a,b,np); // t,dist
                    double dist=prj[1]; int segId=(e.segId>=0)?e.segId:bestSeg;
                    if(dist<best-EPS ||
                      (Math.abs(dist-best)<EPS && segId<bestSeg)){
                        best=dist; bestT=prj[0];
                        bestU=u; bestV=e.to; bestSeg=segId;
                    }
                }
            }
        }

        Point a=points.get(bestU), b=points.get(bestV);
        double cx=a.x+bestT*(b.x-a.x), cy=a.y+bestT*(b.y-a.y);

        int connIdx;
        if(bestT<EPS)          connIdx=bestU;
        else if(bestT>1-EPS)   connIdx=bestV;
        else{
            connIdx=findExistingPoint(cx,cy);
            if(connIdx==-1){
                connIdx=points.size();
                points.add(new Point(cx,cy)); graph.add(new ArrayList<>());
                String cname="C"+(crossIndex.size()+1);
                crossIndex.put(cname,connIdx); reverseIndex.put(connIdx,cname);

                double d1=points.get(bestU).dist(points.get(connIdx));
                double d2=points.get(bestV).dist(points.get(connIdx));
                removeEdge(bestU,bestV,bestSeg);
                addEdge(bestU,connIdx,d1,bestSeg);
                addEdge(connIdx,bestV,d2,bestSeg);
            }
        }
        addEdge(newIdx,connIdx,np.dist(points.get(connIdx)),segCounter++);
        System.out.printf(Locale.US,"%.5f %.5f%n",cx,cy);
    }

    /* =============================================================
       3. 経路クエリ (小課題5‑6) – Yen 法
       ============================================================= */
    static void processQuery(String[] q){
        int s=getIndex(q[0]), t=getIndex(q[1]), k=Integer.parseInt(q[2]);
        if(s==-1||t==-1||s>=graph.size()||t>=graph.size()){ System.out.println("NA");return;}
        List<Path> paths=yen(s,t,k);
        if(paths.isEmpty()) System.out.println("NA");
        else for(Path p:paths) System.out.print(p);
    }
    static List<Path> yen(int s,int t,int K){
        List<Path> A=new ArrayList<>();
        PriorityQueue<Path>B=new PriorityQueue<>();
        Set<String> seen=new HashSet<>();

        Path first=dijkstraPath(graph,s,t);
        if(first==null) return A;
        A.add(first); seen.add(first.nodes.toString());

        for(int k=1;k<K;k++){
            Path prev=A.get(k-1);
            for(int i=0;i<prev.nodes.size()-1;i++){
                int spurNode=prev.nodes.get(i);
                List<Integer> root=prev.nodes.subList(0,i+1);

                List<List<Edge>> g=cloneGraph(graph);
                for(Path p:A){
                    if(p.nodes.size()>i && root.equals(p.nodes.subList(0,i+1))){
                        int u=p.nodes.get(i), v=p.nodes.get(i+1);
                        int uu=u,vv=v;
                        g.get(uu).removeIf(e->e.to==vv);
                    }
                }
                for(int j=0;j<i;j++) g.get(root.get(j)).clear();

                Path spur=dijkstraPath(g,spurNode,t);
                if(spur==null) continue;

                List<Integer> total=new ArrayList<>(root);
                total.remove(total.size()-1); total.addAll(spur.nodes);

                if(new HashSet<>(total).size()!=total.size()) continue;
                if(seen.add(total.toString())){
                    double cost=0;
                    for(int j=0;j<total.size()-1;j++) cost+=edgeCost(total.get(j),total.get(j+1));
                    B.add(new Path(cost,total));
                }
            }
            if(B.isEmpty()) break;
            A.add(B.poll());
        }
        return A;
    }
    static Path dijkstraPath(List<List<Edge>> g,int s,int t){
        int n=g.size(); double[] dist=new double[n]; int[] prev=new int[n];
        Arrays.fill(dist,Double.POSITIVE_INFINITY); Arrays.fill(prev,-1);
        PriorityQueue<Edge> pq=new PriorityQueue<>(Comparator.comparingDouble(e->e.cost));
        dist[s]=0; pq.add(new Edge(s,0));
        while(!pq.isEmpty()){
            Edge cur=pq.poll(); if(dist[cur.to]<cur.cost-EPS) continue;
            for(Edge e:g.get(cur.to)){
                double nd=dist[cur.to]+e.cost;
                if(nd+EPS<dist[e.to]){
                    dist[e.to]=nd; prev[e.to]=cur.to; pq.add(new Edge(e.to,nd));
                }
            }
        }
        if(Double.isInfinite(dist[t])) return null;
        List<Integer> path=new ArrayList<>();
        for(int v=t;v!=-1;v=prev[v]) path.add(v);
        Collections.reverse(path); return new Path(dist[t],path);
    }

    /* =============================================================
       4. 幹線道路（橋）検出 (小課題8)
       ============================================================= */
    static void printBridges(){
        int n=points.size(), time=0;
        int[] disc=new int[n], low=new int[n];
        Arrays.fill(disc,-1);
        List<int[]> bridges=new ArrayList<>();

        for(int v=0;v<n;v++) if(disc[v]==-1) time=dfsBridge(v,-1,time,disc,low,bridges);

        bridges.sort((a,b)->a[0]!=b[0]?a[0]-b[0]:a[1]-b[1]);
        for(int[] e:bridges)
            System.out.println(formatPoint(e[0])+" "+formatPoint(e[1]));
        System.out.println(); // ← 空行追加
    }
    static int dfsBridge(int v,int parent,int time,int[] disc,int[] low,List<int[]> bridges){
        disc[v]=low[v]=++time;
        for(Edge e:graph.get(v)){
            int to=e.to; if(to==parent) continue;
            if(disc[to]==-1){
                time=dfsBridge(to,v,time,disc,low,bridges);
                low[v]=Math.min(low[v],low[to]);
                if(low[to]>disc[v]+EPS) bridges.add(new int[]{Math.min(v,to),Math.max(v,to)});
            }else low[v]=Math.min(low[v],disc[to]);
        }
        return time;
    }

    /* ----------------- 補助 ----------------- */
    static List<List<Edge>> cloneGraph(List<List<Edge>> g){
        List<List<Edge>> cp=new ArrayList<>();
        for(List<Edge> list:g){
            List<Edge> nl=new ArrayList<>();
            for(Edge e:list) nl.add(new Edge(e.to,e.cost,e.segId));
            cp.add(nl);
        }
        return cp;
    }
    static void addEdge(int u,int v,double c,int id){
        graph.get(u).add(new Edge(v,c,id));
        graph.get(v).add(new Edge(u,c,id));
    }
    static void removeEdge(int u,int v,int id){
        graph.get(u).removeIf(e->e.to==v&&e.segId==id);
        graph.get(v).removeIf(e->e.to==u&&e.segId==id);
    }
    static double edgeCost(int u,int v){
        for(Edge e:graph.get(u)) if(e.to==v) return e.cost;
        throw new IllegalStateException();
    }
    static int findExistingPoint(double x,double y){
        for(int i=0;i<points.size();i++){
            Point p=points.get(i);
            if(Math.abs(p.x-x)<EPS && Math.abs(p.y-y)<EPS) return i;
        }
        return -1;
    }
    static int getIndex(String s){
        if(s.startsWith("C")) return crossIndex.getOrDefault(s,-1);
        try{ return Integer.parseInt(s)-1;}catch(Exception e){ return -1;}
    }
    static List<String> formatPath(List<Integer> path){
        List<String> res=new ArrayList<>();
        for(int idx:path) res.add(formatPoint(idx));
        return res;
    }
    static String formatPoint(int idx){
        if(idx<N) return String.valueOf(idx+1);
        return reverseIndex.getOrDefault(idx,String.valueOf(idx+1)); // 追加地点番号はそのまま
    }

    /* ----- 幾何ユーティリティ ----- */
    static boolean onSegment(Point a,Point b,Point p){
        return Math.abs(p.dist(a)+p.dist(b)-a.dist(b))<1e-6;
    }
    static Point getIntersection(Point A,Point B,Point C,Point D){
        double dx1=B.x-A.x, dy1=B.y-A.y;
        double dx2=D.x-C.x, dy2=D.y-C.y;
        double det=dx1*dy2-dy1*dx2;
        if(Math.abs(det)<EPS) return null;
        double t=((C.x-A.x)*dy2-(C.y-A.y)*dx2)/det;
        double u=((C.x-A.x)*dy1-(C.y-A.y)*dx1)/det;
        if(t<-EPS||t>1+EPS||u<-EPS||u>1+EPS) return null;
        return new Point(A.x+t*dx1,A.y+t*dy1);
    }
    static double projection(Point a,Point b,Point p){
        double dx=b.x-a.x, dy=b.y-a.y;
        double len2=dx*dx+dy*dy;
        double dot=(p.x-a.x)*dx+(p.y-a.y)*dy;
        return len2<EPS?0:dot/len2;
    }
    static double[] projParamDist(Point a,Point b,Point p){
        double dx=b.x-a.x, dy=b.y-a.y, len2=dx*dx+dy*dy;
        double t=len2<EPS?0:((p.x-a.x)*dx+(p.y-a.y)*dy)/len2;
        t=Math.max(0,Math.min(1,t));
        double px=a.x+t*dx, py=a.y+t*dy;
        return new double[]{t,Math.hypot(p.x-px,p.y-py)};
    }
}