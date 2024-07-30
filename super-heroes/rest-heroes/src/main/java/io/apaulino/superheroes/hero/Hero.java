package io.apaulino.superheroes.hero;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.ToString;

import java.util.concurrent.ThreadLocalRandom;

@Entity
@ToString
public class Hero extends PanacheEntity {

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

    public static Uni<Hero> findRandom() {
        return count()
            .map(count -> ThreadLocalRandom.current().nextInt(count.intValue()))
            .chain(randomHero -> findAll().page(randomHero, 1)
                .firstResult());
    }

    public void copyFrom(Hero otherHero) {
        this.name = otherHero.name;
        this.otherName = otherHero.otherName;
        this.level = otherHero.level;
        this.picture = otherHero.picture;
        this.powers = otherHero.powers;
    }
}
