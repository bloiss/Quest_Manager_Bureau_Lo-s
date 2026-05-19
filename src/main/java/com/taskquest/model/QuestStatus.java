package com.taskquest.model;

/**
 * Énumération représentant les états possibles d'une quête.
 *
 * <p>Une quête suit un cycle de vie linéaire :
 * {@code TODO} → {@code IN_PROGRESS} → {@code DONE}</p>
 */
public enum QuestStatus {

    /** La quête est créée mais pas encore commencée. */
    TODO,

    /** La quête est en cours de réalisation. */
    IN_PROGRESS,

    /** La quête est terminée et l'XP a été attribuée. */
    DONE
}
