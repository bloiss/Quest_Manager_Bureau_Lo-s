package com.taskquest.view;

import com.taskquest.model.Quest;

import javax.swing.*;
import java.awt.*;

/**
 * Boîte de dialogue modale pour créer une nouvelle quête.
 *
 * <p>Collecte les informations saisies par l'utilisateur :
 * titre, description, récompense XP et type de quête.
 * Les validations métier sont effectuées dans le contrôleur.</p>
 */
public class DialogForm extends JDialog {

    /** Champ de saisie du titre de la quête. */
    private JTextField titleField;

    /** Zone de saisie de la description. */
    private JTextArea descriptionArea;

    /** Spinner pour la saisie de la récompense XP. */
    private JSpinner xpSpinner;

    /** Case à cocher pour le type de quête (quotidienne ou unique). */
    private JCheckBox dailyCheckBox;

    /** Indique si l'utilisateur a confirmé la création. */
    private boolean confirmed = false;

    /**
     * Construit la boîte de dialogue de création de quête.
     *
     * @param parent la fenêtre parente (pour le centrage et la modalité)
     */
    public DialogForm(JFrame parent) {
        super(parent, "Nouvelle quête", true);
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Initialise et place les composants du formulaire.
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        // Titre
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("Titre* :"), gbc);
        titleField = new JTextField(25);
        titleField.setDocument(new LimitedDocument(Quest.MAX_TITLE_LENGTH));
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(titleField, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Description :"), gbc);
        descriptionArea = new JTextArea(4, 25);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(descScroll, gbc);

        // Récompense XP
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Récompense XP* :"), gbc);
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(50, 1, Quest.MAX_XP_REWARD, 5);
        xpSpinner = new JSpinner(spinnerModel);
        gbc.gridx = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(xpSpinner, gbc);

        // Type quotidien
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        dailyCheckBox = new JCheckBox("Quête quotidienne (se réinitialise chaque jour)");
        formPanel.add(dailyCheckBox, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnOk = new JButton("Créer");
        JButton btnCancel = new JButton("Annuler");

        btnOk.addActionListener(e -> onConfirm());
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnOk);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);

        // Touche Entrée = confirmer, Échap = annuler
        getRootPane().setDefaultButton(btnOk);
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke("ESCAPE"),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    /**
     * Traite la confirmation du formulaire.
     * Le titre est vérifié ici pour un retour visuel immédiat avant envoi au contrôleur.
     */
    private void onConfirm() {
        if (titleField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this,
                "Le titre ne peut pas être vide.",
                "Champ requis", JOptionPane.WARNING_MESSAGE);
            titleField.requestFocus();
            return;
        }
        confirmed = true;
        dispose();
    }

    /**
     * Indique si l'utilisateur a confirmé la création.
     *
     * @return {@code true} si confirmé, {@code false} si annulé
     */
    public boolean isConfirmed() { return confirmed; }

    /**
     * Retourne le titre saisi.
     *
     * @return le titre de la quête
     */
    public String getQuestTitle() { return titleField.getText().trim(); }

    /**
     * Retourne la description saisie.
     *
     * @return la description de la quête
     */
    public String getQuestDescription() { return descriptionArea.getText().trim(); }

    /**
     * Retourne la récompense XP saisie.
     *
     * @return la valeur XP (entier entre 1 et {@value Quest#MAX_XP_REWARD})
     */
    public int getXpReward() { return (Integer) xpSpinner.getValue(); }

    /**
     * Indique si la quête doit être quotidienne.
     *
     * @return {@code true} si quotidienne, {@code false} si unique
     */
    public boolean isDaily() { return dailyCheckBox.isSelected(); }

    // --- Classe interne : limitation de longueur sur les champs texte ---

    /**
     * Document Swing limitant le nombre de caractères saisissables.
     */
    private static class LimitedDocument extends javax.swing.text.PlainDocument {

        /** Nombre maximum de caractères autorisés. */
        private final int maxLength;

        /**
         * Construit un document avec une limite de caractères.
         *
         * @param maxLength le nombre maximum de caractères
         */
        LimitedDocument(int maxLength) {
            this.maxLength = maxLength;
        }

        @Override
        public void insertString(int offset, String str, javax.swing.text.AttributeSet attr)
                throws javax.swing.text.BadLocationException {
            if (str == null) return;
            if ((getLength() + str.length()) <= maxLength) {
                super.insertString(offset, str, attr);
            }
        }
    }
}
