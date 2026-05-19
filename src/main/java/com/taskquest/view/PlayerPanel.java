package com.taskquest.view;

import com.taskquest.model.Player;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Panneau Swing affichant le profil et la progression du joueur.
 *
 * <p>Affiche : nom du joueur, titre actuel, niveau, XP courante
 * et une barre de progression vers le prochain niveau.</p>
 */
public class PlayerPanel extends JPanel {

    /** Étiquette affichant le nom et le titre du joueur. */
    private JLabel nameLabel;

    /** Étiquette affichant le niveau actuel. */
    private JLabel levelLabel;

    /** Étiquette affichant les points XP courants. */
    private JLabel xpLabel;

    /** Barre de progression vers le prochain niveau. */
    private JProgressBar xpProgressBar;

    /**
     * Construit le panneau joueur avec les données initiales.
     *
     * @param player le joueur à afficher (peut être null si non encore créé)
     */
    public PlayerPanel(Player player) {
        initComponents();
        if (player != null) {
            refresh(player);
        }
    }

    /**
     * Initialise et place les composants graphiques du panneau.
     */
    private void initComponents() {
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Profil Aventurier",
            TitledBorder.LEFT, TitledBorder.TOP));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;

        nameLabel = new JLabel("Nom : —");
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 14f));

        levelLabel = new JLabel("Niveau : —");
        xpLabel = new JLabel("XP : —");

        xpProgressBar = new JProgressBar(0, 100);
        xpProgressBar.setStringPainted(true);
        xpProgressBar.setString("Progression");
        xpProgressBar.setPreferredSize(new Dimension(200, 20));

        gbc.gridx = 0; gbc.gridy = 0;
        add(nameLabel, gbc);

        gbc.gridx = 1;
        add(levelLabel, gbc);

        gbc.gridx = 2;
        add(xpLabel, gbc);

        gbc.gridx = 3;
        add(xpProgressBar, gbc);
    }

    /**
     * Met à jour l'affichage avec les données actuelles du joueur.
     *
     * @param player le joueur dont les données sont à afficher
     */
    public void refresh(Player player) {
        if (player == null) return;

        nameLabel.setText(player.getName() + " [" + player.getTitle() + "]");
        levelLabel.setText("Niveau " + player.getLevel());

        if (player.getLevel() < Player.MAX_LEVEL) {
            xpLabel.setText(player.getCurrentXP() + " / " + player.getXpForNextLevel() + " XP");
        } else {
            xpLabel.setText("XP totale : " + player.getTotalXP());
        }

        int progress = player.getLevelProgressPercent();
        xpProgressBar.setValue(progress);
        xpProgressBar.setString(progress + "%");

        revalidate();
        repaint();
    }
}
