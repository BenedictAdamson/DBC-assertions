package uk.badamson.dbc.assertions;

import org.hamcrest.Description;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.BiPredicate;

final class DelegatingPairRelationshipMatcher<T, U> extends PairRelationshipMatcher<T, U> {

    @Nonnull
    private final String description;
    @Nonnull
    private final BiPredicate<T, U> predicate;

    DelegatingPairRelationshipMatcher(@Nonnull U other, @Nonnull String description, @Nonnull BiPredicate<T, U> predicate) {
        super(other);
        this.description = Objects.requireNonNull(description, "description");
        this.predicate = Objects.requireNonNull(predicate, "predicate");
    }

    @Override
    protected boolean matchesSafely(@Nonnull T item1, @Nonnull U item2, @Nonnull Description mismatchDescription) {
        final boolean ok;
        try {
            ok = predicate.test(item1, item2);
        } catch (Exception e) {
            mismatchDescription.appendText("failed because the predicate threw exception ");
            mismatchDescription.appendValue(e);
            return false;
        }
        if (!ok) {
            mismatchDescription.appendText("not satisfied");
        }
        return ok;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(this.description);
    }
}
