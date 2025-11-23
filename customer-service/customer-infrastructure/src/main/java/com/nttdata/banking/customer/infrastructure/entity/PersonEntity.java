package com.nttdata.banking.customer.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA Entity for Person table.
 */
@Entity
@Table(name = "person")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Long personId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "identification", nullable = false, unique = true, length = 20)
    private String identification;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "phone", length = 20)
    private String phone;
}
