package io.order.backend.payment;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

class PaymentArchitectureTest {
    @Test
    void domainAndApplication_respectHexagonalBoundaries() {
        var classes = new ClassFileImporter().withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("io.order.backend.payment");
        noClasses().that().resideInAPackage("..payment.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..", "jakarta.persistence..", "..application..", "..infrastructure..")
                .check(classes);
        noClasses().that().resideInAPackage("..payment.application..")
                .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..", "jakarta.persistence..")
                .check(classes);
        noClasses().that().resideInAPackage("..payment.infrastructure..")
                .should().dependOnClassesThat().resideInAPackage("..payment.application.usecase..")
                .check(classes);
        org.assertj.core.api.Assertions.assertThat(io.order.backend.payment.domain.Payment.class.isRecord()).isFalse();
        fields().that().areDeclaredInClassesThat().haveSimpleName("PaymentJpaEntity")
                .and().areNotStatic().should().bePrivate().check(classes);
        noMethods().that().areDeclaredInClassesThat().resideInAPackage("..payment.domain..")
                .should().haveNameMatching("set[A-Z].*").check(classes);
        noMethods().that().areDeclaredInClassesThat().haveSimpleName("PaymentJpaEntity")
                .should().haveNameMatching("set[A-Z].*").check(classes);
    }
}
