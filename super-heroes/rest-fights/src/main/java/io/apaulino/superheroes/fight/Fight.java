package io.apaulino.superheroes.fight;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

@Entity
@Schema(description = "Each fight has a winner and a loser")
public class Fight extends PanacheEntity {

    @NotNull
    public Instant fightDate;

    @NotNull
    public String winnerName;

    @NotNull
    public int winnerLevel;

    @NotNull
    @Column(columnDefinition = "TEXT")
    public String winnerPowers;

    @NotNull
    public String winnerPicture;

    @NotNull
    public String loserName;

    @NotNull
    public int loserLevel;

    @NotNull
    @Column(columnDefinition = "TEXT")
    public String loserPowers;

    @NotNull
    public String loserPicture;

    @NotNull
    @Convert(converter = TeamsConverter.class)
    public Teams winnerTeam;

    @NotNull
    @Convert(converter = TeamsConverter.class)
    public Teams loserTeam;
}
