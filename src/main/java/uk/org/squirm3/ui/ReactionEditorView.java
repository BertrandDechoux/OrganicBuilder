package uk.org.squirm3.ui;

import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import uk.org.squirm3.Application;
import uk.org.squirm3.Resource;
import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Reaction;
import uk.org.squirm3.engine.ApplicationEngine;

public class ReactionEditorView extends AView {
    private JCheckBox bondedBefore, bondedAfter;
    private JComboBox aType, aState, bType, bState, futureAState, futureBState;
    private JLabel futureAType, futureBType;
    private JButton addReaction;
    private final JPanel editorPanel;

    public ReactionEditorView(final ApplicationEngine applicationEngine) {
        super(applicationEngine);
        editorPanel = createEditorPanel();
    }

    public JPanel getEditorPanel() {
        return editorPanel;
    }

    private JPanel createEditorPanel() {
        final JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.PAGE_AXIS));
        jPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                Application.localize("reactions.editor")));
        final JPanel reactionPanel = new JPanel();
        reactionPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        final ActionListener l = new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                addReaction.setText(createReactionFromEditor().toString());
            }
        };
        aType = createTypeComboBox();
        aType.addActionListener(l);
        reactionPanel.add(aType);
        aState = createStateComboBox();
        aState.addActionListener(l);
        reactionPanel.add(aState);
        bondedBefore = new JCheckBox();
        bondedBefore.addActionListener(l);
        reactionPanel.add(bondedBefore);
        bType = createTypeComboBox();
        bType.addActionListener(l);
        reactionPanel.add(bType);
        bState = createStateComboBox();
        bState.addActionListener(l);
        reactionPanel.add(bState);
        reactionPanel.add(new JLabel(" => "));
        futureAType = new JLabel();
        reactionPanel.add(futureAType);
        futureAState = createStateComboBox();
        futureAState.addActionListener(l);
        reactionPanel.add(futureAState);
        bondedAfter = new JCheckBox();
        bondedAfter.addActionListener(l);
        reactionPanel.add(bondedAfter);
        futureBType = new JLabel();
        reactionPanel.add(futureBType);
        futureBState = createStateComboBox();
        futureBState.addActionListener(l);
        reactionPanel.add(futureBState);
        jPanel.add(reactionPanel);
        addReaction = new JButton(Resource.getIcon("add"));
        addReaction.setMargin(new Insets(0, 0, 0, 0));
        addReaction.setToolTipText(Application
                .localize("reactions.add.tooltip"));
        addReaction.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final Collection c = new ArrayList(1);
                c.add(createReactionFromEditor());
                getApplicationEngine().addReactions(c);
            }
        });
        jPanel.add(addReaction);
        l.actionPerformed(null); // init button's text
        return jPanel;
    }

    private Reaction createReactionFromEditor() {
        return new Reaction(aType.getSelectedIndex(),
                aState.getSelectedIndex(), bondedBefore.isSelected(),
                bType.getSelectedIndex(), bState.getSelectedIndex(),
                futureAState.getSelectedIndex(), bondedAfter.isSelected(),
                futureBState.getSelectedIndex());
    }

    private JComboBox createTypeComboBox() {
        final JComboBox jComboBox = new JComboBox();
        for (int i = 0; i < 8; i++) {
            jComboBox.addItem(String.valueOf(Atom.type_code.charAt(i)));
        }
        return jComboBox;
    }

    private JComboBox createStateComboBox() {
        final JComboBox jComboBox = new JComboBox();
        for (int i = 0; i < 50; i++) { // TODO no hardcoded value!
            jComboBox.addItem(String.valueOf(i));
        }
        return jComboBox;
    }
}
