package io.apaulino.superheroes.villain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.ToString;

import java.util.concurrent.ThreadLocalRandom;

@Entity
@ToString
public class Villain extends PanacheEntity {

    @NotNull
    @Size(min = 3, max = 50)
    public String name;

    public String otherName;

    @NotNull
    @Min(1)
    public int level;

    public String picture;

    @Column(columnDefinition = "TEXT")
    public String powers;

    public static Villain findRandom() {
        long countVillains = count();
        if (countVillains == 0) {
            return null;
        }

        int randomVillain = ThreadLocalRandom.current().nextInt((int) countVillains);
        return findAll().page(randomVillain, 1).firstResult();
    }
}
