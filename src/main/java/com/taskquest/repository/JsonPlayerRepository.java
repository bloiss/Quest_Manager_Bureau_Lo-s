package com.taskquest.repository;

import com.google.gson.*;
import com.taskquest.exception.DataCorruptedException;
import com.taskquest.exception.PlayerNotFoundException;
import com.taskquest.model.Player;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * Implémentation de {@link PlayerRepository} utilisant un fichier JSON pour la persistance.
 *
 * <p>Le profil du joueur est stocké dans un objet JSON unique. Si le fichier est absent,
 * une {@link PlayerNotFoundException} est levée (premier lancement de l'application).</p>
 */
public class JsonPlayerRepository implements PlayerRepository {

    /** Chemin vers le fichier de données du joueur. */
    private final Path filePath;

    /** Instance Gson configurée. */
    private final Gson gson;

    /**
     * Construit le repository avec le chemin de fichier spécifié.
     *
     * @param filePath le chemin vers le fichier JSON du joueur
     */
    public JsonPlayerRepository(Path filePath) {
        this.filePath = filePath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * {@inheritDoc}
     *
     * @throws PlayerNotFoundException si le fichier n'existe pas (premier lancement)
     * @throws DataCorruptedException  si le JSON est invalide ou les champs manquants
     */
    @Override
    public Player load() throws PlayerNotFoundException, DataCorruptedException {
        if (!Files.exists(filePath)) {
            throw new PlayerNotFoundException(
                "Aucun profil joueur trouvé. Première utilisation de l'application.");
        }

        try {
            String json = Files.readString(filePath, StandardCharsets.UTF_8);
            if (json.isBlank()) {
                throw new PlayerNotFoundException("Le fichier joueur est vide.");
            }

            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            String name = obj.get("name").getAsString();
            int level = obj.get("level").getAsInt();
            int currentXP = obj.get("currentXP").getAsInt();
            int totalXP = obj.get("totalXP").getAsInt();
            String title = obj.get("title").getAsString();

            return new Player(name, level, currentXP, totalXP, title);

        } catch (JsonParseException | NullPointerException | IllegalStateException e) {
            throw new DataCorruptedException(
                "Le fichier joueur est corrompu ou malformé : " + filePath, e);
        } catch (IOException e) {
            throw new DataCorruptedException(
                "Impossible de lire le fichier joueur : " + filePath, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Player player) throws DataCorruptedException {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            JsonObject obj = new JsonObject();
            obj.addProperty("name", player.getName());
            obj.addProperty("level", player.getLevel());
            obj.addProperty("currentXP", player.getCurrentXP());
            obj.addProperty("totalXP", player.getTotalXP());
            obj.addProperty("title", player.getTitle());

            Files.writeString(filePath, gson.toJson(obj), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new DataCorruptedException(
                "Impossible d'écrire le fichier joueur : " + filePath, e);
        }
    }
}
