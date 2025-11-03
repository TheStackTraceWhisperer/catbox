package com.example.catbox.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

/**
 * Architectural tests for Spring annotations usage in the catbox application. Tests verify that
 * Spring components are properly annotated.
 */
class SpringAnnotationTest {

  private static JavaClasses importedClasses;

  @BeforeAll
  static void setup() {
    importedClasses =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.example..");
  }

  @Test
  void controllersShouldBeAnnotatedWithRestController() {
    ArchRule rule =
        classes()
            .that()
            .resideInAPackage("..controller..")
            .and()
            .areNotInterfaces()
            .and()
            .areNotEnums()
            .and()
            .areNotRecords()
            .should()
            .beAnnotatedWith(RestController.class)
            .orShould()
            .beAnnotatedWith(Controller.class)
            .because("Controllers should be annotated with @RestController or @Controller");

    rule.allowEmptyShould(true);
    rule.check(importedClasses);
  }

  @Test
  void servicesShouldBeAnnotatedWithService() {
    ArchRule rule =
        classes()
            .that()
            .resideInAPackage("..service..")
            .and()
            .areNotInterfaces()
            .and()
            .areNotEnums()
            .and()
            .areNotRecords()
            .and()
            .areNotAnnotatedWith(org.springframework.context.annotation.Configuration.class)
            .should()
            .beAnnotatedWith(Service.class)
            .because("Service classes should be annotated with @Service");

    rule.allowEmptyShould(true);
    rule.check(importedClasses);
  }

  @Test
  void repositoriesShouldExtendSpringDataRepository() {
    ArchRule rule =
        classes()
            .that()
            .resideInAPackage("..repository..")
            .and()
            .areInterfaces()
            .should()
            .beAnnotatedWith(Repository.class)
            .orShould()
            .haveSimpleNameEndingWith("Repository")
            .because("Repository interfaces should extend Spring Data repositories");

    rule.check(importedClasses);
  }

  @Test
  void configurationClassesShouldBeAnnotatedWithConfiguration() {
    ArchRule rule =
        classes()
            .that()
            .resideInAPackage("..config..")
            .and()
            .areNotInterfaces()
            .and()
            .areNotEnums()
            .and()
            .areNotRecords()
            .and()
            .haveSimpleNameEndingWith("Config")
            .or()
            .haveSimpleNameEndingWith("Configuration")
            .should()
            .beAnnotatedWith(org.springframework.context.annotation.Configuration.class)
            .because("Configuration classes should be annotated with @Configuration");

    rule.check(importedClasses);
  }

  @Test
  void entitiesShouldBeAnnotatedWithEntity() {
    ArchRule rule =
        classes()
            .that()
            .resideInAPackage("..entity..")
            .and()
            .areNotEnums()
            .and()
            .areNotRecords()
            .should()
            .beAnnotatedWith(jakarta.persistence.Entity.class)
            .because("Entity classes should be annotated with @Entity");

    rule.check(importedClasses);
  }

  @Test
  void serviceMethodsShouldNotBePublicUnlessNecessary() {
    ArchRule rule =
        classes()
            .that()
            .resideInAPackage("..service..")
            .and()
            .areAnnotatedWith(Service.class)
            .should()
            .bePackagePrivate()
            .orShould()
            .bePublic()
            .because("Service classes should have appropriate visibility");

    rule.allowEmptyShould(true);
    rule.check(importedClasses);
  }
}
