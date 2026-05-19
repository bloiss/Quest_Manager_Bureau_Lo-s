package com.taskquest.view;

import com.taskquest.controller.QuestController;
import com.taskquest.exception.DataCorruptedException;
import com.taskquest.exception.InvalidQuestException;
import com.taskquest.model.Quest;
import com.taskquest.model.QuestStatus;
import com.taskquest.model.Reward;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panneau Swing gérant l'affichage et les interactions avec les quêtes.
 *
 * <p>Permet à l'utilisateur de :</p>
 * <ul>
 *   <li>Voir la liste des quêtes avec leur statut</li>
 *   <li>Créer une nouvelle quête via un formulaire</li>
 *   <li>Marquer une quête comme terminée</li>
 *   <li>Supprimer une quête</li>
 * </ul>
 *
 * <p>Toutes les actions passent par le {@link QuestController} — jamais directement
 * au repository.</p>
 */
public class QuestPanel extends JPanel {

    /** Contrôleur des quêtes. */
    private final QuestController questController;

    /** Référence à la fenêtre principale pour les notifications et rafraîchissements. */
    private final MainWindow mainWindow;

    /** Modèle de données du tableau des quêtes. */
    private DefaultTableModel tableModel;

    /** Tableau d'affichage des quêtes. */
    private JTable questTable;

    /** Cache de la liste des quêtes pour les actions (complétion, suppression). */
    private List<Quest> currentQuests;

    // Noms des colonnes du tableau
    private static final String[] COLUMN_NAMES = {"Titre", "Type", "XP", "Statut"};

    /**
     * Construit le panneau de gestion des quêtes.
     *
     * @param questController le contrôleur des quêtes
     * @param mainWindow      la fenêtre principale parente
     */
    public QuestPanel(QuestController questController, MainWindow mainWindow) {
        this.questController = questController;
        this.mainWindow = mainWindow;
        initComponents();
        refresh(questController.getAllQuests());
    }

    /**
     * Initialise et dispose les composants graphiques du panneau.
     */
    private void initComponents() {
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Mes Quêtes",
            TitledBorder.LEFT, TitledBorder.TOP));
        setLayout(new BorderLayout(8, 8));

        // --- Tableau des quêtes ---
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tableau en lecture seule
            }
        };
        questTable = new JTable(tableModel);
        questTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        questTable.setRowHeight(24);
        questTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(questTable);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        add(scrollPane, BorderLayout.CENTER);

        // --- Panneau de boutons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));

        JButton btnCreate = new JButton("Nouvelle quête");
        JButton btnComplete = new JButton("Terminer");
        JButton btnDelete = new JButton("Supprimer");

        btnCreate.addActionListener(e -> openCreateDialog());
        btnComplete.addActionListener(e -> completeSelectedQuest());
        btnDelete.addActionListener(e -> deleteSelectedQuest());

        buttonPanel.add(btnCreate);
        buttonPanel.add(btnComplete);
        buttonPanel.add(btnDelete);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Met à jour le tableau avec la liste des quêtes fournie.
     *
     * @param quests la liste des quêtes à afficher
     */
    public void refresh(List<Quest> quests) {
        this.currentQuests = quests;
        tableModel.setRowCount(0);

        for (Quest quest : quests) {
            String type = quest.isRecurring() ? "Quotidienne" : "Unique";
            tableModel.addRow(new Object[]{
                quest.getTitle(),
                type,
                quest.getXpReward() + " XP",
                formatStatus(quest.getStatus())
            });
        }
    }

    /**
     * Ouvre la boîte de dialogue de création d'une nouvelle quête.
     */
    private void openCreateDialog() {
        DialogForm dialog = new DialogForm(mainWindow);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                questController.createQuest(
                    dialog.getQuestTitle(),
                    dialog.getQuestDescription(),
                    dialog.getXpReward(),
                    dialog.isDaily()
                );
                mainWindow.refreshQuestPanel();
            } catch (InvalidQuestException e) {
                mainWindow.showError(e.getMessage());
            } catch (DataCorruptedException e) {
                mainWindow.showError("Erreur de sauvegarde : " + e.getMessage());
            }
        }
    }

    /**
     * Complète la quête sélectionnée dans le tableau.
     */
    private void completeSelectedQuest() {
        int selectedRow = questTable.getSelectedRow();
        if (selectedRow < 0) {
            mainWindow.showInfo("Veuillez sélectionner une quête à terminer.");
            return;
        }

        Quest quest = currentQuests.get(selectedRow);
        try {
            List<Reward> rewards = questController.completeQuest(quest.getId());
            mainWindow.refreshQuestPanel();
            mainWindow.refreshPlayerPanel();
            mainWindow.showRewardDialog(rewards);
        } catch (InvalidQuestException e) {
            mainWindow.showError(e.getMessage());
        } catch (DataCorruptedException e) {
            mainWindow.showError("Erreur de sauvegarde : " + e.getMessage());
        }
    }

    /**
     * Supprime la quête sélectionnée dans le tableau après confirmation.
     */
    private void deleteSelectedQuest() {
        int selectedRow = questTable.getSelectedRow();
        if (selectedRow < 0) {
            mainWindow.showInfo("Veuillez sélectionner une quête à supprimer.");
            return;
        }

        Quest quest = currentQuests.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(
            mainWindow,
            "Supprimer la quête \"" + quest.getTitle() + "\" ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                questController.deleteQuest(quest.getId());
                mainWindow.refreshQuestPanel();
            } catch (InvalidQuestException e) {
                mainWindow.showError(e.getMessage());
            } catch (DataCorruptedException e) {
                mainWindow.showError("Erreur de sauvegarde : " + e.getMessage());
            }
        }
    }

    /**
     * Formate un statut de quête en texte lisible pour l'affichage.
     *
     * @param status le statut à formater
     * @return le libellé correspondant
     */
    private String formatStatus(QuestStatus status) {
        return switch (status) {
            case TODO -> "À faire";
            case IN_PROGRESS -> "En cours";
            case DONE -> "Terminée";
        };
    }
}
