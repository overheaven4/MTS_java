package org.example;

/**
 * Параметризованный интерфейс списка.
 *
 * @param <E> тип элементов списка
 */
public interface CustomList<E> extends Iterable<E> {

    /**
     * Добавляет элемент в конец списка.
     *
     * @param element добавляемый элемент
     */
    void add(E element);

    /**
     * Возвращает элемент по индексу.
     *
     * @param index индекс элемента
     * @return элемент списка
     */
    E get(int index);

    /**
     * Удаляет и возвращает элемент по индексу.
     *
     * @param index индекс удаляемого элемента
     * @return удалённый элемент
     */
    E remove(int index);

    /**
     * Возвращает количество элементов в списке.
     *
     * @return размер списка
     */
    int size();

    /**
     * Проверяет, пуст ли список.
     *
     * @return true, если список пуст, иначе false
     */
    boolean isEmpty();
}