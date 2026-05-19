package com.taskquest.controller;

import com.taskquest.exception.DataCorruptedException;
import com.taskquest.exception.InvalidQuestException;
import com.taskquest.exception.PlayerNotFoundException;
import com.taskquest.model.Player;
import com.taskquest.model.Reward;
import com.taskquest.repository.PlayerRepository;

import java.util.List;

/**
 * Contrôleur gérant le profil du joueur et sa progression.
 *
 * <p>Il est responsable du chargement et de la sauvegarde du joueur,
 * ainsi que de l'attribution des points d'expérience. Il est utilisé par
 * le {@link QuestController} lors de la complétion d'une quête.</p>
 */
public class PlayerController {

    /** Repository d'accès aux données du joueur. */
    private final PlayerRepository playerRepository;

    /** Instance du joueur chargée en mémoire. */
    private Player player;

    /**
     * Construit le contrôleur du joueur.
     *
     * @param playerRepository le repository de persistance du joueur
     */
    public PlayerController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /**
     * Charge le profil du joueur depuis la persistance.
     * Si aucun profil n'existe, retourne {@code false} pour indiquer
     * qu'une création de profil est nécessaire.
     *
     * @return {@code true} si un profil existant a été chargé,
     *         {@code false} si c'est le premier lancement
     * @throws DataCorruptedException si les données sont corrompues
     */
    public boolean loadPlayer() throws DataCorruptedException {
        try {
            this.player = playerRepository.load();
            return true;
        } catch (PlayerNotFoundException e) {
            return false;
        }
    }

    /**
     * Crée un nouveau profil joueur avec le nom donné et le sauvegarde.
     *
     * @param name le nom du joueur (non vide)
     * @throws InvalidQuestException  si le nom est invalide
     * @throws DataCorruptedException si la sauvegarde échoue
     */
    public void createPlayer(String name) throws InvalidQuestException, DataCorruptedException {
        if (name == null || name.isBlank()) {
            throw new InvalidQuestException("Le nom du joueur ne peut pas être vide.");
        }
        if (name.trim().length() > 50) {
            throw new InvalidQuestException("Le nom du joueur ne peut pas dépasser 50 caractères.");
        }
        this.player = new Player(name.trim());
        playerRepository.save(player);
    }

    /**
     * Attribue des points d'expérience au joueur et sauvegarde son profil.
     *
     * @param amount le nombre de points XP à ajouter
     * @return la liste des récompenses débloquées (peut être vide)
     * @throws DataCorruptedException si la sauvegarde échoue
     */
    public List<Reward> addXP(int amount) throws DataCorruptedException {
        List<Reward> rewards = player.addXP(amount);
        playerRepository.save(player);
        return rewards;
    }

    /**
     * Sauvegarde le profil du joueur.
     * À appeler à la fermeture de l'application.
     *
     * @throws DataCorruptedException si la sauvegarde échoue
     */
    public void savePlayer() throws DataCorruptedException {
        playerRepository.save(player);
    }

    /**
     * Retourne le joueur actuellement chargé.
     *
     * @return le joueur, ou {@code null} si aucun profil n'a été chargé
     */
    public Player getPlayer() {
        return player;
    }
}
