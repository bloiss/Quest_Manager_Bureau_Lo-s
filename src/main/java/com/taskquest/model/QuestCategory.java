package com.taskquest.model;

/**
 * Catégories disponibles pour classifier les quêtes.
 *
 * <p>Chaque catégorie possède un libellé lisible pour l'affichage dans l'interface.</p>
 */
public enum QuestCategory {

    /** Catégorie générale (par défaut). */
    GENERAL("Général"),

    /** Quêtes liées au travail ou aux études. */
    TRAVAIL("Travail"),

    /** Quêtes de développement personnel. */
    PERSONNEL("Personnel"),

    /** Quêtes liées à l'apprentissage ou à la formation. */
    APPRENTISSAGE("Apprentissage"),

    /** Quêtes liées à la santé et au sport. */
    SANTE("Santé"),

    /** Quêtes de loisirs et de divertissement. */
    LOISIRS("Loisirs");

    /** Libellé affiché dans l'interface. */
    private final String label;

    /**
     * Construit une catégorie avec son libellé d'affichage.
     *
     * @param label le texte affiché à l'utilisateur
     */
    QuestCategory(String label) {
        this.label = label;
    }

    /**
     * Retourne le libellé lisible de la catégorie.
     *
     * @return le libellé
     */
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
