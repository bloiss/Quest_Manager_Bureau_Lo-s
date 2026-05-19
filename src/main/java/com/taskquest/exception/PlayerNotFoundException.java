package com.taskquest.exception;

/**
 * Exception levée lorsque le profil joueur est introuvable dans les données persistées.
 *
 * <p>Typiquement levée au premier lancement de l'application ou si le fichier
 * de sauvegarde du joueur a été supprimé manuellement.</p>
 */
public class PlayerNotFoundException extends Exception {

    /**
     * Construit une exception avec un message descriptif.
     *
     * @param message le message expliquant pourquoi le joueur est introuvable
     */
    public PlayerNotFoundException(String message) {
        super(message);
    }

    /**
     * Construit une exception avec un message et la cause originale.
     *
     * @param message le message descriptif
     * @param cause   l'exception originale ayant causé celle-ci
     */
    public PlayerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
