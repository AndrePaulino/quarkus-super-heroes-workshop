package io.apaulino.superheroes.villain;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;

import static jakarta.transaction.Transactional.TxType.SUPPORTS;

@Transactional
@ApplicationScoped
public class VillainService {

    @Transactional(SUPPORTS)
    public List<Villain> listAllVillains() {
        return Villain.listAll();
    }

    @Transactional(SUPPORTS)
    public Villain findVillainById(Long id) {
        return Villain.findById(id);
    }

    @Transactional(SUPPORTS)
    public Villain findRandomVillain() {
        return Villain.findRandom();
    }

    public Villain saveVillain(@Valid Villain villain) {
        villain.persist();
        return villain;
    }

    public Villain updateVillain(@Valid Villain villain) {
        Villain updatedVillain = new Villain();
        updatedVillain.name = villain.name;
        updatedVillain.otherName = villain.otherName;
        updatedVillain.level = villain.level;
        updatedVillain.picture = villain.picture;
        updatedVillain.powers = villain.powers;
        return updatedVillain;
    }

    public void removeVillain(Long id) {
        Villain villain = Villain.findById(id);
        villain.delete();
    }
}
