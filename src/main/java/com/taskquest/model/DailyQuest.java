package com.taskquest.model;

import java.time.LocalDate;

/**
 * Représente une quête quotidienne récurrente.
 *
 * <p>Une quête quotidienne se réinitialise automatiquement chaque jour :
 * si la date de dernière complétion est différente d'aujourd'hui,
 * son statut repasse à {@link QuestStatus#TODO}.</p>
 */
public class DailyQuest extends Quest {

    /** Date de la dernière complétion de cette quête (peut être null si jamais complétée). */
    private LocalDate lastCompletedDate;

    /**
     * Construit une nouvelle quête quotidienne.
     *
     * @param title       le titre de la quête
     * @param description la description de la quête
     * @param xpReward    la récompense XP (entre 1 et {@value Quest#MAX_XP_REWARD})
     */
    public DailyQuest(String title, String description, int xpReward) {
        super(title, description, xpReward);
        this.lastCompletedDate = null;
    }

    /**
     * Constructeur de désérialisation (chargement depuis fichier).
     *
     * @param id                l'identifiant existant
     * @param title             le titre
     * @param description       la description
     * @param xpReward          la récompense XP
     * @param status            le statut actuel
     * @param lastCompletedDate la date de dernière complétion (peut être null)
     */
    public DailyQuest(String id, String title, String description, int xpReward,
                      QuestStatus status, LocalDate lastCompletedDate) {
        super(id, title, description, xpReward, status);
        this.lastCompletedDate = lastCompletedDate;
    }

    /**
     * Une quête quotidienne est récurrente.
     *
     * @return {@code true} toujours
     */
    @Override
    public boolean isRecurring() {
        return true;
    }

    /**
     * Réinitialise la quête à {@link QuestStatus#TODO} si elle a déjà été complétée
     * un jour précédent. Cette méthode est appelée au démarrage de l'application.
     */
    @Override
    public void reset() {
        if (lastCompletedDate != null && !lastCompletedDate.equals(LocalDate.now())) {
            setStatus(QuestStatus.TODO);
        }
    }

    /**
     * Marque la quête comme complétée à la date d'aujourd'hui.
     * À appeler en même temps que {@code setStatus(QuestStatus.DONE)}.
     */
    public void markCompletedToday() {
        this.lastCompletedDate = LocalDate.now();
    }

    /**
     * Retourne la date de dernière complétion de cette quête.
     *
     * @return la date de dernière complétion, ou {@code null} si jamais complétée
     */
    public LocalDate getLastCompletedDate() {
        return lastCompletedDate;
    }
}
