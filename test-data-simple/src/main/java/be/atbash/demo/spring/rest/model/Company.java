package be.atbash.demo.spring.rest.model;

import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;

import java.io.Serializable;

@Entity
@Table(name = "company")  // Not strictly needed, but clear because database should not be generated from the code
public class Company implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NaturalId
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // equals and hashCode should be on the natural key, name in this case.
    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Company company)) {
            return false;
        }

        return name.equals(company.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
