package com.example.routebox.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Architectural tests for naming conventions in the catbox application. Tests verify that classes
 * follow consistent naming patterns based on their roles.
 */
class NamingConventionTest {

  private static JavaClasses importedClasses;

  @BeforeAll
  static void setup() {
    importedClasses =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.example..");
  }

  @Test
  void controllersShouldBeSuffixed() {
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
            .haveSimpleNameEndingWith("Controller")
            .because("Controllers should follow naming convention with 'Controller' suffix");

    rule.allowEmptyShould(true);
    rule.check(importedClasses);
  }

  @Test
  void servicesShouldBeSuffixed() {
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
            .haveSimpleNameEndingWith("Service")
            .orShould()
            .haveSimpleNameEndingWith("Handler")
            .orShould()
            .haveSimpleNameEndingWith("Publisher")
            .orShould()
            .haveSimpleNameEndingWith("Poller")
            .orShould()
            .haveSimpleNameEndingWith("Claimer")
            .because("Service classes should follow naming convention with appropriate suffix");

    rule.allowEmptyShould(true);
    rule.check(importedClasses);
  }

  @Test
  void repositoriesShouldBeSuffixed() {
    ArchRule rule =
        classes()
            .that()
            .resideInAPackage("..repository..")
            .and()
            .areNotEnums()
            .and()
            .areNotRecords()
            .should()
            .haveSimpleNameEndingWith("Repository")
            .because("Repositories should follow naming convention with 'Repository' suffix");

    rule.check(importedClasses);
  }

  @Test
  void configurationsShouldBeSuffixed() {
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
            .should()
            .haveSimpleNameEndingWith("Config")
            .orShould()
            .haveSimpleNameEndingWith("Configuration")
            .orShould()
            .haveSimpleNameEndingWith("Factory")
            .because("Configuration classes should follow naming convention");

    rule.allowEmptyShould(true);
    rule.check(importedClasses);
  }

  @Test
  void entitiesShouldResideInEntityPackage() {
    ArchRule rule =
        classes()
            .that()
            .areAnnotatedWith(jakarta.persistence.Entity.class)
            .should()
            .resideInAPackage("..entity..")
            .because("JPA entities should be in entity packages");

    rule.check(importedClasses);
  }

  @Test
  void exceptionsShouldBeSuffixed() {
    ArchRule rule =
        classes()
            .that()
            .resideInAPackage("..exception..")
            .and()
            .areNotEnums()
            .and()
            .areNotRecords()
            .should()
            .haveSimpleNameEndingWith("Exception")
            .because("Exception classes should follow naming convention with 'Exception' suffix");

    rule.allowEmptyShould(true);
    rule.check(importedClasses);
  }

  @Test
  void dtosShouldResideInDtoPackage() {
    ArchRule rule =
        classes()
            .that()
            .resideInAPackage("..dto..")
            .and()
            .areNotEnums()
            .should()
            .haveSimpleNameEndingWith("Request")
            .orShould()
            .haveSimpleNameEndingWith("Response")
            .orShould()
            .haveSimpleNameEndingWith("DTO")
            .orShould()
            .haveSimpleNameEndingWith("Dto")
            .because("DTOs should follow naming convention");

    rule.allowEmptyShould(true);
    rule.check(importedClasses);
  }
}
