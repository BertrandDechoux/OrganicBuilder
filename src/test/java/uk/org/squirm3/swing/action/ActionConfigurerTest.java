package uk.org.squirm3.swing.action;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;

import javax.swing.Action;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionService;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActionConfigurerTest {
    private static final String CONVERSION_MESSAGE = "Exception during the conversion.";

    private static final String IDENTIFIER = "myIdentifier";
    private static final String MESSAGE_CODE = "myMessageCode";
    private static final String MESSAGE = "myMessage";
    private static final String SWING_KEY = "myKey";
    private static final Class<String> TARGET_TYPE = String.class;
    private static final String VALUE = "myObject";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private Properties properties;
    @Mock
    private MessageSource messageSource;
    @Mock
    private ConversionService conversionService;
    @Mock
    private ActionProperty actionProperty;
    @Mock
    private Action action;

    private ActionConfigurer actionConfigurer;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Before
    public void setup() {
        actionConfigurer = ActionConfigurerFactory.createCustomConfigurer(
                properties, messageSource, conversionService,
                Arrays.asList(actionProperty));

        when(actionProperty.getMessageCode(IDENTIFIER))
                .thenReturn(MESSAGE_CODE);
        when(actionProperty.getTargetType()).thenReturn((Class) TARGET_TYPE);
        when(actionProperty.getSwingKey()).thenReturn(SWING_KEY);
    }

    @Test
    public void shouldIgnoreMissingMessage() {
        whenMessageIs(null, null);
        whenConversionFail();
        actionConfigurer.configure(action, IDENTIFIER);
        verify(action, never()).putValue(any(String.class), any());
    }

    @Test
    public void shouldSkipMessageIfPropertyIsNotNull() {
        whenMessageIs(null, null);
        whenConversionFail();
        actionConfigurer.configure(action, IDENTIFIER);

        verify(action, never()).putValue(any(String.class), any());
    }

    @Test
    public void shouldIgnoreEmptyMessage() {
        whenMessageIs(null, "    ");
        whenConversionFail();
        actionConfigurer.configure(action, IDENTIFIER);
        verify(action, never()).putValue(any(String.class), any());
    }

    @Test
    public void shoulFailOnConversionException() {
        whenMessageIs(null, MESSAGE);
        whenConversionFail();
        try {
            actionConfigurer.configure(action, IDENTIFIER);
        } finally {
            expectConversionFailure();
        }
    }

    @Test
    public void shoulFailOnConversionWithoutResult() {
        whenMessageIs(null, MESSAGE);
        when(conversionService.convert(MESSAGE, TARGET_TYPE)).thenReturn(null);
        try {
            actionConfigurer.configure(action, IDENTIFIER);
        } finally {
            verify(action, never()).putValue(any(String.class), any());
            expectNullConversionResult();
        }
    }

    @Test
    public void shouldSetPropertyAfterSuccessfulPropertyConversion() {
        whenMessageIs(MESSAGE, "property overide message");
        when(conversionService.convert(MESSAGE, TARGET_TYPE)).thenReturn(VALUE);
        actionConfigurer.configure(action, IDENTIFIER);
        verify(action).putValue(SWING_KEY, VALUE);
    }

    @Test
    public void shouldSetPropertyAfterSuccessfulMessageConversion() {
        whenMessageIs(null, MESSAGE);
        when(conversionService.convert(MESSAGE, TARGET_TYPE)).thenReturn(VALUE);
        actionConfigurer.configure(action, IDENTIFIER);
        verify(action).putValue(SWING_KEY, VALUE);
    }

    private void expectNullConversionResult() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Error during the conversion of " + MESSAGE);
    }

    private void whenMessageIs(final String property, final String message) {
        when(properties.getProperty(MESSAGE_CODE)).thenReturn(property);
        when(
                messageSource.getMessage(MESSAGE_CODE, null, null,
                        Locale.getDefault())).thenReturn(message);
    }

    private void expectConversionFailure() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage(CONVERSION_MESSAGE);
    }

    @SuppressWarnings("unchecked")
    private void whenConversionFail() {
        when(conversionService.convert(any(), any(Class.class))).thenThrow(
                new RuntimeException(CONVERSION_MESSAGE));
    }

}
