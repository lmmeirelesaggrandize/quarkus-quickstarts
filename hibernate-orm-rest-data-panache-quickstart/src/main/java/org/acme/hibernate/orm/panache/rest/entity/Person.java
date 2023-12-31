package org.acme.hibernate.orm.panache.rest.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name = "PersonForEntity")
@NamedQuery(name = "PersonForEntity.containsInName", query = "from PersonForEntity where name like CONCAT('%', CONCAT(:name, '%'))")
public class Person extends PanacheEntity {
    public String name;
    public LocalDate birthDate;
}
