package com.taskquest.model;

/**
 * Représente une quête à accomplir une seule fois.
 *
 * <p>Une fois marquée {@link QuestStatus#DONE}, une quête unique
 * ne peut plus être réinitialisée ni recomplétée.</p>
 */
public class OneTimeQuest extends Quest {

    /**
     * Construit une nouvelle quête unique.
     *
     * @param title       le titre de la quête
     * @param description la description de la quête
     * @param xpReward    la récompense XP (entre 1 et {@value Quest#MAX_XP_REWARD})
     */
    public OneTimeQuest(String title, String description, int xpReward) {
        super(title, description, xpReward);
    }

    /**
     * Constructeur de désérialisation (chargement depuis fichier).
     *
     * @param id          l'identifiant existant
     * @param title       le titre
     * @param description la description
     * @param xpReward    la récompense XP
     * @param status      le statut actuel
     */
    public OneTimeQuest(String id, String title, String description, int xpReward, QuestStatus status) {
        super(id, title, description, xpReward, status);
    }

    /**
     * Une quête unique n'est pas récurrente.
     *
     * @return {@code false} toujours
     */
    @Override
    public boolean isRecurring() {
        return false;
    }

    /**
     * La réinitialisation n'a pas d'effet sur une quête unique.
     * Cette méthode est présente par contrat mais ne fait rien.
     */
    @Override
    public void reset() {
        // Une quête unique ne se réinitialise pas
    }
}
