package com.example.routebox.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

/**
 * Architectural tests for transaction boundaries in the routebox application.
 * Tests verify that services properly handle transactional operations.
 */
class TransactionBoundaryTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.example..");
    }

    @Test
    void serviceMethodsModifyingDataShouldBeTransactional() {
        ArchRule rule = methods()
            .that().areDeclaredInClassesThat().resideInAPackage("..service..")
            .and().areDeclaredInClassesThat().areAnnotatedWith(org.springframework.stereotype.Service.class)
            .and().arePublic()
            .and().haveNameMatching("(create|update|delete|save|remove).*")
            .should().beAnnotatedWith(org.springframework.transaction.annotation.Transactional.class)
            .orShould().beDeclaredInClassesThat().areAnnotatedWith(org.springframework.transaction.annotation.Transactional.class)
            .because("Service methods that modify data should be transactional");

        rule.allowEmptyShould(true);
        rule.check(importedClasses);
    }

    @Test
    void controllersShouldNotBeTransactional() {
        ArchRule rule = classes()
            .that().resideInAPackage("..controller..")
            .should().notBeAnnotatedWith(org.springframework.transaction.annotation.Transactional.class)
            .because("Controllers should not manage transactions directly - this should be done in the service layer");

        rule.allowEmptyShould(true);
        rule.check(importedClasses);
    }

    @Test
    void repositoriesShouldNotDefineTransactions() {
        ArchRule rule = methods()
            .that().areDeclaredInClassesThat().resideInAPackage("..repository..")
            .should().notBeAnnotatedWith(org.springframework.transaction.annotation.Transactional.class)
            .because("Repositories should not define their own transactions - Spring Data handles this");

        rule.check(importedClasses);
    }

    @Test
    void serviceClassesShouldBeAnnotatedWithTransactional() {
        ArchRule rule = classes()
            .that().resideInAPackage("..service..")
            .and().areAnnotatedWith(org.springframework.stereotype.Service.class)
            .and().haveSimpleNameEndingWith("Service")
            .should().beAnnotatedWith(org.springframework.transaction.annotation.Transactional.class)
            .because("Service classes should typically be annotated with @Transactional at class or method level");

        rule.allowEmptyShould(true);
        rule.check(importedClasses);
    }
}
