package com.taskquest.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Classe abstraite représentant une quête dans le système TaskQuest.
 *
 * <p>Une quête possède un titre, une description, une récompense en XP
 * et un statut. Les sous-classes définissent le comportement spécifique
 * (quête unique ou quotidienne).</p>
 *
 * <p>Contraintes de validation :</p>
 * <ul>
 *   <li>Le titre ne peut pas être vide et est limité à {@value #MAX_TITLE_LENGTH} caractères.</li>
 *   <li>La description est limitée à {@value #MAX_DESCRIPTION_LENGTH} caractères.</li>
 *   <li>La récompense XP doit être comprise entre 1 et {@value #MAX_XP_REWARD}.</li>
 * </ul>
 */
public abstract class Quest {

    /** Longueur maximale du titre d'une quête. */
    public static final int MAX_TITLE_LENGTH = 100;

    /** Longueur maximale de la description d'une quête. */
    public static final int MAX_DESCRIPTION_LENGTH = 500;

    /** Valeur maximale autorisée pour la récompense XP. */
    public static final int MAX_XP_REWARD = 1000;

    /** Identifiant unique de la quête (généré automatiquement). */
    private final String id;

    /** Titre de la quête. */
    private String title;

    /** Description détaillée de la quête. */
    private String description;

    /** Points d'expérience accordés à la complétion. */
    private int xpReward;

    /** Statut actuel de la quête. */
    private QuestStatus status;

    /** Date et heure de complétion (null si pas encore terminée). */
    private LocalDateTime completedAt;
    /**
     * Construit une nouvelle quête avec les paramètres donnés.
     *
     * <p>Les paramètres sont supposés déjà validés par le contrôleur.
     * L'identifiant est généré automatiquement.</p>
     *
     * @param title       le titre de la quête (non vide, max {@value #MAX_TITLE_LENGTH} chars)
     * @param description la description de la quête (max {@value #MAX_DESCRIPTION_LENGTH} chars)
     * @param xpReward    la récompense XP (entre 1 et {@value #MAX_XP_REWARD})
     */
    protected Quest(String title, String description, int xpReward) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.xpReward = xpReward;
        this.status = QuestStatus.TODO;
    }

    /**
     * Constructeur utilisé lors de la désérialisation (chargement depuis fichier).
     *
     * @param id          l'identifiant existant de la quête
     * @param title       le titre de la quête
     * @param description la description de la quête
     * @param xpReward    la récompense XP
     * @param status      le statut actuel de la quête
     */
    protected Quest(String id, String title, String description, int xpReward, QuestStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.xpReward = xpReward;
        this.status = status;
    }

    /**
     * Indique si cette quête doit être réinitialisée automatiquement chaque jour.
     *
     * @return {@code true} si la quête est quotidienne, {@code false} sinon
     */
    public abstract boolean isRecurring();

    /**
     * Réinitialise la quête à son état initial ({@link QuestStatus#TODO}).
     * Cette opération n'est significative que pour les quêtes récurrentes.
     */
    public abstract void reset();

    // --- Getters ---

    /**
     * Retourne l'identifiant unique de la quête.
     *
     * @return l'identifiant UUID sous forme de chaîne
     */
    public String getId() {
        return id;
    }

    /**
     * Retourne le titre de la quête.
     *
     * @return le titre
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retourne la description de la quête.
     *
     * @return la description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retourne la récompense XP de la quête.
     *
     * @return les points d'expérience accordés à la complétion
     */
    public int getXpReward() {
        return xpReward;
    }

    /**
     * Retourne le statut actuel de la quête.
     *
     * @return le statut ({@link QuestStatus})
     */
    public QuestStatus getStatus() {
        return status;
    }

    // --- Setters ---

    /**
     * Modifie le titre de la quête.
     *
     * @param title le nouveau titre (non vide, max {@value #MAX_TITLE_LENGTH} chars)
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Modifie la description de la quête.
     *
     * @param description la nouvelle description (max {@value #MAX_DESCRIPTION_LENGTH} chars)
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Modifie la récompense XP de la quête.
     *
     * @param xpReward la nouvelle valeur XP (entre 1 et {@value #MAX_XP_REWARD})
     */
    public void setXpReward(int xpReward) {
        this.xpReward = xpReward;
    }

    /**
     * Modifie le statut de la quête.
     *
     * @param status le nouveau statut
     */
    public void setStatus(QuestStatus status) {
        this.status = status;
    }

    /**
     * Retourne la date et heure de complétion de la quête.
     *
     * @return la date/heure de complétion, ou {@code null} si non terminée
     */
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    /**
     * Définit la date et heure de complétion.
     *
     * @param completedAt la date/heure de complétion
     */
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%d XP) - %s", getClass().getSimpleName(), title, xpReward, status);
    }
}
