package uk.badamson.dbc.assertions;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import javax.annotation.Nonnull;

/**
 * <p>
 * A {@linkplain org.hamcrest.Matcher matcher} for an object that is satisfied if, and only if, calling
 * a method of that object does not throw an exception.
 * </p>
 */
public abstract class MethodDoesNotThrowException<T> extends TypeSafeDiagnosingMatcher<T> {

    /**
     * <p>
     * Call the method that should not throw an exception for the object being matched.
     * </p>
     */
    @SuppressWarnings("RedundantThrows")
    protected abstract void callMethod(@Nonnull T item) throws Throwable;

    @Override
    protected final boolean matchesSafely(T item, Description mismatchDescription) {
        try {
            callMethod(item);
        } catch (Throwable e) {
            mismatchDescription.appendText("failed because it threw exception ");
            mismatchDescription.appendValue(e);
            return false;
        }
        return true;
    }
}// class
