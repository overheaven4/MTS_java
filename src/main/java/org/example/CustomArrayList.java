package org.example;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Динамический массив на основе внутреннего массива Object[].
 *
 * @param <E> тип элементов списка
 */
public class CustomArrayList<E> implements CustomList<E> {

    private static final int DEFAULT_CAPACITY = 10;

    private Object[] elements;
    private int size;

    /**
     * Создаёт пустой список с начальной ёмкостью 10.
     */
    public CustomArrayList() {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    /**
     * Создаёт пустой список с указанной начальной ёмкостью.
     *
     * @param initialCapacity начальная ёмкость массива
     */
    public CustomArrayList(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        this.elements = new Object[initialCapacity];
        this.size = 0;
    }

    /**
     * Добавляет элемент в конец списка.
     *
     * @param element добавляемый элемент
     */
    @Override
    public void add(E element) {
        if (element == null) {
            throw new NullPointerException("Element cannot be null");
        }
        if (size == elements.length) {
            int newCapacity = (int) (elements.length * 1.5);
            if (newCapacity == elements.length) {
                newCapacity = elements.length + 1;
            }
            elements = Arrays.copyOf(elements, newCapacity);
        }
        elements[size] = element;
        size++;
    }

    /**
     * Возвращает элемент по индексу.
     *
     * @param index индекс элемента
     * @return элемент по указанному индексу
     */
    @Override
    public E get(int index) {
        checkIndex(index);
        return (E) elements[index];
    }

    /**
     * Удаляет элемент по индексу и сдвигает хвост массива влево.
     *
     * @param index индекс удаляемого элемента
     * @return удалённый элемент
     */
    @Override
    public E remove(int index) {
        checkIndex(index);
        E removed = (E) elements[index];
        int elementsMoved = size - index - 1;
        if (elementsMoved > 0) {
            System.arraycopy(elements, index + 1, elements, index, elementsMoved);
        }
        size--;
        elements[size] = null;
        return removed;
    }

    /**
     * Возвращает текущий размер списка.
     *
     * @return количество элементов
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Проверяет, пуст ли список.
     *
     * @return true, если размер равен 0
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Возвращает итератор для последовательного обхода списка.
     *
     * @return итератор элементов
     */
    @Override
    public Iterator<E> iterator() {
        return new CustomIterator();
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    private class CustomIterator implements Iterator<E> {

        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < size;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            E element = (E) elements[currentIndex];
            currentIndex++;
            return element;
        }
    }
}