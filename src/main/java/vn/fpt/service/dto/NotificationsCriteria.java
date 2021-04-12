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
 * Criteria class for the {@link vn.fpt.domain.Notifications} entity. This class is used
 * in {@link vn.fpt.web.rest.NotificationsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /notifications?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class NotificationsCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter content;

    private ZonedDateTimeFilter times;

    private BooleanFilter status;

    private LongFilter userId;

    public NotificationsCriteria() {
    }

    public NotificationsCriteria(NotificationsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.content = other.content == null ? null : other.content.copy();
        this.times = other.times == null ? null : other.times.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
    }

    @Override
    public NotificationsCriteria copy() {
        return new NotificationsCriteria(this);
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

    public BooleanFilter getStatus() {
        return status;
    }

    public void setStatus(BooleanFilter status) {
        this.status = status;
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
        final NotificationsCriteria that = (NotificationsCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(content, that.content) &&
            Objects.equals(times, that.times) &&
            Objects.equals(status, that.status) &&
            Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        content,
        times,
        status,
        userId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationsCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (content != null ? "content=" + content + ", " : "") +
                (times != null ? "times=" + times + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
            "}";
    }

}
