package com.taskquest.view;

import com.taskquest.controller.PlayerController;
import com.taskquest.controller.QuestController;
import com.taskquest.exception.DataCorruptedException;
import com.taskquest.model.Player;
import com.taskquest.model.Quest;
import com.taskquest.model.Reward;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Fenêtre principale de l'application TaskQuest.
 *
 * <p>Elle orchestre les deux panneaux principaux :</p>
 * <ul>
 *   <li>{@link PlayerPanel} — affichage du profil et de la progression du joueur</li>
 *   <li>{@link QuestPanel} — liste et gestion des quêtes</li>
 * </ul>
 *
 * <p>La fenêtre intercepte l'événement de fermeture pour déclencher
 * la sauvegarde automatique des données.</p>
 */
public class MainWindow extends JFrame {

    /** Contrôleur des quêtes. */
    private final QuestController questController;

    /** Contrôleur du joueur. */
    private final PlayerController playerController;

    /** Panneau d'affichage du profil joueur. */
    private PlayerPanel playerPanel;

    /** Panneau de gestion des quêtes. */
    private QuestPanel questPanel;

    /**
     * Construit la fenêtre principale et initialise tous les composants graphiques.
     *
     * @param questController  le contrôleur des quêtes
     * @param playerController le contrôleur du joueur
     */
    public MainWindow(QuestController questController, PlayerController playerController) {
        this.questController = questController;
        this.playerController = playerController;

        setTitle("TaskQuest — Gestionnaire de quêtes RPG");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        initComponents();
        initCloseHandler();
    }

    /**
     * Initialise et place les composants graphiques dans la fenêtre.
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        playerPanel = new PlayerPanel(playerController.getPlayer());
        questPanel = new QuestPanel(questController, this);

        add(playerPanel, BorderLayout.NORTH);
        add(questPanel, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.add(new JLabel("TaskQuest v1.0 — Bonne quête !"));
        add(statusBar, BorderLayout.SOUTH);

        pack();
    }

    /**
     * Enregistre le gestionnaire de fermeture pour sauvegarder les données
     * avant de quitter l'application.
     */
    private void initCloseHandler() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                saveAndExit();
            }
        });
    }

    /**
     * Sauvegarde les données et ferme l'application.
     * Affiche un message d'erreur si la sauvegarde échoue.
     */
    private void saveAndExit() {
        try {
            questController.saveQuests();
            playerController.savePlayer();
        } catch (DataCorruptedException e) {
            showError("Erreur lors de la sauvegarde : " + e.getMessage());
        }
        dispose();
        System.exit(0);
    }

    /**
     * Met à jour l'affichage du profil joueur (appelé après un gain d'XP).
     */
    public void refreshPlayerPanel() {
        playerPanel.refresh(playerController.getPlayer());
    }

    /**
     * Met à jour la liste des quêtes affichées.
     */
    public void refreshQuestPanel() {
        questPanel.refresh(questController.getAllQuests());
    }

    /**
     * Affiche une boîte de dialogue annonçant les récompenses débloquées.
     *
     * @param rewards la liste des récompenses à annoncer
     */
    public void showRewardDialog(List<Reward> rewards) {
        if (rewards == null || rewards.isEmpty()) {
            return;
        }
        StringBuilder sb = new StringBuilder("Niveau supérieur !\n\n");
        for (Reward reward : rewards) {
            sb.append("Nouveau titre : ").append(reward.getTitle()).append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(),
            "Récompense débloquée !", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Affiche un message d'erreur dans une boîte de dialogue.
     *
     * @param message le message d'erreur à afficher
     */
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message,
            "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Affiche un message d'information dans une boîte de dialogue.
     *
     * @param message le message à afficher
     */
    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message,
            "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}
