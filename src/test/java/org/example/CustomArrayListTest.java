package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomArrayListTest {

    @Test
    void newListIsEmptyAndSizeIsZero() {
        CustomList<String> list = new CustomArrayList<>();

        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void addElementsPreservesOrder() {
        CustomList<String> list = new CustomArrayList<>();

        list.add("A");
        assertEquals(1, list.size());
        assertFalse(list.isEmpty());
        assertEquals("A", list.get(0));

        list.add("B");
        list.add("C");
        assertEquals(3, list.size());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));
    }

    @Test
    void addMoreThanTenElementsExpandsArrayWithoutDataLoss() {
        CustomList<Integer> list = new CustomArrayList<>();

        for (int i = 0; i < 20; i++) {
            list.add(i);
        }

        assertEquals(20, list.size());
        for (int i = 0; i < 20; i++) {
            assertEquals(i, list.get(i));
        }
    }

    @Test
    void getWorksForFirstMiddleAndLastIndex() {
        CustomList<String> list = new CustomArrayList<>();
        list.add("first");
        list.add("second");
        list.add("middle");
        list.add("fourth");
        list.add("last");

        assertEquals("first", list.get(0));
        assertEquals("middle", list.get(2));
        assertEquals("last", list.get(4));
    }

    @ParameterizedTest
    @ValueSource(ints = {-2, -1, 3, 5})
    void getAndRemoveThrowExceptionForInvalidIndex(int invalidIndex) {
        CustomList<String> list = new CustomArrayList<>();
        list.add("A");
        list.add("B");
        list.add("C");

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(invalidIndex));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(invalidIndex));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 4})
    void removeFirstMiddleAndLastElementReturnsCorrectValue(int indexToRemove) {
        CustomList<String> list = new CustomArrayList<>();
        list.add("item0");
        list.add("item1");
        list.add("item2");
        list.add("item3");
        list.add("item4");

        String expected = "item" + indexToRemove;
        String removed = list.remove(indexToRemove);

        assertEquals(expected, removed);
        assertEquals(4, list.size());
    }

    @Test
    void addNullThrowsNullPointerException() {
        CustomList<String> list = new CustomArrayList<>();

        assertThrows(NullPointerException.class, () -> list.add(null));
    }

    @Test
    void iteratorTraversesElementsInCorrectOrder() {
        CustomList<Integer> list = new CustomArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);

        List<Integer> actual = new ArrayList<>();
        for (Integer value : list) {
            actual.add(value);
        }

        assertEquals(3, actual.size());
        assertEquals(1, actual.get(0));
        assertEquals(2, actual.get(1));
        assertEquals(3, actual.get(2));
    }

    @Test
    void iteratorNextThrowsNoSuchElementExceptionWhenFinished() {
        CustomList<String> list = new CustomArrayList<>();
        list.add("only");

        Iterator<String> iterator = list.iterator();
        assertTrue(iterator.hasNext());
        assertEquals("only", iterator.next());
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }
}