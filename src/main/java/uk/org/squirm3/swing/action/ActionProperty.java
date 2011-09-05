package uk.org.squirm3.swing.action;

/**
 * Property that can be used for {@link javax.swing.Action}.
 */
interface ActionProperty {

    /**
     * @return Identifier of the property used for
     *         {@link javax.swing.Action#putValue(String, Object)}.
     */
    public String getSwingKey();

    /**
     * @return The type of the value of this property.
     */
    public Class<?> getTargetType();

    /**
     * @return the message code used to get a {@link String} representation of
     *         the property.
     */
    public String getMessageCode(final String actionIdentifier);

}
