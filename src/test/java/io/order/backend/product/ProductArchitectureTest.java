package io.order.backend.product;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ProductArchitectureTest {
    @Test
    void product_whenCheckingDependencies_shouldKeepPortsAndDomainIndependent() {
        var classes = new ClassFileImporter().importPackages("io.order.backend.product");
        noClasses().that().resideInAPackage("..product.domain..")
                .should().dependOnClassesThat().resideInAnyPackage("org.springframework..", "jakarta..",
                        "..application..", "..infrastructure..").check(classes);
        noClasses().that().resideInAPackage("..product.application..")
                .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..", "..dto..",
                        "jakarta.persistence..").check(classes);
        noClasses().that().resideInAPackage("..product.infrastructure.web.controller..")
                .should().dependOnClassesThat().resideInAnyPackage("..application.usecase..",
                        "..application.port.out..", "..infrastructure.persistence..").check(classes);
    }
}
