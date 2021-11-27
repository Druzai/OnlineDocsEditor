package ru.app.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "docUsersRights")
@Getter
@Setter
public class DocumentUserRights {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    @OneToOne
    private Role role;

    public DocumentUserRights(User user, Role role){
        this.user = user;
        this.role = role;
    }

    public DocumentUserRights() {
    }
}
