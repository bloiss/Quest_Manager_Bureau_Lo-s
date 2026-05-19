package com.taskquest.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Représente le joueur dans le système TaskQuest.
 *
 * <p>Le joueur accumule de l'XP en complétant des quêtes. À chaque palier,
 * il monte de niveau et débloque un nouveau titre (via {@link Reward}).</p>
 *
 * <p>Paliers de niveau prédéfinis :</p>
 * <ul>
 *   <li>Niveau 1 → Novice</li>
 *   <li>Niveau 2 → Apprenti (100 XP)</li>
 *   <li>Niveau 3 → Développeur (300 XP)</li>
 *   <li>Niveau 4 → Vétéran (600 XP)</li>
 *   <li>Niveau 5 → Architecte (1000 XP)</li>
 *   <li>Niveau 6 → Légende (1500 XP)</li>
 * </ul>
 */
public class Player {

    /** Niveau maximum atteignable par le joueur. */
    public static final int MAX_LEVEL = 6;

    /** Quantité d'XP nécessaire par palier de niveau. */
    private static final int[] XP_THRESHOLDS = {0, 100, 300, 600, 1000, 1500};

    /** Liste immuable des récompenses associées à chaque palier. */
    private static final List<Reward> REWARDS = List.of(
        new Reward(1, "Novice"),
        new Reward(2, "Apprenti"),
        new Reward(3, "Développeur"),
        new Reward(4, "Vétéran"),
        new Reward(5, "Architecte"),
        new Reward(6, "Légende")
    );

    /** Nom du joueur. */
    private String name;

    /** Niveau actuel du joueur (entre 1 et {@value #MAX_LEVEL}). */
    private int level;

    /** XP accumulée depuis le début du niveau actuel. */
    private int currentXP;

    /** XP totale accumulée depuis la création du personnage. */
    private int totalXP;

    /** Titre actuel du joueur. */
    private String title;

    /**
     * Construit un nouveau joueur au niveau 1 avec 0 XP.
     *
     * @param name le nom du joueur (non vide)
     */
    public Player(String name) {
        this.name = name;
        this.level = 1;
        this.currentXP = 0;
        this.totalXP = 0;
        this.title = REWARDS.get(0).getTitle();
    }

    /**
     * Constructeur de désérialisation (chargement depuis fichier).
     *
     * @param name      le nom du joueur
     * @param level     le niveau actuel
     * @param currentXP l'XP dans le niveau actuel
     * @param totalXP   l'XP totale accumulée
     * @param title     le titre actuel
     */
    public Player(String name, int level, int currentXP, int totalXP, String title) {
        this.name = name;
        this.level = level;
        this.currentXP = currentXP;
        this.totalXP = totalXP;
        this.title = title;
    }

    /**
     * Ajoute des points d'expérience au joueur et déclenche les montées de niveau
     * si les seuils sont atteints.
     *
     * @param amount le nombre de points XP à ajouter (doit être positif)
     * @return la liste des récompenses débloquées durant cette attribution (peut être vide)
     */
    public List<Reward> addXP(int amount) {
        if (amount <= 0) {
            return Collections.emptyList();
        }

        this.currentXP += amount;
        this.totalXP += amount;

        List<Reward> unlockedRewards = new ArrayList<>();

        // Vérification des montées de niveau
        while (level < MAX_LEVEL && currentXP >= getXpForNextLevel()) {
            currentXP -= getXpForNextLevel();
            level++;
            Reward reward = REWARDS.get(level - 1);
            this.title = reward.getTitle();
            unlockedRewards.add(reward);
        }

        // Au niveau max, on conserve l'XP sans la perdre
        if (level == MAX_LEVEL) {
            currentXP = totalXP;
        }

        return unlockedRewards;
    }

    /**
     * Retourne l'XP nécessaire pour atteindre le prochain niveau.
     * Retourne {@code Integer.MAX_VALUE} si le niveau maximum est atteint.
     *
     * @return l'XP requise pour le prochain niveau
     */
    public int getXpForNextLevel() {
        if (level >= MAX_LEVEL) {
            return Integer.MAX_VALUE;
        }
        return XP_THRESHOLDS[level];
    }

    /**
     * Calcule le pourcentage de progression vers le prochain niveau (0 à 100).
     *
     * @return le pourcentage de progression (entier entre 0 et 100)
     */
    public int getLevelProgressPercent() {
        if (level >= MAX_LEVEL) {
            return 100;
        }
        int xpNeeded = getXpForNextLevel();
        return (int) ((currentXP / (double) xpNeeded) * 100);
    }

    /**
     * Retourne la liste complète des récompenses disponibles dans le jeu.
     *
     * @return liste immuable des récompenses
     */
    public static List<Reward> getAllRewards() {
        return REWARDS;
    }

    // --- Getters ---

    /**
     * Retourne le nom du joueur.
     *
     * @return le nom
     */
    public String getName() { return name; }

    /**
     * Retourne le niveau actuel du joueur.
     *
     * @return le niveau (entre 1 et {@value #MAX_LEVEL})
     */
    public int getLevel() { return level; }

    /**
     * Retourne l'XP accumulée dans le niveau actuel.
     *
     * @return l'XP courante dans le niveau
     */
    public int getCurrentXP() { return currentXP; }

    /**
     * Retourne l'XP totale accumulée depuis la création du personnage.
     *
     * @return l'XP totale
     */
    public int getTotalXP() { return totalXP; }

    /**
     * Retourne le titre actuel du joueur.
     *
     * @return le titre
     */
    public String getTitle() { return title; }

    /**
     * Modifie le nom du joueur.
     *
     * @param name le nouveau nom
     */
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return String.format("%s [%s] — Niveau %d — %d XP", name, title, level, totalXP);
    }
}
