package uk.badamson.dbc.assertions;

import org.hamcrest.Description;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * <p>
 * Provide safe access to some method calls,
 * to reduce the need for guard assertions in unit tests.
 * </p>
 */
public final class Safe {

    private Safe() {
        assert false; // should not instance
    }

    /**
     * <p>
     * Provide a {@link String} representation of a given object, in a manner that
     * is safe in cases where the {@link Object#toString()} method (or its override)
     * throws an exception.
     * </p>
     * <p>
     * This method is intended for use in test assertion failure messages, to
     * identify the object that failed the assertion. However, because the
     * {@link Object#toString()} method (and its overrides) can <em>themselves</em>
     * be faulty and throw exceptions, and {@code object} could be null, directly
     * calling a {@code object.toString()} method in test code is unwise. This
     * method returns the text given by {@code object.toString()}, if possible, but
     * if {@code object} is {@code null} it returns "null", and if
     * {@code object.toString()} throws an exception, it instead returns a fall-back
     * value. The fall-back value is the text that {@link Object#toString()} returns
     * if {@link Object#hashCode()} is not overridden.
     * </p>
     */
    @Nonnull
    public static String toString(@Nullable final Object object) {
        if (object == null) {
            return "null";
        } else {
            try {
                return object.toString();
            } catch (final Exception e) {
                return object.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(object));
            }
        }
    }

    /**
     * <p>
     * Provide the {@link Object#equals(Object)} value for use in a
     * {@link org.hamcrest.Matcher},
     * in a manner that is safe against nulls or an {@link Object#equals(Object)} implementation that throws exceptions.
     * </p>
     *
     * @return The {@linkplain Boolean#valueOf(boolean) boxed}
     * value of {@code item1.equals(item2)},
     * or null if computing that value throw an exception.
     * If the method returns null, it appends a failure description to {@code mismatchDescription}.
     */
    @Nullable
    public static Boolean equals(@Nullable Object item1, @Nullable Object item2, @Nonnull Description mismatchDescription) {
        // Objects.equals does not give the wanted value for item1 == item2
        if (item1 == null && item2 == null) {
            return true;
        } else if (item1 == null) {
            // avoid NPE
            return false;
        } else {
            try {
                return item1.equals(item2);
            } catch (Exception e) {
                mismatchDescription.appendText("but equals() threw exception ");
                mismatchDescription.appendValue(e);
                return null;
            }
        }
    }
}
