package com.taskquest.controller;

import com.taskquest.exception.DataCorruptedException;
import com.taskquest.exception.InvalidQuestException;
import com.taskquest.model.*;
import com.taskquest.repository.QuestRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrôleur gérant toutes les opérations sur les quêtes.
 *
 * <p>C'est le seul point d'entrée de la vue vers la logique métier des quêtes.
 * Il valide les données entrantes, délègue la persistance au {@link QuestRepository}
 * et notifie le {@link PlayerController} lors de la complétion d'une quête.</p>
 *
 * <p>Aucune vue ne doit accéder directement au repository.</p>
 */
public class QuestController {

    /** Repository d'accès aux données des quêtes. */
    private final QuestRepository questRepository;

    /** Contrôleur du joueur, notifié lors de la complétion d'une quête. */
    private final PlayerController playerController;

    /** Cache en mémoire de la liste des quêtes (chargé au démarrage). */
    private List<Quest> quests;

    /**
     * Construit le contrôleur de quêtes.
     *
     * @param questRepository  le repository de persistance des quêtes
     * @param playerController le contrôleur du joueur pour l'attribution des XP
     */
    public QuestController(QuestRepository questRepository, PlayerController playerController) {
        this.questRepository = questRepository;
        this.playerController = playerController;
    }

    /**
     * Charge les quêtes depuis la persistance et réinitialise les quêtes quotidiennes
     * si nécessaire. Doit être appelé au démarrage de l'application.
     *
     * @throws DataCorruptedException si les données sont corrompues
     */
    public void loadQuests() throws DataCorruptedException {
        this.quests = questRepository.loadAll();
        // Réinitialisation des quêtes quotidiennes dont la date a changé
        for (Quest quest : quests) {
            if (quest.isRecurring()) {
                quest.reset();
            }
        }
    }

    /**
     * Retourne la liste de toutes les quêtes chargées en mémoire.
     *
     * @return la liste des quêtes (jamais null)
     */
    public List<Quest> getAllQuests() {
        return List.copyOf(quests);
    }
/**
 * Retourne les quêtes filtrées par statut et triées selon un critère.
 *
 * @param statusFilter le statut souhaité, ou {@code null} pour toutes les quêtes
 * @param sortBy       "title", "xp", "type", ou {@code null} pour l'ordre naturel
 * @return la liste filtrée et triée
 */
public List<Quest> getFilteredAndSorted(QuestStatus statusFilter, String sortBy) {
    List<Quest> result = quests.stream()
        .filter(q -> statusFilter == null || q.getStatus() == statusFilter)
        .collect(java.util.stream.Collectors.toList());

    if ("title".equals(sortBy)) {
        result.sort(java.util.Comparator.comparing(Quest::getTitle,
            String.CASE_INSENSITIVE_ORDER));
    } else if ("xp".equals(sortBy)) {
        result.sort(java.util.Comparator.comparingInt(Quest::getXpReward).reversed());
    } else if ("type".equals(sortBy)) {
        result.sort(java.util.Comparator.comparing(Quest::isRecurring));
    }

    return result;
}

    /**
     * Crée et ajoute une nouvelle quête à la liste, puis la persiste.
     *
     * @param title       le titre de la quête
     * @param description la description de la quête
     * @param xpReward    la récompense XP
     * @param isDaily     {@code true} pour une quête quotidienne, {@code false} pour unique
     * @throws InvalidQuestException  si les données sont invalides
     * @throws DataCorruptedException si la sauvegarde échoue
     */
    public void createQuest(String title, String description, int xpReward, boolean isDaily)
            throws InvalidQuestException, DataCorruptedException {

        validateQuestData(title, description, xpReward);

        Quest quest = isDaily
            ? new DailyQuest(title.trim(), description.trim(), xpReward)
            : new OneTimeQuest(title.trim(), description.trim(), xpReward);

        quests.add(quest);
        questRepository.saveAll(quests);
    }

    /**
     * Marque une quête comme terminée, attribue l'XP au joueur et sauvegarde.
     *
     * @param questId l'identifiant de la quête à compléter
     * @return la liste des récompenses débloquées par le joueur (peut être vide)
     * @throws InvalidQuestException  si la quête est introuvable ou déjà terminée
     * @throws DataCorruptedException si la sauvegarde échoue
     */
    public List<Reward> completeQuest(String questId)
            throws InvalidQuestException, DataCorruptedException {

        Quest quest = findQuestById(questId);

        if (quest.getStatus() == QuestStatus.DONE) {
            throw new InvalidQuestException(
                "La quête \"" + quest.getTitle() + "\" est déjà terminée.");
        }

        quest.setStatus(QuestStatus.DONE);
        quest.setCompletedAt(LocalDateTime.now());
        if (quest instanceof DailyQuest daily) {
            daily.markCompletedToday();
        }

        List<Reward> rewards = playerController.addXP(quest.getXpReward());
        questRepository.saveAll(quests);

        return rewards;
    }

    /**
     * Supprime une quête de la liste et sauvegarde.
     *
     * @param questId l'identifiant de la quête à supprimer
     * @throws InvalidQuestException  si la quête est introuvable
     * @throws DataCorruptedException si la sauvegarde échoue
     */
    public void deleteQuest(String questId)
            throws InvalidQuestException, DataCorruptedException {

        Quest quest = findQuestById(questId);
        quests.remove(quest);
        questRepository.saveAll(quests);
    }

    /**
     * Sauvegarde l'état actuel de toutes les quêtes.
     * À appeler à la fermeture de l'application.
     *
     * @throws DataCorruptedException si la sauvegarde échoue
     */
    public void saveQuests() throws DataCorruptedException {
        questRepository.saveAll(quests);
    }

    /**
     * Recherche une quête par son identifiant dans la liste en mémoire.
     *
     * @param questId l'identifiant à rechercher
     * @return la quête trouvée
     * @throws InvalidQuestException si aucune quête ne correspond à cet identifiant
     */
    private Quest findQuestById(String questId) throws InvalidQuestException {
        return quests.stream()
            .filter(q -> q.getId().equals(questId))
            .findFirst()
            .orElseThrow(() -> new InvalidQuestException(
                "Quête introuvable avec l'identifiant : " + questId));
    }

    /**
     * Valide les données d'une quête avant création ou modification.
     *
     * @param title       le titre à valider
     * @param description la description à valider
     * @param xpReward    la récompense XP à valider
     * @throws InvalidQuestException si une règle de validation n'est pas respectée
     */
    private void validateQuestData(String title, String description, int xpReward)
            throws InvalidQuestException {

        if (title == null || title.isBlank()) {
            throw new InvalidQuestException("Le titre de la quête ne peut pas être vide.");
        }
        if (title.trim().length() > Quest.MAX_TITLE_LENGTH) {
            throw new InvalidQuestException(
                "Le titre ne peut pas dépasser " + Quest.MAX_TITLE_LENGTH + " caractères.");
        }
        if (description != null && description.length() > Quest.MAX_DESCRIPTION_LENGTH) {
            throw new InvalidQuestException(
                "La description ne peut pas dépasser " + Quest.MAX_DESCRIPTION_LENGTH + " caractères.");
        }
        if (xpReward <= 0) {
            throw new InvalidQuestException("La récompense XP doit être un entier strictement positif.");
        }
        if (xpReward > Quest.MAX_XP_REWARD) {
            throw new InvalidQuestException(
                "La récompense XP ne peut pas dépasser " + Quest.MAX_XP_REWARD + " points.");
        }
    }
}
