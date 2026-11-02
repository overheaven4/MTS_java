package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentRegistryTest {

    @Test
    void studentCreationAndValidation() {
        Student student = new Student(1, "Ivan", 8.5);
        assertEquals(1, student.getId());
        assertEquals("Ivan", student.getName());
        assertEquals(8.5, student.getGrade());

        assertThrows(IllegalArgumentException.class, () -> new Student(0, "Ivan", 8.5));
        assertThrows(IllegalArgumentException.class, () -> new Student(-1, "Ivan", 8.5));
        assertThrows(IllegalArgumentException.class, () -> new Student(1, null, 8.5));
        assertThrows(IllegalArgumentException.class, () -> new Student(1, "   ", 8.5));
        assertThrows(IllegalArgumentException.class, () -> new Student(1, "Ivan", -0.1));
        assertThrows(IllegalArgumentException.class, () -> new Student(1, "Ivan", 10.1));
    }

    @Test
    void studentEqualsAndHashCode() {
        Student s1 = new Student(1, "Ivan", 8.5);
        Student s2 = new Student(1, "Ivan", 8.5);
        Student s3 = new Student(2, "Petr", 8.5);

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
        assertNotEquals(s1, s3);
    }

    @Test
    void findStudentsByGradeRangeNormalAndBoundaryCases() {
        StudentRegistry registry = new StudentRegistry();
        List<Student> list = new ArrayList<>();
        Student s1 = new Student(1, "A", 0.0);
        Student s2 = new Student(2, "B", 7.0);
        Student s3 = new Student(3, "C", 8.5);
        Student s4 = new Student(4, "D", 10.0);
        list.add(s1);
        list.add(s2);
        list.add(s3);
        list.add(s4);

        HashMap<Integer, Student> map = registry.createHashMap(list);

        List<Student> found = registry.findStudentsByGradeRange(map, 7.0, 10.0);
        assertEquals(3, found.size());
        assertTrue(found.contains(s2));
        assertTrue(found.contains(s3));
        assertTrue(found.contains(s4));
        assertEquals(4, map.size());
    }

    @Test
    void findStudentsByGradeRangeEmptyMapAndInvalidArguments() {
        StudentRegistry registry = new StudentRegistry();
        HashMap<Integer, Student> emptyMap = new HashMap<>();

        List<Student> found = registry.findStudentsByGradeRange(emptyMap, 5.0, 8.0);
        assertTrue(found.isEmpty());

        assertThrows(IllegalArgumentException.class,
                () -> registry.findStudentsByGradeRange(emptyMap, 9.0, 5.0));
        assertThrows(IllegalArgumentException.class,
                () -> registry.findStudentsByGradeRange(emptyMap, -1.0, 5.0));
        assertThrows(IllegalArgumentException.class,
                () -> registry.findStudentsByGradeRange(emptyMap, 5.0, 11.0));
        assertThrows(IllegalArgumentException.class,
                () -> registry.findStudentsByGradeRange(null, 5.0, 8.0));
    }

    @Test
    void getTopNStudentsByIdNormalAndBoundaryCases() {
        StudentRegistry registry = new StudentRegistry();
        List<Student> list = new ArrayList<>();
        Student s1 = new Student(10, "A", 5.0);
        Student s2 = new Student(20, "B", 6.0);
        Student s3 = new Student(30, "C", 7.0);
        list.add(s1);
        list.add(s2);
        list.add(s3);

        TreeMap<Integer, Student> treeMap = registry.createDescendingTreeMap(list);

        List<Student> topTwo = registry.getTopNStudentsById(treeMap, 2);
        assertEquals(2, topTwo.size());
        assertEquals(s3, topTwo.get(0));
        assertEquals(s2, topTwo.get(1));

        List<Student> topZero = registry.getTopNStudentsById(treeMap, 0);
        assertTrue(topZero.isEmpty());

        List<Student> topMoreThanSize = registry.getTopNStudentsById(treeMap, 10);
        assertEquals(3, topMoreThanSize.size());
        assertEquals(s3, topMoreThanSize.get(0));
        assertEquals(s2, topMoreThanSize.get(1));
        assertEquals(s1, topMoreThanSize.get(2));
        assertEquals(3, treeMap.size());
    }

    @Test
    void getTopNStudentsByIdEmptyMapAndInvalidArguments() {
        StudentRegistry registry = new StudentRegistry();
        NavigableMap<Integer, Student> emptyMap = registry.createDescendingTreeMap(new ArrayList<>());

        List<Student> result = registry.getTopNStudentsById(emptyMap, 5);
        assertTrue(result.isEmpty());

        assertThrows(IllegalArgumentException.class,
                () -> registry.getTopNStudentsById(emptyMap, -1));
        assertThrows(IllegalArgumentException.class,
                () -> registry.getTopNStudentsById(null, 2));
    }
}