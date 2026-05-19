package com.taskquest.exception;

/**
 * Exception levée lorsque le fichier de données est absent, illisible ou corrompu.
 *
 * <p>Cette exception est utilisée par la couche {@code repository} pour signaler
 * tout problème d'intégrité lors de la lecture ou de l'écriture des données persistées.</p>
 *
 * <p>L'application doit toujours gérer cette exception gracieusement en proposant
 * un état initial plutôt qu'en crashant.</p>
 */
public class DataCorruptedException extends Exception {

    /**
     * Construit une exception avec un message descriptif.
     *
     * @param message le message expliquant la nature de la corruption
     */
    public DataCorruptedException(String message) {
        super(message);
    }

    /**
     * Construit une exception avec un message et la cause originale.
     *
     * @param message le message descriptif
     * @param cause   l'exception d'I/O ou de parsing originale
     */
    public DataCorruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
