package io.order.backend.common;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class DomainArchitectureTest {
    @Test
    void domain_shouldRemainIndependentOfFrameworkAndAdapters() throws java.net.URISyntaxException {
        var classes = new ClassFileImporter()
            .importPath(java.nio.file.Path.of(io.order.backend.OrderApplication.class
                    .getProtectionDomain().getCodeSource().getLocation().toURI()));
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "org.springframework..", "jakarta.persistence..", "org.hibernate..", "..application..", "..infrastructure..", "..api..", "..dto..")
            .check(classes);
    }

    @Test
    void application_shouldDependOnPortsRatherThanAdapters() throws java.net.URISyntaxException {
        var classes = new ClassFileImporter()
            .importPath(java.nio.file.Path.of(io.order.backend.OrderApplication.class
                    .getProtectionDomain().getCodeSource().getLocation().toURI()));
        noClasses().that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "..infrastructure..", "..api..", "org.springframework.jdbc..",
                "org.springframework.data.jpa..", "jakarta.persistence..", "org.hibernate..",
                "io.order.backend.common.idempotency..")
            .check(classes);
        noClasses().that().resideInAPackage("..application..")
            .should().dependOnClassesThat().haveFullyQualifiedName("io.order.backend.common.messaging.OutboxService")
            .check(classes);
    }

    @Test
    void controllers_shouldDependOnUseCasePortsRatherThanImplementations() throws java.net.URISyntaxException {
        var classes = new ClassFileImporter()
            .importPath(java.nio.file.Path.of(io.order.backend.OrderApplication.class
                    .getProtectionDomain().getCodeSource().getLocation().toURI()));
        noClasses().that().resideInAPackage("..api..")
            .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..", "..application")
            .check(classes);
    }

    @Test
    void modules_shouldKeepForeignDomainAndRepositoriesPrivate() throws java.net.URISyntaxException {
        var classes = new ClassFileImporter()
            .importPath(java.nio.file.Path.of(io.order.backend.OrderApplication.class
                    .getProtectionDomain().getCodeSource().getLocation().toURI()));
        var modules = java.util.List.of("cart", "checkout", "inventory", "order", "payment", "product", "shipping");
        for (String owner : modules) {
            for (String other : modules) {
                if (owner.equals(other)) continue;
                String foreign = "io.order.backend." + other;
                noClasses().that().resideInAPackage("io.order.backend." + owner + "..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                        foreign + ".domain..", foreign + ".infrastructure..", foreign + ".mapper..",
                        foreign + ".application.port.out..", foreign + ".application")
                    .check(classes);
            }
        }
    }
}
