package com.mykare.mykare_assignment.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String gender;

    private String ipAddress;

    private String country;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "role_enum DEFAULT 'USER'")
    private Role role;


}
