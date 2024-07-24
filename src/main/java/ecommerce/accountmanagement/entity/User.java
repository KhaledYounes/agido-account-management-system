package ecommerce.accountmanagement.entity;

import ecommerce.accountmanagement.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100, message = "Name cannot be longer than 100 characters")
    @Column(nullable = false, unique = true)
    private String name;

    @Size(max = 100, message = "E-Mail cannot be longer than 100 characters")
    @Column(name = "e_mail", nullable = false, unique = true)
    private String eMail;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;
}