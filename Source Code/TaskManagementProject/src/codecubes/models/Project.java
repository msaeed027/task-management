package codecubes.models;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by msaeed on 1/25/2017.
 */

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @Lob
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "project")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<Task> tasks = new HashSet();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }
}
