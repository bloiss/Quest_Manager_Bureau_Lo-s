# TaskQuest

**Gestionnaire de tâches gamifié** — Projet Java B1 · Ynov Campus Rennes · S2 2025-2026

Chaque tâche accomplie est une quête. Chaque quête complétée rapporte de l'XP.  
Accumulez de l'XP, montez de niveau, et devenez une **Légende** !

---

## Fonctionnalités

- Créer des quêtes **uniques** ou **quotidiennes** avec titre, description et récompense XP
- Afficher la liste des quêtes avec leur statut (À faire / En cours / Terminée)
- Marquer une quête comme terminée → XP attribuée automatiquement
- Supprimer une quête
- Profil joueur : nom, niveau, XP courante, titre actuel, barre de progression
- Persistance automatique au lancement et à la fermeture de l'application

---

## Prérequis

- Java 17 ou supérieur
- Maven 3.8+

---

## Lancer l'application

```bash
# Compiler et packager
mvn package -q

# Lancer
java -jar target/taskquest.jar
```

Ou depuis un IDE (IntelliJ, Eclipse, VS Code) : exécuter la classe `com.taskquest.Main`.

---

## Générer la Javadoc

```bash
mvn javadoc:javadoc
```

La documentation est générée dans le dossier `doc/`.

---

## Structure du projet

```
src/main/java/com/taskquest/
├── Main.java                   ← Point d'entrée
├── model/                      ← Logique métier et données
│   ├── Quest.java              ← Classe abstraite
│   ├── OneTimeQuest.java       ← Quête unique
│   ├── DailyQuest.java         ← Quête quotidienne
│   ├── Player.java             ← Le joueur (niveaux, XP)
│   ├── Reward.java             ← Récompense par palier
│   └── QuestStatus.java        ← Enum TODO/IN_PROGRESS/DONE
├── view/                       ← Interface graphique Swing
│   ├── MainWindow.java
│   ├── PlayerPanel.java
│   ├── QuestPanel.java
│   └── DialogForm.java
├── controller/                 ← Coordination modèle ↔ vue
│   ├── QuestController.java
│   └── PlayerController.java
├── repository/                 ← Accès aux données (JSON)
│   ├── QuestRepository.java    ← Interface
│   ├── PlayerRepository.java   ← Interface
│   ├── JsonQuestRepository.java
│   └── JsonPlayerRepository.java
└── exception/                  ← Exceptions métier
    ├── InvalidQuestException.java
    ├── PlayerNotFoundException.java
    └── DataCorruptedException.java
```

---

## Choix techniques

| Aspect | Choix | Justification |
|--------|-------|---------------|
| Interface graphique | Swing | Intégré au JDK, aucune dépendance externe |
| Persistance | JSON (Gson) | Simple, lisible, facile à déboguer |
| Architecture | MVC stricte | Séparation claire des responsabilités |
| Java | 17 LTS | Switch expressions, records, text blocks |

---

## Niveaux et titres

| Niveau | Titre | XP requise |
|--------|-------|-----------|
| 1 | Novice | 0 |
| 2 | Apprenti | 100 |
| 3 | Développeur | 300 |
| 4 | Vétéran | 600 |
| 5 | Architecte | 1000 |
| 6 | Légende | 1500 |

---

*Projet réalisé dans le cadre du module Programmation Ytrack — Ynov Campus Rennes B1 S2 2025-2026*
