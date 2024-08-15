package io.apaulino.superheroes.fight;

import io.apaulino.superheroes.fight.client.HeroProxy;
import io.apaulino.superheroes.fight.client.VillainProxy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;

@ApplicationScoped
@Transactional(SUPPORTS)
public class FightService {

    @Inject
    Logger logger;
    @RestClient
    HeroProxy heroProxy;
    @RestClient
    VillainProxy villainProxy;

    private final float HERO_POWER_MAX_MULTIPLIER = 3f;
    private final float VILLAIN_POWER_MAX_MULTIPLIER = 3f;

    private final Random random = ThreadLocalRandom.current();

    public List<Fight> findAllFights() {
        return Fight.listAll();
    }

    public Fight findFightById(Long id) {
        return Fight.findById(id);
    }

    Fighters findRandomFighters() {
        Fighters fighters = new Fighters();
        fighters.hero = heroProxy.findRandomHero();
        fighters.villain = villainProxy.findRandomVillain();
        return fighters;
    }

    @Transactional(REQUIRED)
    public Fight saveFight(Fighters fighters) {
        Fight fight = determineWinner(fighters);
        fight.fightDate = Instant.now();
        fight.persist();

        return fight;
    }

    private Fight determineWinner(Fighters fighters) {
        float heroPower = fighters.hero.level * random.nextFloat(HERO_POWER_MAX_MULTIPLIER);
        float villainPower = fighters.villain.level * random.nextFloat(VILLAIN_POWER_MAX_MULTIPLIER);

        if (heroPower == villainPower) return random.nextBoolean() ? heroWon(fighters) : villainWon(fighters);
        if (heroPower > villainPower) return heroWon(fighters);
        return villainWon(fighters);
    }

    private Fight heroWon(Fighters fighters) {
        logger.info("Yes, Hero won! ️🦸🏽");
        Fight fight = new Fight();
        fight.winnerName = fighters.hero.name;
        fight.winnerPicture = fighters.hero.picture;
        fight.winnerLevel = fighters.hero.level;
        fight.winnerPowers = fighters.hero.powers;
        fight.loserName = fighters.villain.name;
        fight.loserPicture = fighters.villain.picture;
        fight.loserLevel = fighters.villain.level;
        fight.loserPowers = fighters.villain.powers;
        fight.loserTeam = Teams.VILLAINS;
        fight.winnerTeam = Teams.HEROES;
        return fight;
    }

    private Fight villainWon(Fighters fighters) {
        logger.info("OH, Villain won 🦹🏽");
        Fight fight = new Fight();
        fight.winnerName = fighters.villain.name;
        fight.winnerPicture = fighters.villain.picture;
        fight.winnerLevel = fighters.villain.level;
        fight.winnerPowers = fighters.villain.powers;
        fight.loserName = fighters.hero.name;
        fight.loserPicture = fighters.hero.picture;
        fight.loserLevel = fighters.hero.level;
        fight.loserPowers = fighters.hero.powers;
        fight.winnerTeam = Teams.VILLAINS;
        fight.loserTeam = Teams.HEROES;
        return fight;
    }

}
