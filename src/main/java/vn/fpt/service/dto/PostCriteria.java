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
 * Criteria class for the {@link vn.fpt.domain.Post} entity. This class is used
 * in {@link vn.fpt.web.rest.PostResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /posts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PostCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter content;

    private ZonedDateTimeFilter createTime;

    private IntegerFilter like;

    private IntegerFilter type;

    private LongFilter userId;

    public PostCriteria() {
    }

    public PostCriteria(PostCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.content = other.content == null ? null : other.content.copy();
        this.createTime = other.createTime == null ? null : other.createTime.copy();
        this.like = other.like == null ? null : other.like.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
    }

    @Override
    public PostCriteria copy() {
        return new PostCriteria(this);
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

    public ZonedDateTimeFilter getCreateTime() {
        return createTime;
    }

    public void setCreateTime(ZonedDateTimeFilter createTime) {
        this.createTime = createTime;
    }

    public IntegerFilter getLike() {
        return like;
    }

    public void setLike(IntegerFilter like) {
        this.like = like;
    }

    public IntegerFilter getType() {
        return type;
    }

    public void setType(IntegerFilter type) {
        this.type = type;
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
        final PostCriteria that = (PostCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(content, that.content) &&
            Objects.equals(createTime, that.createTime) &&
            Objects.equals(like, that.like) &&
            Objects.equals(type, that.type) &&
            Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        content,
        createTime,
        like,
        type,
        userId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PostCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (content != null ? "content=" + content + ", " : "") +
                (createTime != null ? "createTime=" + createTime + ", " : "") +
                (like != null ? "like=" + like + ", " : "") +
                (type != null ? "type=" + type + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
            "}";
    }

}
