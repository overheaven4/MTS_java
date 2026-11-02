package org.example;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Класс для сравнения производительности ArrayList и LinkedList.
 */
public class CollectionPerformanceExperiment {

    private static final int ELEMENTS_COUNT = 10_000;
    private static final int WARMUP_RUNS = 2;
    private static final int MEASURE_RUNS = 5;

    private static long checksum = 0;

    /**
     * Запускает эксперимент и выводит таблицу с результатами и выводами.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        long arrayAddEnd = measureAddEnd(true);
        long linkedAddEnd = measureAddEnd(false);

        long arrayAddStart = measureAddStart(true);
        long linkedAddStart = measureAddStart(false);

        long arrayInsertMiddle = measureInsertMiddle(true);
        long linkedInsertMiddle = measureInsertMiddle(false);

        long arrayGet = measureSequentialGet(true);
        long linkedGet = measureSequentialGet(false);

        long arrayRemoveStart = measureRemoveStart(true);
        long linkedRemoveStart = measureRemoveStart(false);

        long arrayRemoveEnd = measureRemoveEnd(true);
        long linkedRemoveEnd = measureRemoveEnd(false);

        System.out.println("Операция | Количество | ArrayList (нс) | LinkedList (нс)");
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Добавление в конец | " + ELEMENTS_COUNT + " | " + arrayAddEnd + " | " + linkedAddEnd);
        System.out.println("Добавление в начало | " + ELEMENTS_COUNT + " | " + arrayAddStart + " | " + linkedAddStart);
        System.out.println("Вставка в середину | " + ELEMENTS_COUNT + " | " + arrayInsertMiddle + " | " + linkedInsertMiddle);
        System.out.println("Доступ по индексу (get) | " + ELEMENTS_COUNT + " | " + arrayGet + " | " + linkedGet);
        System.out.println("Удаление из начала | " + ELEMENTS_COUNT + " | " + arrayRemoveStart + " | " + linkedRemoveStart);
        System.out.println("Удаление из конца | " + ELEMENTS_COUNT + " | " + arrayRemoveEnd + " | " + linkedRemoveEnd);
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Контрольная сумма: " + checksum);
        System.out.println();
        System.out.println("Выводы:");
        System.out.println("1. Добавление в конец у обеих коллекций работает за O(1), но ArrayList быстрее из-за хорошей локальности данных в массиве и отсутствия создания объектов узлов Node.");
        System.out.println("2. Добавление в начало и удаление из начала у LinkedList работают за O(1) за счет смены ссылок первого узла.");
        System.out.println("3. В ArrayList вставка и удаление из начала требуют сдвига всех элементов массива вправо или влево, поэтому каждая такая операция стоит O(n).");
        System.out.println("4. При вставке в середину обе коллекции тратят O(n) на операцию: ArrayList сдвигает половину массива в памяти, а LinkedList идет по ссылкам до середины списка, что медленнее из-за промахов кэша процессора.");
        System.out.println("5. Последовательный доступ по индексу get(i) в ArrayList выполняется за O(1), а в LinkedList каждый вызов идет по узлам с начала или конца за O(n), превращая цикл в O(n^2).");
        System.out.println("6. Удаление из конца в обоих списках работает за O(1): ArrayList просто уменьшает размер без сдвига элементов, а LinkedList меняет ссылку на последний узел.");
    }

    private static List<Integer> createEmptyList(boolean isArrayList) {
        if (isArrayList) {
            return new ArrayList<>();
        }
        return new LinkedList<>();
    }

    private static List<Integer> createFilledList(boolean isArrayList) {
        List<Integer> list = createEmptyList(isArrayList);
        for (int i = 0; i < ELEMENTS_COUNT; i++) {
            list.add(i);
        }
        return list;
    }

    private static long measureAddEnd(boolean isArrayList) {
        for (int r = 0; r < WARMUP_RUNS; r++) {
            runAddEnd(isArrayList);
        }
        long totalTime = 0;
        for (int r = 0; r < MEASURE_RUNS; r++) {
            totalTime += runAddEnd(isArrayList);
        }
        return totalTime / MEASURE_RUNS;
    }

    private static long runAddEnd(boolean isArrayList) {
        List<Integer> list = createEmptyList(isArrayList);
        long start = System.nanoTime();
        for (int i = 0; i < ELEMENTS_COUNT; i++) {
            list.add(i);
        }
        long time = System.nanoTime() - start;
        checksum += list.size();
        return time;
    }

    private static long measureAddStart(boolean isArrayList) {
        for (int r = 0; r < WARMUP_RUNS; r++) {
            runAddStart(isArrayList);
        }
        long totalTime = 0;
        for (int r = 0; r < MEASURE_RUNS; r++) {
            totalTime += runAddStart(isArrayList);
        }
        return totalTime / MEASURE_RUNS;
    }

    private static long runAddStart(boolean isArrayList) {
        List<Integer> list = createEmptyList(isArrayList);
        long start = System.nanoTime();
        for (int i = 0; i < ELEMENTS_COUNT; i++) {
            list.add(0, i);
        }
        long time = System.nanoTime() - start;
        checksum += list.size();
        return time;
    }

    private static long measureInsertMiddle(boolean isArrayList) {
        for (int r = 0; r < WARMUP_RUNS; r++) {
            runInsertMiddle(isArrayList);
        }
        long totalTime = 0;
        for (int r = 0; r < MEASURE_RUNS; r++) {
            totalTime += runInsertMiddle(isArrayList);
        }
        return totalTime / MEASURE_RUNS;
    }

    private static long runInsertMiddle(boolean isArrayList) {
        List<Integer> list = createEmptyList(isArrayList);
        long start = System.nanoTime();
        for (int i = 0; i < ELEMENTS_COUNT; i++) {
            list.add(list.size() / 2, i);
        }
        long time = System.nanoTime() - start;
        checksum += list.size();
        return time;
    }

    private static long measureSequentialGet(boolean isArrayList) {
        for (int r = 0; r < WARMUP_RUNS; r++) {
            runSequentialGet(isArrayList);
        }
        long totalTime = 0;
        for (int r = 0; r < MEASURE_RUNS; r++) {
            totalTime += runSequentialGet(isArrayList);
        }
        return totalTime / MEASURE_RUNS;
    }

    private static long runSequentialGet(boolean isArrayList) {
        List<Integer> list = createFilledList(isArrayList);
        long sum = 0;
        long start = System.nanoTime();
        for (int i = 0; i < ELEMENTS_COUNT; i++) {
            sum += list.get(i);
        }
        long time = System.nanoTime() - start;
        checksum += sum;
        return time;
    }

    private static long measureRemoveStart(boolean isArrayList) {
        for (int r = 0; r < WARMUP_RUNS; r++) {
            runRemoveStart(isArrayList);
        }
        long totalTime = 0;
        for (int r = 0; r < MEASURE_RUNS; r++) {
            totalTime += runRemoveStart(isArrayList);
        }
        return totalTime / MEASURE_RUNS;
    }

    private static long runRemoveStart(boolean isArrayList) {
        List<Integer> list = createFilledList(isArrayList);
        long sum = 0;
        long start = System.nanoTime();
        for (int i = 0; i < ELEMENTS_COUNT; i++) {
            sum += list.remove(0);
        }
        long time = System.nanoTime() - start;
        checksum += sum;
        return time;
    }

    private static long measureRemoveEnd(boolean isArrayList) {
        for (int r = 0; r < WARMUP_RUNS; r++) {
            runRemoveEnd(isArrayList);
        }
        long totalTime = 0;
        for (int r = 0; r < MEASURE_RUNS; r++) {
            totalTime += runRemoveEnd(isArrayList);
        }
        return totalTime / MEASURE_RUNS;
    }

    private static long runRemoveEnd(boolean isArrayList) {
        List<Integer> list = createFilledList(isArrayList);
        long sum = 0;
        long start = System.nanoTime();
        for (int i = 0; i < ELEMENTS_COUNT; i++) {
            sum += list.remove(list.size() - 1);
        }
        long time = System.nanoTime() - start;
        checksum += sum;
        return time;
    }
}