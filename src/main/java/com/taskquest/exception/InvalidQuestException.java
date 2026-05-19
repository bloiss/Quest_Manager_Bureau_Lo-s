package com.taskquest.exception;

/**
 * Exception levée lorsqu'une quête contient des données invalides.
 *
 * <p>Cas d'usage typiques :</p>
 * <ul>
 *   <li>Titre vide ou trop long</li>
 *   <li>Description trop longue</li>
 *   <li>Récompense XP négative, nulle ou supérieure au maximum autorisé</li>
 *   <li>Tentative de compléter une quête déjà terminée</li>
 * </ul>
 */
public class InvalidQuestException extends Exception {

    /**
     * Construit une exception avec un message descriptif.
     *
     * @param message le message expliquant la cause de l'invalidité
     */
    public InvalidQuestException(String message) {
        super(message);
    }

    /**
     * Construit une exception avec un message et la cause originale.
     *
     * @param message le message descriptif
     * @param cause   l'exception originale ayant causé celle-ci
     */
    public InvalidQuestException(String message, Throwable cause) {
        super(message, cause);
    }
}
