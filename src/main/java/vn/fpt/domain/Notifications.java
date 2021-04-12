package vn.fpt.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * A Notifications.
 */
@Entity
@Table(name = "notifications")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Notifications implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "times")
    private ZonedDateTime times;

    @Column(name = "status")
    private Boolean status;

    @ManyToOne
    @JsonIgnoreProperties(value = "notifications", allowSetters = true)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public Notifications content(String content) {
        this.content = content;
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getTimes() {
        return times;
    }

    public Notifications times(ZonedDateTime times) {
        this.times = times;
        return this;
    }

    public void setTimes(ZonedDateTime times) {
        this.times = times;
    }

    public Boolean isStatus() {
        return status;
    }

    public Notifications status(Boolean status) {
        this.status = status;
        return this;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public Notifications user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notifications)) {
            return false;
        }
        return id != null && id.equals(((Notifications) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Notifications{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", times='" + getTimes() + "'" +
            ", status='" + isStatus() + "'" +
            "}";
    }
}
