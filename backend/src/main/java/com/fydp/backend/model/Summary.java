package com.fydp.backend.model;

import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.util.Objects;

//model to interact with Summary table
@Entity
@Table(name = "summary", schema = "brevity")
public class Summary {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long summary_id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinTable(name="user_request",
            schema="brevity",
            joinColumns = {@JoinColumn(name="summary_id")},
            inverseJoinColumns={@JoinColumn(name="id")}
    )
    private User user;

    private String title;

    private String data;

    private boolean finished;

    public Summary(String title, String data, boolean finished, User user) {
        this.title = title;
        this.data = data;
        this.finished = finished;
        this.user = user;
    }

    public Summary(){}

    public Long getSummary_id() {
        return summary_id;
    }

    public void setSummary_id(Long summary_id) {
        this.summary_id = summary_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Summary summary = (Summary) o;
        return finished == summary.finished &&
                Objects.equals(summary_id, summary.summary_id) &&
                Objects.equals(title, summary.title) &&
                Objects.equals(data, summary.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(summary_id, title, data, finished);
    }

    @Override
    public String toString() {
        return "Summary{" +
                "summary_id=" + summary_id +
                ", title='" + title + '\'' +
                ", data='" + data + '\'' +
                ", finished=" + finished +
                '}';
    }
}
