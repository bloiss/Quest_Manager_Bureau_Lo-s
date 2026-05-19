package com.taskquest;

import com.taskquest.controller.PlayerController;
import com.taskquest.controller.QuestController;
import com.taskquest.exception.DataCorruptedException;
import com.taskquest.exception.InvalidQuestException;
import com.taskquest.repository.JsonPlayerRepository;
import com.taskquest.repository.JsonQuestRepository;
import com.taskquest.view.MainWindow;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Point d'entrée de l'application TaskQuest.
 *
 * <p>Responsabilités au démarrage :</p>
 * <ol>
 *   <li>Initialiser les repositories (JSON)</li>
 *   <li>Initialiser les contrôleurs</li>
 *   <li>Charger les données persistées</li>
 *   <li>Créer un profil joueur si premier lancement</li>
 *   <li>Lancer l'interface graphique Swing sur l'EDT</li>
 * </ol>
 */
public class Main {

    /** Dossier de stockage des données de l'application. */
    private static final Path DATA_DIR = Paths.get("data");

    /** Fichier de persistance des quêtes. */
    private static final Path QUESTS_FILE = DATA_DIR.resolve("quests.json");

    /** Fichier de persistance du joueur. */
    private static final Path PLAYER_FILE = DATA_DIR.resolve("player.json");

    /**
     * Point d'entrée principal de l'application.
     *
     * @param args arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        // Initialisation des repositories
        JsonPlayerRepository playerRepo = new JsonPlayerRepository(PLAYER_FILE);
        JsonQuestRepository questRepo = new JsonQuestRepository(QUESTS_FILE);

        // Initialisation des contrôleurs
        PlayerController playerController = new PlayerController(playerRepo);
        QuestController questController = new QuestController(questRepo, playerController);

        // Chargement des données
        try {
            boolean playerExists = playerController.loadPlayer();

            if (!playerExists) {
                // Premier lancement : demande du nom du joueur
                String name = promptPlayerName();
                if (name == null) {
                    System.exit(0); // L'utilisateur a annulé
                }
                playerController.createPlayer(name);
            }

            questController.loadQuests();

        } catch (DataCorruptedException e) {
            JOptionPane.showMessageDialog(null,
                "Erreur de chargement des données :\n" + e.getMessage() +
                "\n\nL'application va démarrer avec des données vides.",
                "Données corrompues", JOptionPane.WARNING_MESSAGE);
        } catch (InvalidQuestException e) {
            JOptionPane.showMessageDialog(null,
                "Nom de joueur invalide : " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Lancement de l'interface graphique sur l'Event Dispatch Thread
        final PlayerController finalPlayerController = playerController;
        final QuestController finalQuestController = questController;

        SwingUtilities.invokeLater(() -> {
            // Application du look & feel système
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Look & feel par défaut si indisponible
            }

            MainWindow window = new MainWindow(finalQuestController, finalPlayerController);
            window.setVisible(true);
        });
    }

    /**
     * Affiche une boîte de dialogue pour demander le nom du joueur lors du premier lancement.
     *
     * @return le nom saisi, ou {@code null} si l'utilisateur a annulé
     */
    private static String promptPlayerName() {
        String name = null;
        while (name == null || name.isBlank()) {
            name = JOptionPane.showInputDialog(null,
                "Bienvenue dans TaskQuest !\n\nEntrez le nom de votre aventurier :",
                "Premier lancement", JOptionPane.QUESTION_MESSAGE);
            if (name == null) {
                return null; // Annulé
            }
            if (name.isBlank()) {
                JOptionPane.showMessageDialog(null,
                    "Le nom ne peut pas être vide.",
                    "Champ requis", JOptionPane.WARNING_MESSAGE);
            }
        }
        return name;
    }
}
