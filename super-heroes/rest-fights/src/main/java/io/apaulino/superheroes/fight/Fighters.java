package io.apaulino.superheroes.fight;

import io.apaulino.superheroes.fight.client.Hero;
import io.apaulino.superheroes.fight.client.Villain;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "A fight between one hero and one villain")
public class Fighters {
    @NotNull
    public Hero hero;
    @NotNull
    public Villain villain;
}
