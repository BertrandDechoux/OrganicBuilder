package uk.org.squirm3.ui;

import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.Reaction;
import uk.org.squirm3.springframework.Messages;

public class ReactionEditorPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final MessageSource messageSource;
    private final ImageIcon addIcon;

    private JCheckBox bondedBefore, bondedAfter;
    private JComboBox aType, aState, bType, bState, futureAState, futureBState;
    private JLabel futureAType, futureBType;
    private JButton addReaction;

    public ReactionEditorPanel(final ApplicationEngine applicationEngine,
            final MessageSource messageSource, final ImageIcon addIcon) {
        this.messageSource = messageSource;
        this.addIcon = addIcon;
        createEditorPanel(applicationEngine);
        setMaximumSize(getMinimumSize());
    }

    private void createEditorPanel(final ApplicationEngine applicationEngine) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                Messages.localize("reactions.editor", messageSource)));
        final JPanel reactionPanel = new JPanel();
        reactionPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        final ActionListener l = new ActionListener() {
            @Override
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
        add(reactionPanel);
        addReaction = new JButton(addIcon);
        addReaction.setMargin(new Insets(0, 0, 0, 0));
        addReaction.setToolTipText(Messages.localize("reactions.add.tooltip",
                messageSource));
        addReaction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Collection<Reaction> c = new ArrayList<Reaction>(1);
                c.add(createReactionFromEditor());
                applicationEngine.addReactions(c);
            }
        });
        add(addReaction);
        l.actionPerformed(null); // init button's text
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
