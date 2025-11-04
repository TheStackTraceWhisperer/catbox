package com.example.routebox.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Architectural tests for layering rules in the routebox application.
 * Tests verify that the application follows a clean layered architecture pattern.
 */
class LayeringArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.example..");
    }

    @Test
    void controllersShouldNotAccessRepositoriesDirectly() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..controller..")
            .should().dependOnClassesThat().resideInAPackage("..repository..")
            .because("Controllers should not access repositories directly");

        rule.check(importedClasses);
    }

    @Test
    void servicesShouldNotAccessControllers() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..service..")
            .should().dependOnClassesThat().resideInAPackage("..controller..")
            .because("Services should not access controllers");

        rule.allowEmptyShould(true);
        rule.check(importedClasses);
    }

    @Test
    void repositoriesShouldNotAccessServicesOrControllers() {
        ArchRule serviceRule = noClasses()
            .that().resideInAPackage("..repository..")
            .should().dependOnClassesThat().resideInAPackage("..service..")
            .because("Repositories should not access services");

        ArchRule controllerRule = noClasses()
            .that().resideInAPackage("..repository..")
            .should().dependOnClassesThat().resideInAPackage("..controller..")
            .because("Repositories should not access controllers");

        serviceRule.check(importedClasses);
        controllerRule.check(importedClasses);
    }
}
