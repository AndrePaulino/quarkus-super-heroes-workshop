package io.apaulino.superheroes.villain;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ConfigUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class VillainApplicationLifeCycle {

    @Inject
    Logger LOGGER;

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("""


             ██▒   █▓ ██▓ ██▓     ██▓    ▄▄▄       ██▓ ███▄    █   ██████        ▄▄▄       ██▓███   ██▓
            ▓██░   █▒▓██▒▓██▒    ▓██▒   ▒████▄    ▓██▒ ██ ▀█   █ ▒██    ▒       ▒████▄    ▓██░  ██▒▓██▒
             ▓██  █▒░▒██▒▒██░    ▒██░   ▒██  ▀█▄  ▒██▒▓██  ▀█ ██▒░ ▓██▄         ▒██  ▀█▄  ▓██░ ██▓▒▒██▒
              ▒██ █░░░██░▒██░    ▒██░   ░██▄▄▄▄██ ░██░▓██▒  ▐▌██▒  ▒   ██▒      ░██▄▄▄▄██ ▒██▄█▓▒ ▒░██░
               ▒▀█░  ░██░░██████▒░██████▒▓█   ▓██▒░██░▒██░   ▓██░▒██████▒▒       ▓█   ▓██▒▒██▒ ░  ░░██░
               ░ ▐░  ░▓  ░ ▒░▓  ░░ ▒░▓  ░▒▒   ▓▒█░░▓  ░ ▒░   ▒ ▒ ▒ ▒▓▒ ▒ ░       ▒▒   ▓▒█░▒▓▒░ ░  ░░▓ \s
               ░ ░░   ▒ ░░ ░ ▒  ░░ ░ ▒  ░ ▒   ▒▒ ░ ▒ ░░ ░░   ░ ▒░░ ░▒  ░ ░        ▒   ▒▒ ░░▒ ░      ▒ ░
                 ░░   ▒ ░  ░ ░     ░ ░    ░   ▒    ▒ ░   ░   ░ ░ ░  ░  ░          ░   ▒   ░░        ▒ ░
                  ░   ░      ░  ░    ░  ░     ░  ░ ░           ░       ░              ░  ░          ░ \s
                 ░                                                                                    \s
            """);
        LOGGER.info("The application 'Villains API' is running on " + ConfigUtils.getProfiles() + " profile");
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application 'Villains API is stopping...'");
    }
}

