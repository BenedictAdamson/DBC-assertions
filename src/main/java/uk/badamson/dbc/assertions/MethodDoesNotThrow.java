package uk.badamson.dbc.assertions;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

final class MethodDoesNotThrow<T> extends TypeSafeDiagnosingMatcher<T> {

    @Nonnull
    private final String methodName;

    @Nonnull
    private final Consumer<T> method;

    MethodDoesNotThrow(@Nonnull String methodName, @Nonnull Consumer<T> method) {
        this.methodName = Objects.requireNonNull(methodName, "methodName");
        this.method = Objects.requireNonNull(method, "method");
    }

    @Override
    protected final boolean matchesSafely(T item, Description mismatchDescription) {
        try {
            method.accept(item);
        } catch (Throwable e) {
            mismatchDescription.appendText("failed because it threw exception ");
            mismatchDescription.appendValue(e);
            return false;
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("method ");
        description.appendText(methodName);
        description.appendText(" does not throw an exception");
    }

}
