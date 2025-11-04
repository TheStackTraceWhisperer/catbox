package com.example.routebox.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

/**
 * Architectural tests for JPA entity and repository patterns in the routebox application.
 * Tests verify that entities and repositories follow JPA best practices.
 */
class EntityRepositoryPatternTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.example..");
    }

    @Test
    void entitiesIdFieldsShouldBeAnnotatedWithId() {
        ArchRule rule = fields()
            .that().areDeclaredInClassesThat().areAnnotatedWith(jakarta.persistence.Entity.class)
            .and().haveName("id")
            .should().beAnnotatedWith(jakarta.persistence.Id.class)
            .because("Entity id fields should be annotated with @Id");

        rule.check(importedClasses);
    }

    @Test
    void repositoriesShouldBeInterfaces() {
        ArchRule rule = classes()
            .that().resideInAPackage("..repository..")
            .and().haveSimpleNameEndingWith("Repository")
            .should().beInterfaces()
            .because("Spring Data repositories should be interfaces");

        rule.check(importedClasses);
    }

    @Test
    void entitiesShouldNotHavePublicFields() {
        ArchRule rule = fields()
            .that().areDeclaredInClassesThat().areAnnotatedWith(jakarta.persistence.Entity.class)
            .and().areNotStatic()
            .should().bePrivate()
            .orShould().beProtected()
            .orShould().bePackagePrivate()
            .because("Entity fields should not be public to maintain encapsulation");

        rule.check(importedClasses);
    }

    @Test
    void entitiesShouldResideInEntityPackage() {
        ArchRule rule = classes()
            .that().areAnnotatedWith(jakarta.persistence.Entity.class)
            .should().resideInAPackage("..entity..")
            .because("JPA entities should be organized in entity packages");

        rule.check(importedClasses);
    }

    @Test
    void tableAnnotationShouldBeUsedForEntities() {
        ArchRule rule = classes()
            .that().areAnnotatedWith(jakarta.persistence.Entity.class)
            .should().beAnnotatedWith(jakarta.persistence.Table.class)
            .because("Entities should explicitly define table names with @Table annotation");

        rule.check(importedClasses);
    }
}
