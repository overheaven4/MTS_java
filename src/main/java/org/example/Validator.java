package org.example;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public final class Validator {

    private Validator() {
    }

    public static ValidationResult validate(Object object) {
        if (object == null) {
            throw new NullPointerException("Validated object cannot be null");
        }

        List<ValidationError> errors = new ArrayList<>();
        Class<?> currentClass = object.getClass();

        while (currentClass != null && currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                    continue;
                }
                validateField(object, currentClass, field, errors);
            }
            currentClass = currentClass.getSuperclass();
        }

        return new ValidationResult(errors);
    }

    private static void validateField(
            Object object,
            Class<?> declaringClass,
            Field field,
            List<ValidationError> errors) {

        boolean hasConstraint = field.isAnnotationPresent(NotNull.class)
                || field.isAnnotationPresent(Size.class)
                || field.isAnnotationPresent(Range.class)
                || field.isAnnotationPresent(Email.class);

        if (!field.trySetAccessible()) {
            throw new ValidationConfigurationException(
                    "Cannot access field '" + field.getName() + "' in class " + declaringClass.getName());
        }

        Object value;
        try {
            value = field.get(object);
        } catch (IllegalAccessException e) {
            throw new ValidationConfigurationException(
                    "Cannot read field '" + field.getName() + "' in class " + declaringClass.getName(), e);
        }

        if (!hasConstraint) {
            return;
        }

        if (field.isAnnotationPresent(NotNull.class)) {
            NotNull annotation = field.getAnnotation(NotNull.class);
            checkNotNull(field, value, annotation, errors);
        }

        if (field.isAnnotationPresent(Size.class)) {
            Size annotation = field.getAnnotation(Size.class);
            checkSize(field, value, annotation, errors);
        }

        if (field.isAnnotationPresent(Range.class)) {
            Range annotation = field.getAnnotation(Range.class);
            checkRange(field, value, annotation, errors);
        }

        if (field.isAnnotationPresent(Email.class)) {
            Email annotation = field.getAnnotation(Email.class);
            checkEmail(field, value, annotation, errors);
        }
    }

    private static void checkNotNull(
            Field field,
            Object value,
            NotNull annotation,
            List<ValidationError> errors) {
        if (!field.getType().isPrimitive() && value == null) {
            errors.add(new ValidationError(
                    field.getName(),
                    NotNull.class.getSimpleName(),
                    annotation.message()));
        }
    }

    private static void checkSize(
            Field field,
            Object value,
            Size annotation,
            List<ValidationError> errors) {
        if (!CharSequence.class.isAssignableFrom(field.getType())) {
            throw new ValidationConfigurationException(
                    "@Size is not supported on field '" + field.getName()
                            + "' of type " + field.getType().getName());
        }

        int min = annotation.min();
        int max = annotation.max();
        if (min < 0 || min > max) {
            throw new ValidationConfigurationException(
                    "Invalid @Size bounds on field '" + field.getName() + "'");
        }

        if (value == null) {
            return;
        }

        CharSequence sequence = (CharSequence) value;
        int length = sequence.length();
        if (length < min || length > max) {
            errors.add(new ValidationError(
                    field.getName(),
                    Size.class.getSimpleName(),
                    annotation.message()));
        }
    }

    private static void checkRange(
            Field field,
            Object value,
            Range annotation,
            List<ValidationError> errors) {
        if (!isSupportedNumericType(field.getType())) {
            throw new ValidationConfigurationException(
                    "@Range is not supported on field '" + field.getName()
                            + "' of type " + field.getType().getName());
        }

        double min = annotation.min();
        double max = annotation.max();
        if (Double.isNaN(min) || Double.isNaN(max)
                || Double.isInfinite(min) || Double.isInfinite(max)
                || min > max) {
            throw new ValidationConfigurationException(
                    "Invalid @Range bounds on field '" + field.getName() + "'");
        }

        if (value == null) {
            return;
        }

        Number number = (Number) value;
        double doubleValue = number.doubleValue();
        if (Double.isNaN(doubleValue) || doubleValue < min || doubleValue > max) {
            errors.add(new ValidationError(
                    field.getName(),
                    Range.class.getSimpleName(),
                    annotation.message()));
        }
    }

    private static void checkEmail(
            Field field,
            Object value,
            Email annotation,
            List<ValidationError> errors) {
        if (!CharSequence.class.isAssignableFrom(field.getType())) {
            throw new ValidationConfigurationException(
                    "@Email is not supported on field '" + field.getName()
                            + "' of type " + field.getType().getName());
        }

        if (value == null) {
            return;
        }

        String text = value.toString();
        if (!isValidEmail(text)) {
            errors.add(new ValidationError(
                    field.getName(),
                    Email.class.getSimpleName(),
                    annotation.message()));
        }
    }

    private static boolean isSupportedNumericType(Class<?> type) {
        if (Number.class.isAssignableFrom(type)) {
            return true;
        }
        return type == byte.class
                || type == short.class
                || type == int.class
                || type == long.class
                || type == float.class
                || type == double.class;
    }

    private static boolean isValidEmail(String email) {
        if (email.isEmpty()) {
            return false;
        }
        for (int i = 0; i < email.length(); i++) {
            if (Character.isWhitespace(email.charAt(i))) {
                return false;
            }
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 0 || atIndex != email.lastIndexOf('@') || atIndex == email.length() - 1) {
            return false;
        }

        String domainPart = email.substring(atIndex + 1);
        int dotIndex = domainPart.indexOf('.');
        if (dotIndex <= 0 || dotIndex == domainPart.length() - 1) {
            return false;
        }

        return true;
    }
}