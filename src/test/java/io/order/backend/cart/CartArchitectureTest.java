package io.order.backend.cart;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class CartArchitectureTest {
    @Test
    void cart_whenCheckingDependencies_shouldKeepPortsAndDomainIndependent() {
        var classes = new ClassFileImporter().importPackages("io.order.backend.cart");
        noClasses().that().resideInAPackage("..cart.domain..")
                .should().dependOnClassesThat().resideInAnyPackage("org.springframework..", "jakarta..",
                        "..application..", "..infrastructure..").check(classes);
        noClasses().that().resideInAPackage("..cart.application..")
                .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..", "..dto..",
                        "jakarta.persistence..", "org.springframework.data..").check(classes);
        noClasses().that().resideInAPackage("..cart.infrastructure.web.controller..")
                .should().dependOnClassesThat().resideInAnyPackage("..application.usecase..",
                        "..application.port.out..", "..infrastructure.persistence..").check(classes);
    }
}
