package uk.org.squirm3.swing.action;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 * Declares all {@link Action} pseudo properties as {@link ActionProperty}.
 */
enum ActionProperties implements ActionProperty {

    ACCELERATOR(Action.ACCELERATOR_KEY, KeyStroke.class), //
    COMMAND(Action.ACTION_COMMAND_KEY, String.class), //
    MNEMOINDEX(Action.DISPLAYED_MNEMONIC_INDEX_KEY, Integer.class), //
    LARGEICON(Action.LARGE_ICON_KEY, Icon.class), //
    LONGTEXT(Action.LONG_DESCRIPTION, String.class), //
    MNEMONIC(Action.MNEMONIC_KEY, Integer.class), //
    NAME(Action.NAME, String.class), //
    SELECTED(Action.SELECTED_KEY, Boolean.class), //
    SHORTTEXT(Action.SHORT_DESCRIPTION, String.class), //
    SMALLICON(Action.SMALL_ICON, Icon.class);

    private final String swingKey;
    private final Class<?> targetType;

    private ActionProperties(final String swingKey, final Class<?> targetType) {
        this.swingKey = swingKey;
        this.targetType = targetType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.org.squirm3.swing.action.ActionProperty#getSwingKey()
     */
    @Override
    public String getSwingKey() {
        return swingKey;
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.org.squirm3.swing.action.ActionProperty#getTargetType()
     */
    @Override
    public Class<?> getTargetType() {
        return targetType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * uk.org.squirm3.swing.action.ActionProperty#getMessageCode(java.lang.String
     * )
     */
    @Override
    public String getMessageCode(final String actionIdentifier) {
        return actionIdentifier + ".action." + name().toLowerCase();
    }

}
