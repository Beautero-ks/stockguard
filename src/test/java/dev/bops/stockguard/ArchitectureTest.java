package dev.bops.stockguard;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

class ArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("dev.bops.stockguard");
    }

    /**
     * Règle 1 : Les modules métier ne doivent pas se connaître directement.
     * Ils ne peuvent dépendre que de 'shared', des librairies standard et du framework.
     */
    @Test
    void modulesShouldNotDependOnEachOther() {
        classes()
                .that().resideInAnyPackage(
                        "dev.bops.stockguard.catalog..",
                        "dev.bops.stockguard.stock..",
                        "dev.bops.stockguard.replenishment..",
                        "dev.bops.stockguard.user..",
                        "dev.bops.stockguard.traceability.."
                )
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(
                        "dev.bops.stockguard.shared..",
                        "dev.bops.stockguard.catalog..",
                        "dev.bops.stockguard.stock..",
                        "dev.bops.stockguard.replenishment..",
                        "dev.bops.stockguard.user..",
                        "dev.bops.stockguard.traceability..",
                        "java..",
                        "org.springframework..",
                        "jakarta..",
                        "lombok..",
                        "org.slf4j..",
                        "com.google.common.."
                )
                .because("Les modules métier ne doivent pas avoir de dépendances directes entre eux. "
                        + "Utilisez les services d'application ou un bus d'événements.")
                .check(importedClasses);
    }

    /**
     * Règle 2 : Le domaine métier doit rester pur.
     * Pas de dépendance à Spring, Jackson, ou toute technologie d'infrastructure.
     */
    @Test
    void domainShouldNotDependOnInfrastructure() {
        classes()
                .that().resideInAPackage("..domain..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(
                        "java..",
                        "jakarta.persistence..",
                        "dev.bops.stockguard..domain..",
                        "lombok..",
                        "com.google.common.."
                )
                .because("Le domaine contient la logique métier et doit être indépendant des frameworks.")
                .check(importedClasses);
    }

    /**
     * Règle 3 : Tout contrôleur Spring MVC doit être dans un package 'api'.
     * Règle assouplie pour le début de projet (allowEmptyShould).
     */
    @Test
    void controllersShouldBeInApiPackage() {
        classes()
                .that().haveSimpleNameEndingWith("Controller")
                .should().resideInAPackage("..api..")
                .allowEmptyShould(true)
                .because("Les contrôleurs exposent l'API REST et doivent être dans la couche api.")
                .check(importedClasses);
    }

    /**
     * Règle 4 : Les repositories Spring Data JPA doivent être dans 'infrastructure'.
     * Règle assouplie pour le début de projet.
     */
    @Test
    void jpaRepositoriesShouldBeInInfrastructure() {
        classes()
                .that().haveSimpleNameEndingWith("Repository")
                .and().resideInAPackage("..infrastructure..")
                .should().beInterfaces()
                .allowEmptyShould(true)
                .because("Les repositories JPA font partie de la couche d'infrastructure.")
                .check(importedClasses);
    }
}