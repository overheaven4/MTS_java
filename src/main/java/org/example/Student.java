package org.example;

import java.util.Objects;

/**
 * Неизменяемая модель студента.
 */
public final class Student {

    private final int id;
    private final String name;
    private final double grade;

    /**
     * Создаёт объект студента с проверкой входных данных.
     *
     * @param id    положительный идентификатор
     * @param name  непустое имя
     * @param grade оценка от 0.0 до 10.0 включительно
     */
    public Student(int id, String name, double grade) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id must be positive");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (Double.isNaN(grade) || grade < 0.0 || grade > 10.0) {
            throw new IllegalArgumentException("Grade must be between 0.0 and 10.0");
        }
        this.id = id;
        this.name = name;
        this.grade = grade;
    }

    /**
     * Возвращает идентификатор студента.
     *
     * @return идентификатор студента
     */
    public int getId() {
        return id;
    }

    /**
     * Возвращает имя студента.
     *
     * @return имя студента
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает оценку студента.
     *
     * @return оценка студента
     */
    public double getGrade() {
        return grade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Student student = (Student) o;
        return id == student.id
                && Double.compare(student.grade, grade) == 0
                && Objects.equals(name, student.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, grade);
    }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', grade=" + grade + "}";
    }
}