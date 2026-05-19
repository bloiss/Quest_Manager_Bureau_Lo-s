package com.taskquest.repository;

import com.taskquest.exception.DataCorruptedException;
import com.taskquest.model.Quest;

import java.util.List;

/**
 * Interface définissant le contrat d'accès aux données des quêtes.
 *
 * <p>Toute implémentation (JSON, SQLite…) doit respecter ce contrat.
 * La couche {@code controller} utilise uniquement cette interface,
 * ce qui permet de changer d'implémentation sans modifier le reste du code.</p>
 */
public interface QuestRepository {

    /**
     * Charge toutes les quêtes depuis la source de données.
     *
     * @return la liste de toutes les quêtes (vide si aucune)
     * @throws DataCorruptedException si les données sont illisibles ou corrompues
     */
    List<Quest> loadAll() throws DataCorruptedException;

    /**
     * Sauvegarde toutes les quêtes dans la source de données.
     * Remplace intégralement les données existantes.
     *
     * @param quests la liste des quêtes à persister
     * @throws DataCorruptedException si l'écriture échoue
     */
    void saveAll(List<Quest> quests) throws DataCorruptedException;
}
