package io.order.backend.common;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class CommonArchitectureTest {
    @Test
    void domainAndPorts_shouldNotDependOnFrameworkOrAdapters() {
        var classes = new ClassFileImporter().importPackages("io.order.backend.common");
        noClasses().that().resideInAnyPackage(
                "..common.domain..", "..common.application.port..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "org.springframework..", "jakarta.persistence..", "..infrastructure..", "..config..")
            .check(classes);
        noClasses().that().haveSimpleNameEndingWith("Exception")
            .should().dependOnClassesThat().resideInAnyPackage("org.springframework..")
            .check(classes);
    }
}
