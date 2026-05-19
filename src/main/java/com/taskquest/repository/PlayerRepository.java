package com.taskquest.repository;

import com.taskquest.exception.DataCorruptedException;
import com.taskquest.exception.PlayerNotFoundException;
import com.taskquest.model.Player;

/**
 * Interface définissant le contrat d'accès aux données du joueur.
 *
 * <p>Le joueur est unique dans l'application. Les implémentations
 * (JSON, SQLite…) doivent gérer le cas où aucun profil n'existe encore.</p>
 */
public interface PlayerRepository {

    /**
     * Charge le profil du joueur depuis la source de données.
     *
     * @return le joueur chargé
     * @throws PlayerNotFoundException si aucun profil n'est trouvé
     * @throws DataCorruptedException  si les données sont illisibles ou corrompues
     */
    Player load() throws PlayerNotFoundException, DataCorruptedException;

    /**
     * Sauvegarde le profil du joueur dans la source de données.
     *
     * @param player le joueur à persister
     * @throws DataCorruptedException si l'écriture échoue
     */
    void save(Player player) throws DataCorruptedException;
}
