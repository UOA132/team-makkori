import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Comparator;
import java.util.Objects; // <-- Add this import

public class GenerateTestCases {

    private static final String OUTPUT_DIR = "generated_test_data"; // 出力ディレクトリ名
    private static final Random random = new Random();

    // 基本的なPointクラス
    static class Point {
        long x, y; // 座標が大きいためlongを使用

        Point(long x, long y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y); // Objects.hash を使用
        }
    }


    public static void main(String[] args) {
        // 出力ディレクトリを作成
        new java.io.File(OUTPUT_DIR).mkdirs();

        try {
            // 小課題1のテストデータ生成
            System.out.println("Generating Subtask 1 test data...");
            generateSubtask1Tests();
            System.out.println("Subtask 1 test data generated.");

            // 小課題2のテストデータ生成
            System.out.println("Generating Subtask 2 test data...");
            generateSubtask2Tests();
            System.out.println("Subtask 2 test data generated.");

            // 小課題3のテストデータ生成
            System.out.println("Generating Subtask 3 test data...");
            generateSubtask3Tests();
            System.out.println("Subtask 3 test data generated.");

            // 小課題4のテストデータ生成
            System.out.println("Generating Subtask 4 test data...");
            generateSubtask4Tests();
            System.out.println("Subtask 4 test data generated.");

            // 小課題5 & 6 のテストデータ生成
            System.out.println("Generating Subtask 5 & 6 test data...");
            generateSubtask5And6Tests();
            System.out.println("Subtask 5 & 6 test data generated.");

            // 小課題7のテストデータ生成
            System.out.println("Generating Subtask 7 test data...");
            generateSubtask7Tests();
            System.out.println("Subtask 7 test data generated.");

            // 小課題8のテストデータ生成
            System.out.println("Generating Subtask 8 test data...");
            generateSubtask8Tests();
            System.out.println("Subtask 8 test data generated.");

            // 小課題9のテストデータ生成
            System.out.println("Generating Subtask 9 test data...");
            generateSubtask9Tests();
            System.out.println("Subtask 9 test data generated.");

            // 小課題10のテストデータ生成
            System.out.println("Generating Subtask 10 test data...");
            generateSubtask10Tests();
            System.out.println("Subtask 10 test data generated.");

            // 小課題11のテストデータ生成
            System.out.println("Generating Subtask 11 test data...");
            generateSubtask11Tests();
            System.out.println("Subtask 11 test data generated.");


        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error generating test data: " + e.getMessage());
        }
    }

    /**
     * 小課題1のテストデータを生成するメソッド
     */
    private static void generateSubtask1Tests() throws IOException {
        // テストケース1.1: 標準的な交差
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_1_1.txt"))) {
            out.println("# テストケース1.1: 標準的な交差");
            out.println("# 期待される出力:");
            out.println("# 2.50000 2.50000");
            out.println("4 2 0 0");
            out.println("0 0");
            out.println("5 5");
            out.println("0 5");
            out.println("5 0");
            out.println("1 2");
            out.println("3 4");
        }

        // テストケース1.2: 別の標準的な交差
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_1_2.txt"))) {
            out.println("# テストケース1.2: 別の標準的な交差");
            out.println("# 期待される出力:");
            out.println("# 20.00000 20.00000");
            out.println("4 2 0 0");
            out.println("10 10");
            out.println("30 30");
            out.println("10 30");
            out.println("30 10");
            out.println("1 2");
            out.println("3 4");
        }

        // テストケース2.1: 平行で交差しない
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_2_1.txt"))) {
            out.println("# テストケース2.1: 平行で交差しない線分");
            out.println("# 期待される出力:");
            out.println("# NA");
            out.println("4 2 0 0");
            out.println("0 0");
            out.println("5 0");
            out.println("0 1");
            out.println("5 1");
            out.println("1 2");
            out.println("3 4");
        }

        // テストケース2.2: 平行で同一直線上にあるが、重ならない
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_2_2.txt"))) {
            out.println("# テストケース2.2: 平行で同一直線上にあるが、重ならない線分");
            out.println("# 期待される出力:");
            out.println("# NA");
            out.println("4 2 0 0");
            out.println("0 0");
            out.println("1 1");
            out.println("2 2");
            out.println("3 3");
            out.println("1 2");
            out.println("3 4");
        }

        // テストケース2.3: 平行でないが線分外で交差
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_2_3.txt"))) {
            out.println("# テストケース2.3: 平行でないが線分外で交差する線分");
            out.println("# 期待される出力:");
            out.println("# NA");
            out.println("4 2 0 0");
            out.println("0 0");
            out.println("1 1");
            out.println("10 0");
            out.println("11 1");
            out.println("1 2");
            out.println("3 4");
        }

        // テストケース3.1: 一方の線分の端点でもう一方の線分と交差 (T字路)
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_3_1.txt"))) {
            out.println("# テストケース3.1: T字路（線分がもう一方の端点と交差）");
            out.println("# 期待される出力:");
            out.println("# NA");
            out.println("4 2 0 0");
            out.println("0 0");
            out.println("10 0");
            out.println("5 0");
            out.println("5 10");
            out.println("1 2");
            out.println("3 4");
        }

        // テストケース3.2: 両方の線分の端点が一致 (I字路)
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_3_2.txt"))) {
            out.println("# テストケース3.2: I字路（両方の線分の端点が一致）");
            out.println("# 期待される出力:");
            out.println("# NA");
            out.println("4 2 0 0");
            out.println("0 0");
            out.println("5 0");
            out.println("5 0");
            out.println("10 0");
            out.println("1 2");
            out.println("3 4");
        }

        // テストケース3.3: 一方の線分の内部でもう一方の線分の端点と交差 (L字路)
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_3_3.txt"))) {
            out.println("# テストケース3.3: L字路（一方の線分内部でもう一方の端点と交差）");
            out.println("# 期待される出力:");
            out.println("# NA");
            out.println("4 2 0 0");
            out.println("0 0");
            out.println("5 5");
            out.println("5 5");
            out.println("5 0");
            out.println("1 2");
            out.println("3 4");
        }

        // テストケース4.1: わずかにずれた平行線
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_4_1.txt"))) {
            out.println("# テストケース4.1: わずかにずれた平行線（浮動小数点誤差の確認）");
            out.println("# 期待される出力:");
            out.println("# NA");
            out.println("4 2 0 0");
            out.println("0 0");
            out.println("5 0");
            out.println("0 0.000000001");
            out.println("5 0.000000001");
            out.println("1 2");
            out.println("3 4");
        }

        // テストケース4.2: わずかにずれた同一直線上の線分 (重ならない)
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_4_2.txt"))) {
            out.println("# テストケース4.2: わずかにずれた同一直線上だが重ならない線分");
            out.println("# 期待される出力:");
            out.println("# NA");
            out.println("4 2 0 0");
            out.println("0 0");
            out.println("10 10");
            out.println("10.000000001 10.000000001");
            out.println("20 20");
            out.println("1 2");
            out.println("3 4");
        }

        // テストケース5.1: 垂直線と水平線の交差
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_5_1.txt"))) {
            out.println("# テストケース5.1: 垂直線と水平線の交差");
            out.println("# 期待される出力:");
            out.println("# 5.00000 5.00000");
            out.println("4 2 0 0");
            out.println("0 5");
            out.println("10 5");
            out.println("5 0");
            out.println("5 10");
            out.println("1 2");
            out.println("3 4");
        }
    }

    /**
     * 小課題2のテストデータを生成するメソッド
     */
    private static void generateSubtask2Tests() throws IOException {
        // テストケース1.1: PDFの入力例 (複数の交差地点とソート)
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_2_1_1.txt"))) {
            out.println("# テストケース2.1.1: PDF入力例（複数の交差地点とソート）");
            out.println("# 期待される出力:");
            out.println("# 3.66667 3.66667");
            out.println("# 4.86885 2.70492");
            out.println("# 5.86957 3.26087");
            out.println("6 5 0 0");
            out.println("0 0");
            out.println("2 5");
            out.println("4 7");
            out.println("5 5");
            out.println("7 1");
            out.println("9 5");
            out.println("1 4");
            out.println("1 6");
            out.println("2 5");
            out.println("3 5");
            out.println("4 6");
        }

        // テストケース1.2: 異なる座標とソート順の確認
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_2_1_2.txt"))) {
            out.println("# テストケース2.1.2: 異なる座標でのソート順の確認");
            out.println("# 期待される出力:");
            out.println("# 5.00000 5.00000");
            out.println("4 2 0 0");
            out.println("0 0");
            out.println("10 10");
            out.println("0 10");
            out.println("10 0");
            out.println("1 2");
            out.println("3 4");
        }

        // テストケース1.3: 複数の交点がX座標が同じでY座標が異なる場合
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_2_1_3.txt"))) {
            out.println("# テストケース2.1.3: 複数の交点がX座標が同じでY座標が異なる場合");
            out.println("# 期待される出力:");
            out.println("# 5.00000 2.50000"); // (5, 2.5)
            out.println("# 5.00000 7.50000"); // (5, 7.5)
            out.println("6 3 0 0");
            out.println("0 0");
            out.println("10 0");
            out.println("5 10");
            out.println("5 0");
            out.println("0 5");
            out.println("10 5");
            out.println("1 3"); // (0,0)-(5,10)
            out.println("2 4"); // (10,0)-(5,0)
            out.println("5 6"); // (0,5)-(10,5) (水平線)
        }

        // テストケース2.2.1: 平行で離れた線分
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_2_2_1.txt"))) {
            out.println("# テストケース2.2.1: 平行で離れた線分（交差なし）");
            out.println("# 期待される出力:");
            out.println("# (空行)");
            out.println("4 2 0 0");
            out.println("0 0");
            out.println("5 0");
            out.println("0 10");
            out.println("5 10");
            out.println("1 2");
            out.println("3 4");
        }

        // テストケース2.2.2: 同一直線上だが重ならない線分
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_2_2_2.txt"))) {
            out.println("# テストケース2.2.2: 同一直線上だが重ならない線分（交差なし）");
            out.println("# 期待される出力:");
            out.println("# (空行)");
            out.println("4 2 0 0");
            out.println("0 0");
            out.println("5 5");
            out.println("6 6");
            out.println("10 10");
            out.println("1 2");
            out.println("3 4");
        }

        // テストケース2.2.3: 延長線は交差するが線分外
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_2_2_3.txt"))) {
            out.println("# テストケース2.2.3: 延長線は交差するが線分範囲外");
            out.println("# 期待される出力:");
            out.println("# (空行)");
            out.println("4 2 0 0");
            out.println("0 0");
            out.println("1 1");
            out.println("10 0");
            out.println("11 1");
            out.println("1 2");
            out.println("3 4");
        }

        // テストケース2.3.1: 一方の線分の端点でもう一方の線分と交差 (T字路)
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_2_3_1.txt"))) {
            out.println("# テストケース2.3.1: T字路（線分が端点に接する）");
            out.println("# 期待される出力:");
            out.println("# (空行)");
            out.println("4 2 0 0");
            out.println("0 0");
            out.println("10 0");
            out.println("5 0");
            out.println("5 10");
            out.println("1 2");
            out.println("3 4");
        }

        // テストケース2.3.2: 両方の線分の端点が一致 (I字路)
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_2_3_2.txt"))) {
            out.println("# テストケース2.3.2: I字路（両線分の端点が一致）");
            out.println("# 期待される出力:");
            out.println("# (空行)");
            out.println("4 2 0 0");
            out.println("0 0");
            out.println("5 0");
            out.println("5 0");
            out.println("10 0");
            out.println("1 2");
            out.println("3 4");
        }

        // テストケース2.3.3: 複数の線分が共通の端点で集まる (星形・放射状)
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_2_3_3.txt"))) {
            out.println("# テストケース2.3.3: 複数の線分が共通の端点に集まる（交差地点ではない）");
            out.println("# 期待される出力:");
            out.println("# (空行)");
            out.println("5 3 0 0");
            out.println("5 5");
            out.println("0 0");
            out.println("10 0");
            out.println("0 10");
            out.println("10 10");
            out.println("1 2");
            out.println("1 3");
            out.println("1 4");
        }

        // テストケース2.4.1: 複数の線分が同一の内部点で交差
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_2_4_1.txt"))) {
            out.println("# テストケース2.4.1: 複数の線分が同一の内部点で交差（重複排除の確認）");
            out.println("# 期待される出力:");
            out.println("# 5.00000 5.00000");
            out.println("6 3 0 0");
            out.println("0 0");
            out.println("10 10");
            out.println("0 10");
            out.println("10 0");
            out.println("5 0");
            out.println("5 10");
            out.println("1 2");
            out.println("3 4");
            out.println("5 6");
        }

        // テストケース2.4.2: 複数の交差地点がX座標が同じでY座標が異なる
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_2_4_2.txt"))) {
            out.println("# テストケース2.4.2: X座標が同じでY座標が異なる交差地点のソート確認");
            out.println("# 期待される出力:");
            out.println("# 2.50000 5.00000"); // (2.5, 5)
            out.println("# 5.00000 5.00000"); // (5, 5)
            out.println("# 7.50000 5.00000"); // (7.5, 5)
            out.println("8 4 0 0");
            out.println("0 0");
            out.println("10 0");
            out.println("0 10");
            out.println("10 10");
            out.println("0 5"); // P5
            out.println("10 5"); // P6
            out.println("5 0"); // P7
            out.println("5 10"); // P8
            out.println("1 4"); // (0,0)-(10,10)
            out.println("2 3"); // (10,0)-(0,10)
            out.println("5 6"); // (0,5)-(10,5)
            out.println("7 8"); // (5,0)-(5,10)
        }

        // テストケース2.5.1: わずかな交差 (EPSの挙動) - 実際には交差しない
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_2_5_1.txt"))) {
            out.println("# テストケース2.5.1: わずかなズレのある線分（交差なし、EPS挙動）");
            out.println("# 期待される出力:");
            out.println("# (空行)");
            out.println("4 2 0 0");
            out.println("0 0");
            out.println("10 0");
            out.println("0 0.0000001");
            out.println("10 0.0000001");
            out.println("1 4"); // 1->4 (0,0)-(10,0.0000001)
            out.println("2 3"); // 2->3 (10,0)-(0,0.0000001)
        }
    }

    /**
     * 小課題3のテストデータを生成するメソッド
     */
    private static void generateSubtask3Tests() throws IOException {
        // テストケース1.1: PDFの入力例 (地点と交差地点の組み合わせ)
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_3_1_1.txt"))) {
            out.println("# テストケース3.1.1: PDF入力例（地点と交差地点の最短距離）");
            out.println("# 期待される出力:");
            out.println("# 7.07107");
            out.println("# 6.10882");
            out.println("# 5.88562");
            out.println("# NA");
            out.println("# 2.68432");
            out.println("6 5 0 5");
            out.println("0 0");
            out.println("2 5");
            out.println("4 7");
            out.println("5 5");
            out.println("7 1");
            out.println("9 5");
            out.println("1 4");
            out.println("1 6");
            out.println("2 5");
            out.println("3 5");
            out.println("4 6");
            out.println("1 4 1");
            out.println("5 6 1");
            out.println("C1 6 1");
            out.println("C1000 1 1");
            out.println("C1 C3 1");
        }

        // テストケース1.2: シンプルな直線上の経路
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_3_1_2.txt"))) {
            out.println("# テストケース3.1.2: シンプルな直線上の経路");
            out.println("# 期待される出力:");
            out.println("# 15.00000");
            out.println("5 2 0 1");
            out.println("0 0");
            out.println("10 0");
            out.println("20 0");
            out.println("5 0");
            out.println("15 0");
            out.println("1 3"); // 道1: (0,0)-(20,0)
            out.println("2 5"); // 道2: (10,0)-(15,0)
            out.println("1 5 1"); // クエリ: 点1(0,0)から点5(15,0)へ
        }

        // テストケース2.1: 孤立した地点
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_3_2_1.txt"))) {
            out.println("# テストケース3.2.1: 孤立した地点へのクエリ（到達不能）");
            out.println("# 期待される出力:");
            out.println("# NA");
            out.println("4 1 0 1");
            out.println("0 0");
            out.println("10 0");
            out.println("20 20");
            out.println("30 30");
            out.println("1 2");
            out.println("1 3 1");
        }

        // テストケース2.2: 存在しない地点番号または交差地点番号へのクエリ
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_3_2_2.txt"))) {
            out.println("# テストケース3.2.2: 存在しない地点/交差地点へのクエリ");
            out.println("# 期待される出力:");
            out.println("# NA");
            out.println("# NA");
            out.println("3 1 0 2");
            out.println("0 0");
            out.println("10 0");
            out.println("20 0");
            out.println("1 2");
            out.println("1 100 1");
            out.println("C99 2 1");
        }

        // テストケース3.1: T字路からの経路
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_3_3_1.txt"))) {
            out.println("# テストケース3.3.1: T字路からの経路");
            out.println("# 期待される出力:");
            out.println("# 7.07107");
            out.println("4 2 0 1");
            out.println("0 0");
            out.println("10 0");
            out.println("5 0");
            out.println("5 5");
            out.println("1 2");
            out.println("3 4");
            out.println("1 4 1");
        }

        // テストケース3.2: 複数の交差地点を含む経路
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_3_3_2.txt"))) {
            out.println("# テストケース3.3.2: 複数の交差地点を含む経路");
            out.println("# 期待される出力:");
            out.println("# 11.31371");
            out.println("8 4 0 1");
            out.println("0 0");
            out.println("10 0");
            out.println("0 10");
            out.println("10 10");
            out.println("2 8");
            out.println("8 2");
            out.println("5 0");
            out.println("5 10");
            out.println("1 4"); // (0,0)-(10,10)
            out.println("2 3"); // (10,0)-(0,10)
            out.println("5 6"); // (2,8)-(8,2)
            out.println("7 8"); // (5,0)-(5,10)
            out.println("1 6 1"); // (0,0)から(8,2)への最短経路
        }

        // テストケース3.3: 自ループ（同じ始点と終点）
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_3_3_3.txt"))) {
            out.println("# Test Case 3.3.3: 自ループ（始点と終点が同じ）");
            out.println("# 期待される出力:");
            out.println("# 0.00000");
            out.println("# 0.00000");
            out.println("3 1 0 2");
            out.println("0 0");
            out.println("10 0");
            out.println("20 0");
            out.println("1 2");
            out.println("1 1 1");
            out.println("C1 C1 1"); // C1が存在すれば
        }

        // テストケース4.1: 0,0に近い座標
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_3_4_1.txt"))) {
            out.println("# Test Case 3.4.1: 0,0に近い座標");
            out.println("# 期待される出力:");
            out.println("# 0.00001");
            out.println("4 2 0 1");
            out.println("0 0");
            out.println("0.00001 0.00001");
            out.println("0 0.00001");
            out.println("0.00001 0");
            out.println("1 2");
            out.println("3 4");
            out.println("1 4 1");
        }

        // テストケース4.2: 最大座標値に近い座標
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_3_4_2.txt"))) {
            out.println("# Test Case 3.4.2: 最大座標値に近い座標");
            out.println("# 期待される出力:");
            out.println("# 14.14214");
            out.println("4 2 0 1");
            out.println("9990 9990");
            out.println("10000 10000");
            out.println("9990 10000");
            out.println("10000 9990");
            out.println("1 2");
            out.println("3 4");
            out.println("1 4 1");
        }
    }

    /**
     * 小課題4のテストデータを生成するメソッド
     */
    private static void generateSubtask4Tests() throws IOException {
        // テストケース1.1: PDFの入力例
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_4_1_1.txt"))) {
            out.println("# テストケース4.1.1: PDF入力例（距離と経路）");
            out.println("# 期待される出力:");
            out.println("# 7.07107");
            out.println("# 1 C1 4");
            out.println("# 6.10882");
            out.println("# 5 C3 6");
            out.println("# 5.88562");
            out.println("# C1 4 6");
            out.println("# NA");
            out.println("# 2.68432");
            out.println("# C1 C2 C3");
            out.println("6 5 0 5");
            out.println("0 0");
            out.println("2 5");
            out.println("4 7");
            out.println("5 5");
            out.println("7 1");
            out.println("9 5");
            out.println("1 4");
            out.println("1 6");
            out.println("2 5");
            out.println("3 5");
            out.println("4 6");
            out.println("1 4 1");
            out.println("5 6 1");
            out.println("C1 6 1");
            out.println("C1000 1 1");
            out.println("C1 C3 1");
        }

        // テストケース1.2: シンプルな直線上の経路
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_4_1_2.txt"))) {
            out.println("# テストケース4.1.2: シンプルな直線上の経路（距離と経路）");
            out.println("# 期待される出力:");
            out.println("# 15.00000");
            out.println("# 1 4 5"); // Path 1 - C1 - 5 if C1=(5,0), C2=(15,0) (point numbers 4 and 5 in this file)
            out.println("5 2 0 1");
            out.println("0 0"); // 1
            out.println("10 0"); // 2
            out.println("20 0"); // 3
            out.println("5 0"); // 4 (C1候補)
            out.println("15 0"); // 5 (C2候補)
            out.println("1 3"); // 道1: (0,0)-(20,0)
            out.println("2 5"); // 道2: (10,0)-(15,0)
            out.println("1 5 1"); // クエリ: 点1(0,0)から点5(15,0)へ
        }

        // テストケース2.1: 孤立した地点
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_4_2_1.txt"))) {
            out.println("# テストケース4.2.1: 孤立した地点へのクエリ（到達不能）");
            out.println("# 期待される出力:");
            out.println("# NA");
            out.println("4 1 0 1");
            out.println("0 0");
            out.println("10 0");
            out.println("20 20");
            out.println("30 30");
            out.println("1 2");
            out.println("1 3 1");
        }

        // テストケース2.2: 存在しない地点番号または交差地点番号へのクエリ
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_4_2_2.txt"))) {
            out.println("# テストケース4.2.2: 存在しない地点/交差地点へのクエリ");
            out.println("# 期待される出力:");
            out.println("# NA");
            out.println("# NA");
            out.println("3 1 0 2");
            out.println("0 0");
            out.println("10 0");
            out.println("20 0");
            out.println("1 2");
            out.println("1 100 1");
            out.println("C99 2 1");
        }
        
        // テストケース3.1: 同じ距離で複数の経路が存在し、辞書順がテストされるケース
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_4_3_1.txt"))) {
            out.println("# テストケース4.3.1: 同じ距離で複数の経路が存在（辞書順は実装依存）");
            out.println("# 期待される出力:");
            out.println("# 14.14214"); // sqrt(10^2+10^2)
            out.println("# 1 C1 4"); // Path: point1 - C1 - point4 (assuming C1 is (5,5))
            out.println("5 4 0 1");
            out.println("0 0"); // 1
            out.println("10 0"); // 2
            out.println("0 10"); // 3
            out.println("10 10"); // 4
            out.println("5 5"); // 5 (C1候補)
            out.println("1 5"); // 道1-5 (0,0)-(5,5)
            out.println("2 5"); // 道2-5 (10,0)-(5,5)
            out.println("3 5"); // 道3-5 (0,10)-(5,5)
            out.println("4 5"); // 道4-5 (10,10)-(5,5)
            out.println("1 4 1"); // クエリ: 点1から点4へ
        }

        // テストケース4.1: 0,0に近い座標
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_4_4_1.txt"))) {
            out.println("# テストケース4.4.1: 0,0に近い座標（距離と経路）");
            out.println("# 期待される出力:");
            out.println("# 0.00001");
            out.println("# 1 4"); // Path might be 1-4 if 4 is intersection
            out.println("4 2 0 1");
            out.println("0 0"); // 1
            out.println("0.00001 0.00001"); // 2
            out.println("0 0.00001"); // 3
            out.println("0.00001 0"); // 4
            out.println("1 2"); // (0,0)-(0.00001,0.00001)
            out.println("3 4"); // (0,0.00001)-(0.00001,0)
            out.println("1 4 1"); // クエリ: 点1から点4へ
        }

        // テストケース4.2: 最大座標値に近い座標
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_4_4_2.txt"))) {
            out.println("# テストケース4.4.2: 最大座標値に近い座標（距離と経路）");
            out.println("# 期待される出力:");
            out.println("# 14.14214");
            out.println("# 1 C1 4"); // Path: 1 - C1 - 4 (assuming C1 is the intersection of 1-2 and 3-4)
            out.println("4 2 0 1");
            out.println("9990 9990"); // 1
            out.println("10000 10000"); // 2
            out.println("9990 10000"); // 3
            out.println("10000 9990"); // 4
            out.println("1 2"); // (9990,9990)-(10000,10000)
            out.println("3 4"); // (9990,10000)-(10000,9990)
            out.println("1 4 1"); // クエリ: 点1から点4へ
        }
    }

    /**
     * 小課題5 & 6 のテストデータを生成するメソッド
     */
    private static void generateSubtask5And6Tests() throws IOException {
        // テストケース1.1: PDFの入力例 (小課題6の入力例を使用)
        // K=2のクエリが含まれるため、2つの最短経路が出力される
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_6_1_1.txt"))) {
            out.println("# テストケース6.1.1: PDF入力例（第K最短路と経路提案）");
            out.println("# 期待される出力（PDFより）:");
            out.println("# 7.07107");
            out.println("# 1 C1 4");
            out.println("# 8.65895");
            out.println("# 1 C2 C3 4");
            out.println("# 8.99493");
            out.println("# 1 C2 C1 4");
            out.println("# 9.81418");
            out.println("# 1 C1 C2 C3 4");
            out.println("# 12.77110");
            out.println("# 1 C2 5 C3 4");
            out.println("# 2.68432");
            out.println("# C1 C2 C3");
            out.println("# 3.83003");
            out.println("# C1 4 C3");
            out.println("# 6.79648");
            out.println("# C1 C2 5 C3");
            out.println("# 9.46671");
            out.println("# 6 C3");
            out.println("# 11.90000");
            out.println("# C2 C3 C1 1");
            out.println("# 16.01210");
            out.println("# C1 1 C2 5 C3");
            out.println("6 5 0 2"); // N M P Q (Q=2)
            out.println("0 0"); // 1
            out.println("2 5"); // 2
            out.println("4 7"); // 3
            out.println("5 5"); // 4
            out.println("7 1"); // 5
            out.println("9 5"); // 6
            out.println("1 4"); // (0,0)-(5,5) - 交差あり (C1,C2,C3)
            out.println("1 6"); // (0,0)-(9,5)
            out.println("2 5"); // (2,5)-(7,1)
            out.println("3 5"); // (4,7)-(7,1)
            out.println("4 6"); // (5,5)-(9,5)
            out.println("1 4 5"); // クエリ1: 1から4へ、K=5まで
            out.println("C1 C3 10"); // クエリ2: C1からC3へ、K=10まで
        }

        // テストケース1.2: シンプルなグラフでK=2の経路
        // 直線に分岐が少しあるパターン
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_6_1_2.txt"))) {
            out.println("# テストケース6.1.2: シンプルなグラフでのK=2経路");
            out.println("# 期待される出力:");
            out.println("# 20.00000"); // Path 1: 1-2-3 (cost 20)
            out.println("# 1 2 3");
            out.println("# 22.36068"); // Path 2: 1-4-5-3 (cost sqrt(5^2+5^2) + sqrt(10^2+0^2) + sqrt(5^2+5^2) approx 7.07+10+7.07)
            out.println("# 1 4 5 3"); // This path cost may differ based on actual point coordinates.
            out.println("6 5 0 1");
            out.println("0 0"); // 1
            out.println("10 0"); // 2
            out.println("20 0"); // 3
            out.println("5 5");  // 4
            out.println("15 5"); // 5
            out.println("10 10"); // 6
            out.println("1 2");
            out.println("2 3");
            out.println("1 4");
            out.println("4 5");
            out.println("5 3");
            out.println("1 3 2"); // クエリ: 1から3へ、K=2まで
        }

        // テストケース1.3: 橋が存在し、橋を避けた経路がK番目になる場合
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_6_1_3.txt"))) {
            out.println("# テストケース6.1.3: 橋を避けた経路がK番目になる場合");
            out.println("# 期待される出力:");
            out.println("# 20.00000"); // Path 1: 1-2-3 (cost 20)
            out.println("# 1 2 3");
            out.println("# 34.14214"); // Path 2: 1-2-4-5-3 (cost 10 + sqrt(10^2+10^2) + 10 + sqrt(10^2+10^2) = 10 + 14.14 + 10 = 34.14)
            out.println("# 1 2 4 5 3");
            out.println("5 4 0 1");
            out.println("0 0"); // 1
            out.println("10 0"); // 2 (橋の片側)
            out.println("20 0"); // 3 (橋のもう片側)
            out.println("10 10"); // 4
            out.println("20 10"); // 5
            out.println("1 2"); // Bridge (direct 1-2)
            out.println("2 4"); // Detour
            out.println("4 5"); // Detour
            out.println("5 3"); // Detour
            out.println("1 3 2"); // クエリ: 1から3へ、K=2まで
        }

        // テストケース2.1: 到達不可能、またはK番目の経路が存在しない場合
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_6_2_1.txt"))) {
            out.println("# テストケース6.2.1: 到達不能、またはK番目の経路が存在しない場合");
            out.println("# 期待される出力:");
            out.println("# NA"); // Q1
            out.println("# 10.00000"); // Q2
            out.println("# 1 2");
            out.println("4 2 0 2");
            out.println("0 0"); // 1
            out.println("10 0"); // 2
            out.println("20 20"); // 3
            out.println("30 30"); // 4
            out.println("1 2");
            out.println("3 4");
            out.println("1 3 1"); // クエリ1: 到達不可能
            out.println("1 2 5"); // クエリ2: Kが大きすぎる (経路は1つしか存在しない)
        }
    }

    /**
     * 小課題7のテストデータを生成するメソッド
     */
    private static void generateSubtask7Tests() throws IOException {
        // テストケース1.1: PDFの入力例
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_7_1_1.txt"))) {
            out.println("# テストケース7.1.1: PDF入力例（最適な道の建設提案）");
            out.println("# 期待される出力:");
            out.println("# 5.78049 1.97561");
            out.println("# 9.00000 5.00000");
            out.println("# 4.20000 5.40000");
            out.println("# 4.20000 6.60000");
            out.println("6 5 4 0"); // N M P Q
            out.println("0 0"); // 1
            out.println("2 5"); // 2
            out.println("4 7"); // 3
            out.println("5 5"); // 4
            out.println("7 1"); // 5
            out.println("9 5"); // 6
            out.println("1 4"); // (0,0)-(5,5)
            out.println("1 6"); // (0,0)-(9,5)
            out.println("2 5"); // (2,5)-(7,1)
            out.println("3 5"); // (4,7)-(7,1)
            out.println("4 6"); // (5,5)-(9,5)
            out.println("5 1");    // 新規地点1
            out.println("11 5");   // 新規地点2
            out.println("5 4");    // 新規地点3
            out.println("3 6");    // 新規地点4
        }

        // テストケース1.2: 既存ノードへの接続
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_7_1_2.txt"))) {
            out.println("# テストケース7.1.2: 既存ノードへの接続");
            out.println("# 期待される出力:");
            out.println("# 0.00000 0.00000");
            out.println("# 10.00000 0.00000");
            out.println("3 1 2 0");
            out.println("0 0"); // 1
            out.println("10 0"); // 2
            out.println("5 0"); // 3
            out.println("1 2");
            out.println("0 0");    // 既存の点1 (0,0) と同じ
            out.println("10 0");   // 既存の点2 (10,0) と同じ
        }

        // テストケース1.3: 水平・垂直線への接続
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_7_1_3.txt"))) {
            out.println("# テストケース7.1.3: 水平・垂直線への接続");
            out.println("# 期待される出力:");
            out.println("# 5.00000 0.00000");
            out.println("# 5.00000 0.00000");
            out.println("4 2 2 0");
            out.println("0 0"); // 1
            out.println("10 0"); // 2
            out.println("0 10"); // 3
            out.println("10 10"); // 4
            out.println("1 2");
            out.println("3 4");
            out.println("5 5");      // 新規地点1 (中央)
            out.println("5 0.1");    // 新規地点2 (水平線に非常に近い)
        }

        // テストケース2.1: 線分の端点への射影
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_7_2_1.txt"))) {
            out.println("# テストケース7.2.1: 線分の端点への射影");
            out.println("# 期待される出力:");
            out.println("# 0.00000 0.00000");
            out.println("# 10.00000 0.00000");
            out.println("3 1 2 0");
            out.println("0 0"); // 1
            out.println("10 0"); // 2
            out.println("5 5"); // 3
            out.println("1 2");
            out.println("-1 0");     // 線分(0,0)-(10,0)の左外
            out.println("11 0");    // 線分(0,0)-(10,0)の右外
        }

        // テストケース2.2: わずかなズレによる精度確認
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_7_2_2.txt"))) {
            out.println("# テストケース7.2.2: わずかなズレによる精度確認");
            out.println("# 期待される出力:");
            out.println("# 5.00000 0.00000");
            out.println("3 1 1 0");
            out.println("0 0"); // 1
            out.println("10 0"); // 2
            out.println("0 0.00000001"); // 3
            out.println("1 2");
            out.println("5 0.000000001"); // 非常に近い点
        }

        // テストケース2.3: 既存ノードに非常に近いが異なる点
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_7_2_3.txt"))) {
            out.println("# テストケース7.2.3: 既存ノードに非常に近いが異なる点");
            out.println("# 期待される出力:");
            out.println("# 0.00000 0.00000");
            out.println("3 1 1 0");
            out.println("0 0"); // 1
            out.println("10 0"); // 2
            out.println("5 5"); // 3
            out.println("1 2");
            out.println("0.000001 0.000001"); // 点1(0,0)に非常に近い
        }

        // テストケース3.1: 新規地点が既存の交差地点に近い場合
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_7_3_1.txt"))) {
            out.println("# テストケース7.3.1: 新規地点が既存の交差地点に近い場合");
            out.println("# 期待される出力:");
            out.println("# 5.00000 5.00000");
            out.println("4 2 1 0");
            out.println("0 0"); // 1
            out.println("10 10"); // 2
            out.println("0 10"); // 3
            out.println("10 0"); // 4
            out.println("1 2");
            out.println("3 4");
            out.println("5 5.000001"); // 交点(5,5)に非常に近い
        }

        // テストケース3.2: 複数の線分を持つ単純な星型ネットワーク
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_7_3_2.txt"))) {
            out.println("# Test Case 7.3.2: 複数の線分を持つ単純な星型ネットワーク");
            out.println("# 期待される出力:");
            out.println("# 5.00000 2.00000"); // Projection of (5,2) onto (5,5)-(10,0)
            out.println("5 4 1 0");
            out.println("5 5");    // 1 (中心点)
            out.println("0 0");    // 2
            out.println("10 0");   // 3
            out.println("0 10");   // 4
            out.println("10 10");  // 5
            out.println("1 2"); // (5,5)-(0,0)
            out.println("1 3"); // (5,5)-(10,0)
            out.println("1 4"); // (5,5)-(0,10)
            out.println("1 5"); // (5,5)-(10,10)
            out.println("5 2"); // 中心点の右下にある新規点
        }
    }

    /**
     * 小課題8のテストデータを生成するメソッド
     */
    private static void generateSubtask8Tests() throws IOException {
        // テストケース1.1: シンプルな橋があるグラフ
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_8_1_1.txt"))) {
            out.println("# テストケース8.1.1: シンプルな橋があるグラフ");
            out.println("# 期待される出力:");
            out.println("# 1 2");
            out.println("# 2 3");
            out.println("# 3 4");
            out.println("4 3 0 0");
            out.println("0 0"); // 1
            out.println("10 0"); // 2
            out.println("20 0"); // 3
            out.println("30 0"); // 4
            out.println("1 2");
            out.println("2 3");
            out.println("3 4");
        }

        // テストケース1.2: サイクル内に橋がないグラフ
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_8_1_2.txt"))) {
            out.println("# テストケース8.1.2: サイクル内に橋がないグラフ");
            out.println("# 期待される出力:");
            out.println("# (空行)");
            out.println("4 4 0 0");
            out.println("0 0"); // 1
            out.println("10 0"); // 2
            out.println("10 10"); // 3
            out.println("0 10"); // 4
            out.println("1 2");
            out.println("2 3");
            out.println("3 4");
            out.println("4 1");
        }

        // テストケース1.3: サイクルと橋が混在するグラフ
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_8_1_3.txt"))) {
            out.println("# テストケース8.1.3: サイクルと橋が混在するグラフ");
            out.println("# 期待される出力:");
            out.println("# 2 5");
            out.println("# 5 6");
            out.println("6 6 0 0");
            out.println("0 0"); // 1
            out.println("10 0"); // 2
            out.println("10 10"); // 3
            out.println("0 10"); // 4
            out.println("20 5"); // 5
            out.println("30 5"); // 6
            out.println("1 2");
            out.println("2 3");
            out.println("3 4");
            out.println("4 1");
            out.println("2 5");
            out.println("5 6");
        }

        // テストケース2.1: 交差地点が橋となるケース
        // このケースでは交差地点が生成され、それが橋の片側になるかをテストする
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_8_2_1.txt"))) {
            out.println("# テストケース8.2.1: 交差地点が橋となるケース");
            out.println("# 期待される出力:");
            out.println("# C1 6"); // Assuming C1 is the intersection of (0,0)-(10,10) and (0,10)-(10,0)
            out.println("6 4 0 0");
            out.println("0 0"); // 1
            out.println("10 10"); // 2
            out.println("0 10"); // 3
            out.println("10 0"); // 4
            out.println("5 5");   // 5 (この点が交差地点になることを期待: C1)
            out.println("20 5");  // 6 (新しいノード)
            out.println("1 2");   // (0,0)-(10,10)
            out.println("3 4");   // (0,10)-(10,0) -> これら2つが(5,5)で交差する
            out.println("1 5");   // (0,0)-(5,5) -> この線分は交差点を含むので、辺(0,0)-C1 になる
            out.println("5 6");   // (5,5)-(20,5) -> C1-6 になる。これが橋。
        }

        // テストケース2.2: 複雑な道路網での複数の橋
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_8_2_2.txt"))) {
            out.println("# テストケース8.2.2: 複雑な道路網での複数の橋");
            out.println("# 期待される出力:");
            out.println("# 1 5");
            out.println("# 2 6");
            out.println("# 3 7");
            out.println("8 6 0 0");
            out.println("0 0"); // 1
            out.println("10 0"); // 2
            out.println("20 0"); // 3
            out.println("30 0"); // 4
            out.println("5 5"); // 5
            out.println("15 5"); // 6
            out.println("25 5"); // 7
            out.println("10 10"); // 8 (使わない点だが、Nの制約を満たすため)
            out.println("1 2"); // 0-10
            out.println("2 3"); // 10-20
            out.println("3 4"); // 20-30
            out.println("1 5"); // (0,0)-(5,5)
            out.println("2 6"); // (10,0)-(15,5)
            out.println("3 7"); // (20,0)-(25,5)
        }
        
        // テストケース3.1: 孤立したノードと辺 (全てが橋)
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_8_3_1.txt"))) {
            out.println("# テストケース8.3.1: 孤立したノードと辺（全てが橋）");
            out.println("# 期待される出力:");
            out.println("# 1 2");
            out.println("# 3 4");
            out.println("4 2 0 0");
            out.println("0 0"); // 1
            out.println("1 1"); // 2
            out.println("10 10"); // 3
            out.println("11 11"); // 4
            out.println("1 2");
            out.println("3 4");
        }

        // テストケース4.1: 検出順序とソート順
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_8_4_1.txt"))) {
            out.println("# テストケース8.4.1: 検出順序とソート順の確認");
            out.println("# 期待される出力:");
            out.println("# 1 2");
            out.println("# 1 5");
            out.println("# 2 3");
            out.println("# 2 6");
            out.println("# 3 4");
            out.println("6 5 0 0");
            out.println("0 0"); // 1
            out.println("10 0"); // 2
            out.println("20 0"); // 3
            out.println("30 0"); // 4
            out.println("5 5"); // 5
            out.println("15 5"); // 6
            out.println("1 2"); // (0,0)-(10,0)
            out.println("2 3"); // (10,0)-(20,0)
            out.println("3 4"); // (20,0)-(30,0)
            out.println("1 5"); // (0,0)-(5,5)
            out.println("2 6"); // (10,0)-(15,5)
        }
    }

    /**
     * 小課題9のテストデータを生成するメソッド
     */
    private static void generateSubtask9Tests() throws IOException {
        // 小課題9: 統合されたシステムテスト
        // 制約: 2≤N≤200, 1≤M<N, 0≤x,y≤105, 0≤P≤100, 0≤Q≤100.
        // テストケース9.1: 追加地点と混合クエリの基本統合テスト
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_9_1_1.txt"))) {
            out.println("5 3 2 4"); // N=5, M=3, P=2, Q=4
            // 初期地点
            out.println("0 0");   // 1
            out.println("10 0");  // 2
            out.println("0 10");  // 3
            out.println("10 10"); // 4
            out.println("5 5");   // 5
            // 初期道路
            out.println("1 2"); // (0,0)-(10,0)
            out.println("3 4"); // (0,10)-(10,10)
            out.println("1 4"); // (0,0)-(10,10) -> (5,5)で3-2と交差
            // 追加地点
            out.println("5 0");   // P1 (線分1-2の中点)
            out.println("10 5");  // P2 (線分2-4の中点)
            // クエリ (Q=4)
            out.println("distance 1 2 1");   // Q1: 地点1から地点2へ (最短距離)
            out.println("distance 1 C1 1");  // Q2: 地点1から最初の交差地点C1へ
            out.println("detect_bridges"); // Q3: 幹線道路検出
            out.println("distance P1 P2 1"); // Q4: 追加地点P1からP2へ
        }

        // テストケース9.2: 複雑な交差と追加地点、K最短路を含むシナリオ
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_9_2_1.txt"))) {
            out.println("8 6 3 3"); // N=8, M=6, P=3, Q=3
            // 初期地点
            out.println("0 0");   // 1
            out.println("10 0");  // 2
            out.println("0 10");  // 3
            out.println("10 10"); // 4
            out.println("5 5");   // 5
            out.println("20 0");  // 6
            out.println("20 10"); // 7
            out.println("15 5");  // 8
            // 初期道路
            out.println("1 4"); // (0,0)-(10,10)
            out.println("2 3"); // (10,0)-(0,10) -> (5,5)で1-4と交差 (C1)
            out.println("5 6"); // (5,5)-(20,0) -> C1-6
            out.println("6 7"); // (20,0)-(20,10)
            out.println("7 8"); // (20,10)-(15,5)
            out.println("8 5"); // (15,5)-(5,5)
            // 追加地点
            out.println("1 1"); // P1 (地点1に近い)
            out.println("18 5"); // P2 (線分6-7に近い)
            out.println("5 1"); // P3 (線分1-2に近い)
            // クエリ
            out.println("distance 1 7 2");   // Q1: 地点1から地点7へ、K=2 (K最短路)
            out.println("distance P1 C1 1"); // Q2: 追加地点P1からC1へ
            out.println("detect_bridges"); // Q3: 幹線道路検出
        }
    }

    /**
     * 小課題10のテストデータを生成するメソッド
     */
    private static void generateSubtask10Tests() throws IOException {
        // 制約: 2≤N≤2×10^5, 1≤M≤10^5, 0≤x,y≤10^9, 0≤P≤1000, 0≤Q≤1000.
        // ただし、交差地点の数は10^5を超えない。すべての道は、縦に垂直な線分か横に水平な線分。
        
        // テストケース10.1.1: 大規模なグリッドだが交差地点は少ない
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_10_1_1.txt"))) {

            int N = 20000; // 点数 (N_MAXより小さめにする)
            int M = 10000;  // 道数 (M_MAXより小さめにする)
            int P = 100;
            int Q = 100;
            long maxCoord = 1_000_000_000L; // 10^9

            out.println(N + " " + M + " " + P + " " + Q);

            // 地点座標の生成 (N点)
            // 水平/垂直な道を生成しやすくするため、グリッド状に点を配置するが、
            // 全てのグリッド点を生成するとNが大きくなりすぎるため、間引きつつ配置する。
            // また、純粋なグリッドだと交差点が多すぎる場合があるため、ランダム性も加える。
            List<Point> points = new ArrayList<>();
            // N点がグリッド状に配置されるように調整 (例えば sqrt(N) x sqrt(N) グリッド)
            int num_x = (int) Math.sqrt(N);
            int num_y = N / num_x;
            long step_x = maxCoord / num_x;
            long step_y = maxCoord / num_y;

            for (int j = 0; j < num_y; j++) {
                for (int i = 0; i < num_x; i++) {
                    long x = i * step_x + random.nextInt(100); // 少しノイズを加える
                    long y = j * step_y + random.nextInt(100);
                    x = Math.max(0, Math.min(maxCoord, x));
                    y = Math.max(0, Math.min(maxCoord, y));
                    points.add(new Point(x, y));
                    out.printf("%d %d%n", x, y);
                    if (points.size() == N) break; // N点生成したら終了
                }
                if (points.size() == N) break;
            }
            // N点がnum_x * num_yで埋まらない場合、残りはランダムに生成
            while (points.size() < N) {
                long x = random.nextLong(maxCoord + 1);
                long y = random.nextLong(maxCoord + 1);
                points.add(new Point(x, y));
                out.printf("%d %d%n", x, y);
            }
            
            // 道の定義の生成 (M個)
            // 水平・垂直線のみという制約を満たすように道を生成
            Set<String> existingRoads = new HashSet<>();
            int roadsGenerated = 0;
            while (roadsGenerated < M) {
                int uIdx = random.nextInt(N);
                int vIdx = random.nextInt(N);

                if (uIdx == vIdx) continue;

                Point p1 = points.get(uIdx);
                Point p2 = points.get(vIdx);

                // ランダムに水平線または垂直線を選ぶ
                if (random.nextBoolean()) { // 水平線に変換
                    p2 = new Point(p2.x, p1.y); // p2のY座標をp1のY座標に揃える
                } else { // 垂直線に変換
                    p2 = new Point(p1.x, p2.y); // p2のX座標をp1のX座標に揃える
                }
                
                // 変換後のp2が既存の点にあるかを探す
                int finalVIdx = -1;
                for(int k=0; k<N; k++) {
                    if(points.get(k).equals(p2)) {
                        finalVIdx = k;
                        break;
                    }
                }
                // 見つからなければ、元のvIdxのまま使う（結果的に水平/垂直にならない場合もあるが、ランダム性を保持）
                if (finalVIdx == -1) finalVIdx = vIdx;

                // 同じ点同士の道や、既に存在する道は避ける
                String roadKey = Math.min(uIdx, finalVIdx) + "-" + Math.max(uIdx, finalVIdx);
                if (uIdx == finalVIdx || existingRoads.contains(roadKey)) continue;

                out.printf("%d %d%n", uIdx + 1, finalVIdx + 1); // 1-indexedで出力
                existingRoads.add(roadKey);
                roadsGenerated++;
            }


            // P個の追加地点
            for (int i = 0; i < P; i++) {
                out.printf("%d %d%n", random.nextLong(maxCoord + 1), random.nextLong(maxCoord + 1));
            }

            // Q個のクエリ
            for (int i = 0; i < Q; i++) {
                generateRandomQuery(N, P, out);
            }
        }

        // テストケース10.2.1: 少ない交差地点だが、非常に長い道路があるケース (小課題10の制約)
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_10_2_1.txt"))) {
            int N = 2000; 
            int M = 1999; 
            int P = 0;
            int Q = 5;
            long maxX = 1_000_000_000L; 

            out.printf("%d %d %d %d%n", N, M, P, Q);

            // ほとんどが一直線に並んだ点（Y=0の水平線）
            for (int i = 0; i < N; i++) {
                out.printf("%d %d%n", (long)i * (maxX / (N - 1)), 0L); // Y=0の直線上に均等配置
            }

            // 連続する点を結ぶ道（水平線）
            for (int i = 0; i < N - 1; i++) {
                out.printf("%d %d%n", (i + 1), (i + 2));
            }

            // わずかな交差地点を意図的に作成するための分岐 (垂直線)
            long centralX = (long)(N / 2) * (maxX / (N - 1));
            int branchPointIdx = N / 2; // 中央のノード (1-indexed)
            
            // 新しい点 (Y座標が異なる)
            long branchY = random.nextLong(maxX / 10) + 1;
            out.printf("%d %d%n", centralX, branchY); // N+1 番目の点として追加

            // 中央のノードからこの新しい点へ垂直な道を作成
            out.printf("%d %d%n", (branchPointIdx + 1), (N + 1));
            M++; // 道の数を増やす

            // Q個のクエリ (端点から端点へ、途中の大規模な直線を通る)
            out.printf("distance %d %d %d%n", 1, N, 1); // 最長パス
            out.printf("distance %d %d %d%n", 1, (N / 2), 1); // 短いパス
            out.printf("distance %d %d %d%n", (N / 4), (N * 3 / 4), 1); // 中間のパス
            out.printf("distance %d %d %d%n", 1, N, 5); // K-最短路も試す
            out.println("detect_bridges"); // 幹線道路も試す
        }
    }

    /**
     * 小課題11: 一般的な大規模データ
     */
    private static void generateSubtask11Tests() throws IOException {
        // 制約: 2≤N≤2×10^5, 1≤M≤10^5, 0≤x,y≤10^9, 0≤P≤1000, 0≤Q≤1000.
        // ただし、交差地点の数は10^5を超えない。

        // テストケース11.1.1: 多くの交差地点が発生するランダムなデータ
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_11_1_1.txt"))) {
            int N = 20000; // 点数 (N_MAXより小さめにする)
            int M = 10000;  // 道数 (M_MAXより小さめにする)
            int P = 100;
            int Q = 100;
            long maxCoord = 1_000_000_000L; // 10^9

            out.printf("%d %d %d %d%n", N, M, P, Q);

            // ランダムな点を生成
            for (int i = 0; i < N; i++) {
                out.printf("%d %d%n", random.nextLong(maxCoord + 1), random.nextLong(maxCoord + 1));
            }

            // ランダムな道を生成 (重複を避ける)
            Set<String> existingRoads = new HashSet<>();
            int roadsGenerated = 0;
            while (roadsGenerated < M) {
                int u = random.nextInt(N);
                int v = random.nextInt(N);
                if (u == v) continue;
                String roadKey = Math.min(u, v) + "-" + Math.max(u, v);
                if (existingRoads.contains(roadKey)) continue;

                out.printf("%d %d%n", u + 1, v + 1); // 1-indexedで出力
                existingRoads.add(roadKey);
                roadsGenerated++;
            }

            // P個の追加地点
            for (int i = 0; i < P; i++) {
                out.printf("%d %d%n", random.nextLong(maxCoord + 1), random.nextLong(maxCoord + 1));
            }

            // Q個のクエリ
            for (int i = 0; i < Q; i++) {
                generateRandomQuery(N, P, out);
            }
        }

        // テストケース11.2.1: X字型が密集するパターン (交差地点が非常に多くなる可能性)
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DIR + "/input_11_2_1.txt"))) {
            int N_base = 1000; // X字型のグループ数 (N_base * 4点が生成される)
            int N = N_base * 4; // 各X字型に4点
            int M = N_base * 2; // 各X字型に2本の道 (対角線)
            int P = 0;
            int Q = 10;
            long maxCoord = 1_000_000_000L;

            out.printf("%d %d %d %d%n", N, M, P, Q);

            // X字型の中心間隔を計算
            long spacing = maxCoord / (N_base + 1);
            if (spacing < 1000) spacing = 1000; // 最小間隔を確保

            int currentPointId = 1;
            for (int i = 0; i < N_base; i++) {
                // 各X字型の中心座標 (オーバーフローしないように注意)
                long centerX = (long)i * spacing + random.nextInt(100) - 50;
                long centerY = random.nextLong(maxCoord - 200) + 100; // Yはランダムだが交差範囲は狭い

                // 4つの端点 (中心から+-50の範囲でノイズ)
                long x1 = centerX - 50 + random.nextInt(20) - 10; long y1 = centerY - 50 + random.nextInt(20) - 10;
                long x2 = centerX + 50 + random.nextInt(20) - 10; long y2 = centerY + 50 + random.nextInt(20) - 10;
                long x3 = centerX - 50 + random.nextInt(20) - 10; long y3 = centerY + 50 + random.nextInt(20) - 10;
                long x4 = centerX + 50 + random.nextInt(20) - 10; long y4 = centerY - 50 + random.nextInt(20) - 10;

                // 座標が範囲内に収まるように調整
                x1 = Math.max(0, Math.min(maxCoord, x1)); y1 = Math.max(0, Math.min(maxCoord, y1));
                x2 = Math.max(0, Math.min(maxCoord, x2)); y2 = Math.max(0, Math.min(maxCoord, y2));
                x3 = Math.max(0, Math.min(maxCoord, x3)); y3 = Math.max(0, Math.min(maxCoord, y3));
                x4 = Math.max(0, Math.min(maxCoord, x4)); y4 = Math.max(0, Math.min(maxCoord, y4));

                out.printf("%d %d%n", x1, y1); // p1
                out.printf("%d %d%n", x2, y2); // p2
                out.printf("%d %d%n", x3, y3); // p3
                out.printf("%d %d%n", x4, y4); // p4

                // 2本の対角線
                out.printf("%d %d%n", currentPointId, currentPointId + 1);
                out.printf("%d %d%n", currentPointId + 2, currentPointId + 3);
                currentPointId += 4;
            }

            // クエリはランダムに選択 (NはPを含まないのでN+Pにしない)
            for (int i = 0; i < Q; i++) {
                generateRandomQuery(N, P, out); // P=0なのでP地点は選ばれない
            }
        }
    }

    // --- 汎用的なクエリ生成メソッド ---
    private static void generateRandomQuery(int N, int P, PrintWriter out) {
        int queryType = random.nextInt(3); // 0:distance (N), 1:distance (C/P), 2:detect_bridges

        if (queryType == 0 || queryType == 1) { // distance クエリ
            String startNode, endNode;

            // 始点の種類を選択 (通常の地点, 交差点, P地点)
            int startNodeType = random.nextInt(3); 
            if (startNodeType == 0) { // 通常の地点
                startNode = String.valueOf(random.nextInt(N) + 1);
            } else if (startNodeType == 1 && P > 0) { // P地点 (P > 0 の場合のみP地点を選択可能にする)
                startNode = "P" + (random.nextInt(P) + 1);
            } else { // 交差点 (C)
                // 交差点の数は問題の制約で10^5までなので、その範囲でランダムに生成
                startNode = "C" + (random.nextInt(100000) + 1); 
            }

            // 終点の種類を選択
            int endNodeType = random.nextInt(3);
            if (endNodeType == 0) { // 通常の地点
                endNode = String.valueOf(random.nextInt(N) + 1);
            } else if (endNodeType == 1 && P > 0) { // P地点
                endNode = "P" + (random.nextInt(P) + 1);
            } else { // 交差点 (C)
                endNode = "C" + (random.nextInt(100000) + 1);
            }
            
            int k = random.nextInt(5) + 1; // kは1から5まで

            out.printf("distance %s %s %d%n", startNode, endNode, k);

        } else { // detect_bridges クエリ
            out.println("detect_bridges");
        }
    }
}