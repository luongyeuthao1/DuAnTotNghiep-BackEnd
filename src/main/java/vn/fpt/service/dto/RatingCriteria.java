package vn.fpt.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the {@link vn.fpt.domain.Rating} entity. This class is used
 * in {@link vn.fpt.web.rest.RatingResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ratings?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class RatingCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter content;

    private ZonedDateTimeFilter times;

    private IntegerFilter rank;

    private LongFilter userId;

    public RatingCriteria() {
    }

    public RatingCriteria(RatingCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.content = other.content == null ? null : other.content.copy();
        this.times = other.times == null ? null : other.times.copy();
        this.rank = other.rank == null ? null : other.rank.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
    }

    @Override
    public RatingCriteria copy() {
        return new RatingCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getContent() {
        return content;
    }

    public void setContent(StringFilter content) {
        this.content = content;
    }

    public ZonedDateTimeFilter getTimes() {
        return times;
    }

    public void setTimes(ZonedDateTimeFilter times) {
        this.times = times;
    }

    public IntegerFilter getRank() {
        return rank;
    }

    public void setRank(IntegerFilter rank) {
        this.rank = rank;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RatingCriteria that = (RatingCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(content, that.content) &&
            Objects.equals(times, that.times) &&
            Objects.equals(rank, that.rank) &&
            Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        content,
        times,
        rank,
        userId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RatingCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (content != null ? "content=" + content + ", " : "") +
                (times != null ? "times=" + times + ", " : "") +
                (rank != null ? "rank=" + rank + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
            "}";
    }

}
