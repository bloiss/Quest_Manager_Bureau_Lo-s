package com.taskquest.repository;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.taskquest.exception.DataCorruptedException;
import com.taskquest.model.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation de {@link QuestRepository} utilisant un fichier JSON pour la persistance.
 *
 * <p>Le fichier JSON stocke un tableau d'objets quête, chacun incluant un champ
 * {@code type} ("OneTimeQuest" ou "DailyQuest") permettant la désérialisation
 * polymorphique.</p>
 *
 * <p>En cas de fichier absent, la méthode {@link #loadAll()} retourne une liste vide.
 * En cas de fichier corrompu, une {@link DataCorruptedException} est levée.</p>
 */
public class JsonQuestRepository implements QuestRepository {

    /** Chemin vers le fichier de données des quêtes. */
    private final Path filePath;

    /** Instance Gson configurée pour gérer LocalDate et le polymorphisme. */
    private final Gson gson;

    /**
     * Construit le repository avec le chemin de fichier spécifié.
     *
     * @param filePath le chemin vers le fichier JSON de persistance
     */
    public JsonQuestRepository(Path filePath) {
        this.filePath = filePath;
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setPrettyPrinting()
            .create();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Si le fichier n'existe pas, retourne une liste vide sans erreur.
     * Si le JSON est malformé, lève une {@link DataCorruptedException}.</p>
     */
    @Override
    public List<Quest> loadAll() throws DataCorruptedException {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try {
            String json = Files.readString(filePath, StandardCharsets.UTF_8);
            if (json.isBlank()) {
                return new ArrayList<>();
            }

            JsonArray array = JsonParser.parseString(json).getAsJsonArray();
            List<Quest> quests = new ArrayList<>();

            for (JsonElement element : array) {
                JsonObject obj = element.getAsJsonObject();
                String type = obj.get("type").getAsString();
                Quest quest = deserializeQuest(obj, type);
                quests.add(quest);
            }

            return quests;

        } catch (JsonParseException | IllegalStateException e) {
            throw new DataCorruptedException(
                "Le fichier de quêtes est corrompu ou malformé : " + filePath, e);
        } catch (IOException e) {
            throw new DataCorruptedException(
                "Impossible de lire le fichier de quêtes : " + filePath, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Chaque quête est sérialisée avec un champ {@code type} pour permettre
     * la désérialisation polymorphique.</p>
     */
    @Override
    public void saveAll(List<Quest> quests) throws DataCorruptedException {
        try {
            // Création du dossier parent si nécessaire
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            JsonArray array = new JsonArray();
            for (Quest quest : quests) {
                JsonObject obj = gson.toJsonTree(quest).getAsJsonObject();
                obj.addProperty("type", quest.getClass().getSimpleName());
                array.add(obj);
            }

            Files.writeString(filePath, gson.toJson(array), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new DataCorruptedException(
                "Impossible d'écrire le fichier de quêtes : " + filePath, e);
        }
    }

    /**
     * Désérialise un objet JSON en instance de {@link Quest} selon son type.
     *
     * @param obj  l'objet JSON à désérialiser
     * @param type le type de quête ("OneTimeQuest" ou "DailyQuest")
     * @return l'instance de quête correspondante
     * @throws DataCorruptedException si le type est inconnu ou les champs manquants
     */
    private Quest deserializeQuest(JsonObject obj, String type) throws DataCorruptedException {
        try {
            String id = obj.get("id").getAsString();
            String title = obj.get("title").getAsString();
            String description = obj.get("description").getAsString();
            int xpReward = obj.get("xpReward").getAsInt();
            QuestStatus status = QuestStatus.valueOf(obj.get("status").getAsString());

            return switch (type) {
                case "OneTimeQuest" -> new OneTimeQuest(id, title, description, xpReward, status);
                case "DailyQuest" -> {
                    LocalDate lastCompleted = null;
                    if (obj.has("lastCompletedDate") && !obj.get("lastCompletedDate").isJsonNull()) {
                        lastCompleted = LocalDate.parse(obj.get("lastCompletedDate").getAsString());
                    }
                    yield new DailyQuest(id, title, description, xpReward, status, lastCompleted);
                }
                default -> throw new DataCorruptedException("Type de quête inconnu : " + type);
            };

        } catch (NullPointerException | IllegalArgumentException e) {
            throw new DataCorruptedException("Champ manquant ou invalide dans le fichier JSON", e);
        }
    }

    // --- Adaptateur interne pour LocalDate ---

    /**
     * Adaptateur Gson pour sérialiser/désérialiser {@link LocalDate} en ISO-8601 (yyyy-MM-dd).
     */
    private static class LocalDateAdapter
            implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext ctx) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx)
                throws JsonParseException {
            return LocalDate.parse(json.getAsString());
        }
    }
}
