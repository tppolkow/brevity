package com.fydp.backend.model;


import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name="user", schema="brevity")
public class User {

    @Id
    private String id;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name="user_request",
            schema="brevity",
            joinColumns = {@JoinColumn(name="id")},
            inverseJoinColumns={@JoinColumn(name="summary_id")}
    )
    private List<Summary> summaries;

    @NotNull
    private String name;

    @Email
    @NotNull
    private String email;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Summary> getSummaries() {
        return summaries;
    }

    public void setSummaries(List<Summary> summaries) {
        this.summaries = summaries;
    }
}
