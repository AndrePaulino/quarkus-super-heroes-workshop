package io.apaulino.superheroes.villain;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.ExternalDocumentation;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

@ApplicationPath("/")
@OpenAPIDefinition(info =
@Info(title = "Villain API", description = "This API allows CRUD operation on a villain", version = "1.0.0", contact = @Contact(name = "André Paulino", url = "https://github.com/AndrePaulino/")), servers = {@Server(url = "http://localhost:8084")}, externalDocs = @ExternalDocumentation(url = "https://quarkus.io/quarkus-workshops/super-heroes/variants/os-linux-ai-false-azure-false-cli-false-container-true-contract-testing-true-extension-true-kubernetes-true-messaging-true-native-true-observability-false/spine.html#_injecting_configuration_value", description = "Original workshop of this project"))
public class VillainApplication extends Application {
}
