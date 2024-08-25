package io.apaulino.superheroes.hero;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@QuarkusTest
@Provider("rest-heroes")
@PactFolder("pacts")
public class HeroContractVerificationTest {

    @ConfigProperty(name = "quarkus.http.test-port")
    int quarkusPort;

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    private static final String NO_HERO_FOUND_STATE = "No random hero found";

    @BeforeEach
    void setUp(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", this.quarkusPort));
        var noHeroState = Optional.of(context.getInteraction()
                .getProviderStates())
            .orElseGet(List::of)
            .stream()
            .anyMatch(state -> NO_HERO_FOUND_STATE.equals(state.getName()));
        if (noHeroState) {
            PanacheMock.mock(Hero.class);
            when(Hero.findRandom()).thenReturn(Uni.createFrom().nullItem());
        }
    }

    @State(NO_HERO_FOUND_STATE)
    void clearData() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    }
}
