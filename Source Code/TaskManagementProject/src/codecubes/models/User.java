package codecubes.models;

import codecubes.core.Encryption;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by msaeed on 1/22/2017.
 */

@Entity
@Table (name = "users")
public class User {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    @Column (name = "first_name")
    private String firstName;

    @Column (name = "last_name")
    private String lastName;

    @Column(name = "email")
    @NaturalId
    @Basic(optional = false)
    private String email;

    private String password;

    private boolean manager = false;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    @Cascade(CascadeType.ALL)
    private Set<Project> projects = new HashSet();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    @Cascade(CascadeType.ALL)
    private Set<Task> tasks = new HashSet();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        try {
            this.password = Encryption.getInstance().sha1(password);
        } catch (Exception exception) {
            this.password = password;
        }
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }
}
