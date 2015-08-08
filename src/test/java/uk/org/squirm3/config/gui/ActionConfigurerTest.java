package uk.org.squirm3.config.gui;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Locale;

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
import org.springframework.core.env.Environment;

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
    private Environment environment;
    @Mock
    private MessageSource messageSource;
    @Mock
    private ConversionService conversionService;
    @Mock
    private ActionProperty actionProperty;
    @Mock
    private Action action;

    private ActionConfigurer actionConfigurer;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Before
    public void setup() {
        this.actionConfigurer = ActionConfigurerFactory.createCustomConfigurer(this.environment, this.messageSource, this.conversionService, Arrays.asList(this.actionProperty));

        when(this.actionProperty.getMessageCode(IDENTIFIER)).thenReturn(MESSAGE_CODE);
        when(this.actionProperty.getTargetType()).thenReturn((Class) TARGET_TYPE);
        when(this.actionProperty.getSwingKey()).thenReturn(SWING_KEY);
    }

    @Test
    public void shouldIgnoreMissingMessage() {
        this.whenMessageIs(null, null);
        this.whenConversionFail();
        this.actionConfigurer.configure(this.action, IDENTIFIER);
        verify(this.action, never()).putValue(any(String.class), any());
    }

    @Test
    public void shouldSkipMessageIfPropertyIsNotNull() {
        this.whenMessageIs(null, null);
        this.whenConversionFail();
        this.actionConfigurer.configure(this.action, IDENTIFIER);

        verify(this.action, never()).putValue(any(String.class), any());
    }

    @Test
    public void shouldIgnoreEmptyMessage() {
        this.whenMessageIs(null, "    ");
        this.whenConversionFail();
        this.actionConfigurer.configure(this.action, IDENTIFIER);
        verify(this.action, never()).putValue(any(String.class), any());
    }

    @Test
    public void shoulFailOnConversionException() {
        this.whenMessageIs(null, MESSAGE);
        this.whenConversionFail();
        try {
            this.actionConfigurer.configure(this.action, IDENTIFIER);
        } finally {
            this.expectConversionFailure();
        }
    }

    @Test
    public void shoulFailOnConversionWithoutResult() {
        this.whenMessageIs(null, MESSAGE);
        when(this.conversionService.convert(MESSAGE, TARGET_TYPE)).thenReturn(null);
        try {
            this.actionConfigurer.configure(this.action, IDENTIFIER);
        } finally {
            verify(this.action, never()).putValue(any(String.class), any());
            this.expectNullConversionResult();
        }
    }

    @Test
    public void shouldSetPropertyAfterSuccessfulPropertyConversion() {
        this.whenMessageIs(MESSAGE, "property overide message");
        when(this.conversionService.convert(MESSAGE, TARGET_TYPE)).thenReturn(VALUE);
        this.actionConfigurer.configure(this.action, IDENTIFIER);
        verify(this.action).putValue(SWING_KEY, VALUE);
    }

    @Test
    public void shouldSetPropertyAfterSuccessfulMessageConversion() {
        this.whenMessageIs(null, MESSAGE);
        when(this.conversionService.convert(MESSAGE, TARGET_TYPE)).thenReturn(VALUE);
        this.actionConfigurer.configure(this.action, IDENTIFIER);
        verify(this.action).putValue(SWING_KEY, VALUE);
    }

    private void expectNullConversionResult() {
        this.thrown.expect(RuntimeException.class);
        this.thrown.expectMessage("Error during the conversion of " + MESSAGE);
    }

    private void whenMessageIs(final String property, final String message) {
        when(this.environment.getProperty(MESSAGE_CODE)).thenReturn(property);
        when(this.messageSource.getMessage(MESSAGE_CODE, null, null, Locale.getDefault())).thenReturn(message);
    }

    private void expectConversionFailure() {
        this.thrown.expect(RuntimeException.class);
        this.thrown.expectMessage(CONVERSION_MESSAGE);
    }

    private void whenConversionFail() {
        when(this.conversionService.convert(any(), any())).thenThrow(new RuntimeException(CONVERSION_MESSAGE));
    }

}
