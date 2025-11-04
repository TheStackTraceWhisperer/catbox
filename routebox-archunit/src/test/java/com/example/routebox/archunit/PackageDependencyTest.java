package com.example.routebox.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Architectural tests for package dependency rules in the catbox application.
 * Tests verify that packages follow proper dependency patterns.
 */
class PackageDependencyTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.example..");
    }

    @Test
    void commonModuleShouldNotDependOnOtherModules() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("com.example.catbox.common..")
            .should().dependOnClassesThat().resideInAPackage("com.example.catbox.client..")
            .orShould().dependOnClassesThat().resideInAPackage("com.example.catbox.server..")
            .orShould().dependOnClassesThat().resideInAPackage("com.example.order..")
            .because("The common module should be independent and not depend on other modules");

        rule.allowEmptyShould(true);
        rule.check(importedClasses);
    }

    @Test
    void clientModuleShouldNotDependOnServerOrOrderService() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("com.example.catbox.client..")
            .should().dependOnClassesThat().resideInAPackage("com.example.catbox.server..")
            .orShould().dependOnClassesThat().resideInAPackage("com.example.order..")
            .because("The client module should not depend on server or order-service modules");

        rule.allowEmptyShould(true);
        rule.check(importedClasses);
    }

    @Test
    void serverModuleShouldNotDependOnOrderService() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("com.example.catbox.server..")
            .should().dependOnClassesThat().resideInAPackage("com.example.order..")
            .because("The server module should not depend on the order-service module");

        rule.allowEmptyShould(true);
        rule.check(importedClasses);
    }

    @Test
    void orderServiceShouldNotDependOnCatboxServer() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("com.example.order..")
            .should().dependOnClassesThat().resideInAPackage("com.example.catbox.server..")
            .because("The order-service should not depend on catbox-server module");

        rule.allowEmptyShould(true);
        rule.check(importedClasses);
    }

    @Test
    void controllersShouldNotDependOnOtherControllers() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..controller..")
            .should().dependOnClassesThat().resideInAPackage("..controller..")
            .because("Controllers should not depend on other controllers");

        rule.allowEmptyShould(true);
        rule.check(importedClasses);
    }

    @Test
    void entitiesShouldNotDependOnServicesOrControllers() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..entity..")
            .should().dependOnClassesThat().resideInAPackage("..service..")
            .orShould().dependOnClassesThat().resideInAPackage("..controller..")
            .because("Entities should not depend on service or controller layers");

        rule.allowEmptyShould(true);
        rule.check(importedClasses);
    }

    @Test
    void repositoriesShouldOnlyDependOnEntitiesAndSpringData() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..repository..")
            .should().dependOnClassesThat().resideInAPackage("..controller..")
            .orShould().dependOnClassesThat().resideInAPackage("..service..")
            .because("Repositories should only depend on entities and Spring Data");

        rule.allowEmptyShould(true);
        rule.check(importedClasses);
    }
}
