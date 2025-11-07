package com.example.routebox.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Ensures that test classes are properly isolated to prevent regressions when using shared
 * singleton Testcontainers.
 */
class TestIsolationTest {

  private static JavaClasses importedClasses;

  @BeforeAll
  static void setup() {
    importedClasses =
        new ClassFileImporter()
            // Only import test classes
            .withImportOption(new ImportOption.OnlyIncludeTests())
            .importPackages("com.example..");
  }

  @Test
  void springBootTestsUsingDatabaseShouldBeTransactional() {
    ArchRule rule =
        classes()
            .that()
            .areAnnotatedWith(SpringBootTest.class)
            .and()
            .areNotAnnotatedWith(org.junit.jupiter.api.parallel.Isolated.class) // Allow manual isolation
            .and()
            .resideOutsideOfPackage("..kafka..") // Exclude tests that don't use DB
            .and()
            .haveSimpleNameNotContaining("E2E") // Exclude E2E tests that verify async behavior
            .should()
            .beAnnotatedWith(Transactional.class)
            .because(
                "All @SpringBootTest classes using the shared database must be @Transactional "
                    + "to ensure data rollback and prevent test cross-contamination.");

    // Allow rule to pass if no classes match (e.g., in a module with no tests)
    rule.allowEmptyShould(true);

    rule.check(importedClasses);
  }
}
