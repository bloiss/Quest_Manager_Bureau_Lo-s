package com.taskquest.model;

/**
 * Représente une récompense (titre) débloquée par le joueur lorsqu'il atteint
 * un certain niveau.
 *
 * <p>Les récompenses sont prédéfinies et associées à des paliers de niveau.
 * Elles sont immuables une fois créées.</p>
 */
public class Reward {

    /** Niveau requis pour débloquer cette récompense. */
    private final int requiredLevel;

    /** Libellé du titre accordé au joueur. */
    private final String title;

    /**
     * Construit une récompense associée à un palier de niveau.
     *
     * @param requiredLevel le niveau minimum pour débloquer ce titre
     * @param title         le libellé du titre accordé
     */
    public Reward(int requiredLevel, String title) {
        this.requiredLevel = requiredLevel;
        this.title = title;
    }

    /**
     * Retourne le niveau requis pour débloquer cette récompense.
     *
     * @return le niveau minimum requis
     */
    public int getRequiredLevel() {
        return requiredLevel;
    }

    /**
     * Retourne le libellé du titre accordé.
     *
     * @return le titre de la récompense
     */
    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return String.format("Niveau %d : %s", requiredLevel, title);
    }
}
