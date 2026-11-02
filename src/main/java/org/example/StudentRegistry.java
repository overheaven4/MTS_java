package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Реестр студентов и методы выборки данных.
 */
public class StudentRegistry {

    /**
     * Создаёт HashMap студентов, где ключом является id студента.
     *
     * @param students список студентов
     * @return заполненная карта HashMap
     */
    public HashMap<Integer, Student> createHashMap(List<Student> students) {
        if (students == null) {
            throw new IllegalArgumentException("Students list cannot be null");
        }
        HashMap<Integer, Student> map = new HashMap<>();
        for (Student student : students) {
            if (student == null) {
                throw new IllegalArgumentException("Student cannot be null");
            }
            map.put(student.getId(), student);
        }
        return map;
    }

    /**
     * Создаёт TreeMap студентов с сортировкой ключей по убыванию.
     *
     * @param students список студентов
     * @return заполненная карта TreeMap с обратным порядком ключей
     */
    public TreeMap<Integer, Student> createDescendingTreeMap(List<Student> students) {
        if (students == null) {
            throw new IllegalArgumentException("Students list cannot be null");
        }
        TreeMap<Integer, Student> map = new TreeMap<>(Collections.reverseOrder());
        for (Student student : students) {
            if (student == null) {
                throw new IllegalArgumentException("Student cannot be null");
            }
            map.put(student.getId(), student);
        }
        return map;
    }

    /**
     * Находит студентов с оценкой в диапазоне [minGrade, maxGrade].
     *
     * @param students карта студентов
     * @param minGrade минимальная оценка
     * @param maxGrade максимальная оценка
     * @return новый список ArrayList с найденными студентами
     */
    public List<Student> findStudentsByGradeRange(
            Map<Integer, Student> students,
            double minGrade,
            double maxGrade) {
        if (students == null) {
            throw new IllegalArgumentException("Map cannot be null");
        }
        if (Double.isNaN(minGrade) || Double.isNaN(maxGrade)) {
            throw new IllegalArgumentException("Grades cannot be NaN");
        }
        if (minGrade < 0.0 || maxGrade > 10.0 || minGrade > maxGrade) {
            throw new IllegalArgumentException("Invalid grade range");
        }

        List<Student> result = new ArrayList<>();
        for (Student student : students.values()) {
            if (student.getGrade() >= minGrade && student.getGrade() <= maxGrade) {
                result.add(student);
            }
        }
        return result;
    }

    /**
     * Возвращает первые n студентов из карты, отсортированной по убыванию id.
     *
     * @param students карта с сортировкой ключей по убыванию
     * @param n        количество студентов
     * @return новый список ArrayList с первыми n студентами
     */
    public List<Student> getTopNStudentsById(
            NavigableMap<Integer, Student> students,
            int n) {
        if (students == null) {
            throw new IllegalArgumentException("Map cannot be null");
        }
        if (n < 0) {
            throw new IllegalArgumentException("n cannot be negative");
        }

        List<Student> result = new ArrayList<>();
        if (n == 0) {
            return result;
        }

        int count = 0;
        for (Student student : students.values()) {
            if (count == n) {
                break;
            }
            result.add(student);
            count++;
        }
        return result;
    }
}