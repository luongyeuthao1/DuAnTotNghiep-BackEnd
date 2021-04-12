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
 * Criteria class for the {@link vn.fpt.domain.Apply} entity. This class is used
 * in {@link vn.fpt.web.rest.ApplyResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /applies?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ApplyCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ZonedDateTimeFilter time;

    private StringFilter content;

    private LongFilter userId;

    private LongFilter postId;

    public ApplyCriteria() {
    }

    public ApplyCriteria(ApplyCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.time = other.time == null ? null : other.time.copy();
        this.content = other.content == null ? null : other.content.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.postId = other.postId == null ? null : other.postId.copy();
    }

    @Override
    public ApplyCriteria copy() {
        return new ApplyCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public ZonedDateTimeFilter getTime() {
        return time;
    }

    public void setTime(ZonedDateTimeFilter time) {
        this.time = time;
    }

    public StringFilter getContent() {
        return content;
    }

    public void setContent(StringFilter content) {
        this.content = content;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getPostId() {
        return postId;
    }

    public void setPostId(LongFilter postId) {
        this.postId = postId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ApplyCriteria that = (ApplyCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(time, that.time) &&
            Objects.equals(content, that.content) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(postId, that.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        time,
        content,
        userId,
        postId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApplyCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (time != null ? "time=" + time + ", " : "") +
                (content != null ? "content=" + content + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
                (postId != null ? "postId=" + postId + ", " : "") +
            "}";
    }

}
