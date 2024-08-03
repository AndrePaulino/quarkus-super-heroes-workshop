package io.apaulino.superheroes.fight;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Optional;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class TeamsConverter implements AttributeConverter<Teams, String> {

    @Override
    public String convertToDatabaseColumn(Teams teams) {
        return Optional.ofNullable(teams)
            .map(team -> team.name().toLowerCase())
            .orElse(null);
    }

    @Override
    public Teams convertToEntityAttribute(String dbData) {
        return Optional.ofNullable(dbData)
            .flatMap(data -> Stream.of(Teams.values())
                .filter(team -> team.name().equalsIgnoreCase(data))
                .findFirst())
            .orElse(null);
    }
}
