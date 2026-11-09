package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidatorTest {

    private static class BaseAccount {
        @NotNull(message = "Id is required")
        private final String id;

        BaseAccount(String id) {
            this.id = id;
        }
    }

    private static class UserAccount extends BaseAccount {
        @Size(min = 3, max = 5, message = "Invalid username size")
        private static String ignoredStaticField = "x";

        @NotNull(message = "Name is required")
        @Size(min = 2, max = 10, message = "Name length must be 2..10")
        private final String name;

        @Email(message = "Bad email")
        private final String email;

        @Range(min = 18, max = 100, message = "Age must be 18..100")
        private final Integer age;

        @NotNull
        @Range(min = 0, max = 10)
        private final int primitiveScore;

        UserAccount(String id, String name, String email, Integer age, int primitiveScore) {
            super(id);
            this.name = name;
            this.email = email;
            this.age = age;
            this.primitiveScore = primitiveScore;
        }
    }

    private static class SingleFieldChecks {
        @NotNull(message = "NotNull failed")
        private String notNullField = "ok";

        @Size(min = 2, max = 4, message = "Size failed")
        private CharSequence sizeField = new StringBuilder("abc");

        @Range(min = 10.0, max = 20.0, message = "Range failed")
        private Double rangeField = 15.0;

        @Email(message = "Email failed")
        private String emailField = "user@example.com";
    }

    @Test
    void validObjectReturnsTrueAndEmptyErrors() {
        UserAccount user = new UserAccount("id-1", "Ivan", "ivan@mail.com", 25, 5);

        ValidationResult result = Validator.validate(user);

        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void notNullDetectsViolationIndividually() {
        SingleFieldChecks target = new SingleFieldChecks();
        target.notNullField = null;

        ValidationResult result = Validator.validate(target);

        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors().contains(
                new ValidationError("notNullField", "NotNull", "NotNull failed")));
    }

    @Test
    void sizeDetectsViolationIndividually() {
        SingleFieldChecks target = new SingleFieldChecks();
        target.sizeField = "a";

        ValidationResult result = Validator.validate(target);

        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors().contains(
                new ValidationError("sizeField", "Size", "Size failed")));
    }

    @Test
    void rangeDetectsViolationIndividuallyAndHandlesNaN() {
        SingleFieldChecks target = new SingleFieldChecks();
        target.rangeField = 25.0;

        ValidationResult result = Validator.validate(target);

        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors().contains(
                new ValidationError("rangeField", "Range", "Range failed")));

        target.rangeField = Double.NaN;
        ValidationResult nanResult = Validator.validate(target);
        assertFalse(nanResult.isValid());
        assertEquals(1, nanResult.getErrors().size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "plainaddress", "@domain.com", "user@", "user@domain", "user@domain.", "user@.com", "user name@domain.com"})
    void emailDetectsInvalidValues(String invalidEmail) {
        SingleFieldChecks target = new SingleFieldChecks();
        target.emailField = invalidEmail;

        ValidationResult result = Validator.validate(target);

        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors().contains(
                new ValidationError("emailField", "Email", "Email failed")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ab", "abc", "abcd"})
    void sizeBoundaryValuesPassValidation(String validValue) {
        SingleFieldChecks target = new SingleFieldChecks();
        target.sizeField = validValue;

        ValidationResult result = Validator.validate(target);

        assertTrue(result.isValid());
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "abcde"})
    void sizeOutOfBoundsValuesFailValidation(String invalidValue) {
        SingleFieldChecks target = new SingleFieldChecks();
        target.sizeField = invalidValue;

        ValidationResult result = Validator.validate(target);

        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
    }

    @ParameterizedTest
    @ValueSource(doubles = {10.0, 15.5, 20.0})
    void rangeBoundaryValuesPassValidation(double validNumber) {
        SingleFieldChecks target = new SingleFieldChecks();
        target.rangeField = validNumber;

        ValidationResult result = Validator.validate(target);

        assertTrue(result.isValid());
    }

    @ParameterizedTest
    @ValueSource(doubles = {9.99, 20.01})
    void rangeOutOfBoundsValuesFailValidation(double invalidNumber) {
        SingleFieldChecks target = new SingleFieldChecks();
        target.rangeField = invalidNumber;

        ValidationResult result = Validator.validate(target);

        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
    }

    @Test
    void multipleViolationsAcrossInheritedAndPrivateFieldsAreCollectedAndNullSkippedByOtherAnnotations() {
        UserAccount user = new UserAccount(null, "A", "wrong-email", 12, 5);

        ValidationResult result = Validator.validate(user);

        assertFalse(result.isValid());
        List<ValidationError> errors = result.getErrors();
        assertEquals(4, errors.size());
        assertTrue(errors.contains(new ValidationError("id", "NotNull", "Id is required")));
        assertTrue(errors.contains(new ValidationError("name", "Size", "Name length must be 2..10")));
        assertTrue(errors.contains(new ValidationError("email", "Email", "Bad email")));
        assertTrue(errors.contains(new ValidationError("age", "Range", "Age must be 18..100")));

        UserAccount userWithNulls = new UserAccount("id-1", null, null, null, 5);
        ValidationResult nullResult = Validator.validate(userWithNulls);
        assertEquals(1, nullResult.getErrors().size());
        assertTrue(nullResult.getErrors().contains(
                new ValidationError("name", "NotNull", "Name is required")));
    }

    @Test
    void singleFieldViolatingMultipleAnnotationsCollectsAllErrors() {
        class MultiConstraintModel {
            @Size(min = 10, max = 20, message = "Too short")
            @Email(message = "Bad email")
            private final String contact = "abc";
        }

        ValidationResult result = Validator.validate(new MultiConstraintModel());

        assertFalse(result.isValid());
        assertEquals(2, result.getErrors().size());
        assertTrue(result.getErrors().contains(new ValidationError("contact", "Size", "Too short")));
        assertTrue(result.getErrors().contains(new ValidationError("contact", "Email", "Bad email")));
    }

    @Test
    void errorsListIsUnmodifiable() {
        SingleFieldChecks target = new SingleFieldChecks();
        target.notNullField = null;

        ValidationResult result = Validator.validate(target);
        List<ValidationError> errors = result.getErrors();

        assertThrows(UnsupportedOperationException.class,
                () -> errors.add(new ValidationError("a", "b", "c")));
        assertThrows(NullPointerException.class, () -> new ValidationResult(null));
    }

    @Test
    void validateNullThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Validator.validate(null));
    }

    @Test
    void invalidConfigurationThrowsValidationConfigurationException() {
        class NegativeMinSizeModel {
            @Size(min = -1, max = 5)
            private String text = "abc";
        }

        class MinGreaterThanMaxSizeModel {
            @Size(min = 10, max = 5)
            private String text = "abc";
        }

        class UnsupportedSizeTypeModel {
            @Size(min = 1, max = 5)
            private Integer number = null;
        }

        class InvalidRangeBoundsModel {
            @Range(min = 20, max = 10)
            private int value = 15;
        }

        class NaNRangeBoundModel {
            @Range(min = Double.NaN, max = 10)
            private int value = 5;
        }

        class InfiniteRangeBoundModel {
            @Range(min = 0, max = Double.POSITIVE_INFINITY)
            private int value = 5;
        }

        class UnsupportedRangeTypeModel {
            @Range(min = 0, max = 10)
            private String text = "5";
        }

        class UnsupportedEmailTypeModel {
            @Email
            private Integer email = null;
        }

        assertThrows(ValidationConfigurationException.class,
                () -> Validator.validate(new NegativeMinSizeModel()));
        assertThrows(ValidationConfigurationException.class,
                () -> Validator.validate(new MinGreaterThanMaxSizeModel()));
        assertThrows(ValidationConfigurationException.class,
                () -> Validator.validate(new UnsupportedSizeTypeModel()));
        assertThrows(ValidationConfigurationException.class,
                () -> Validator.validate(new InvalidRangeBoundsModel()));
        assertThrows(ValidationConfigurationException.class,
                () -> Validator.validate(new NaNRangeBoundModel()));
        assertThrows(ValidationConfigurationException.class,
                () -> Validator.validate(new InfiniteRangeBoundModel()));
        assertThrows(ValidationConfigurationException.class,
                () -> Validator.validate(new UnsupportedRangeTypeModel()));
        assertThrows(ValidationConfigurationException.class,
                () -> Validator.validate(new UnsupportedEmailTypeModel()));
    }
}