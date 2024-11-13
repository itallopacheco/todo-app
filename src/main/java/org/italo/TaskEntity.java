package org.italo;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalTime;

@Entity
public class TaskEntity extends PanacheEntity {
    public String title;

    @Enumerated(EnumType.STRING)
    public Status status;
    public Boolean active = true;
    public LocalTime createdAt = LocalTime.now();
    public LocalTime updatedAt = LocalTime.now();
}
