package uk.org.squirm3.config.gui;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.KeyStroke;

import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

import uk.org.squirm3.springframework.converter.StringToIconConverter;
import uk.org.squirm3.springframework.converter.StringToKeyStrokeConverter;

import com.google.common.collect.Sets;

public class ActionConfigurerIntegrationTest {
    private static final String IDENTIFIER = "myid";

    private final Environment environment = new StandardEnvironment();
    private final MessageSource messageSource = this.createMessageSource();
    private final ConversionService conversionService = this.createConversionService();
    private final ActionConfigurer actionConfigurer = ActionConfigurerFactory.createDefaultConfigurer(this.environment, this.messageSource, this.conversionService);

    @Test
    public void testActionSetting() {
        final Action action = new TestAction();
        this.actionConfigurer.configure(action, IDENTIFIER);
        new JButton(action);

    }

    @Test
    public void shouldBeSupportedByAction() {
        final Action action = mock(Action.class);
        this.actionConfigurer.configure(action, IDENTIFIER);
        this.verifyActionSetting(action);
    }

    private StaticMessageSource createMessageSource() {
        final StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessages(getMessages(), Locale.getDefault());
        return messageSource;
    }

    @SuppressWarnings("unchecked")
    private ConversionService createConversionService() {
        final ConversionServiceFactoryBean conversionServiceFactoryBean = new ConversionServiceFactoryBean();
        conversionServiceFactoryBean.setConverters(Sets.newHashSet(new StringToIconConverter(), new StringToKeyStrokeConverter()));
        conversionServiceFactoryBean.afterPropertiesSet();

        final ConversionService conversionService = conversionServiceFactoryBean.getObject();
        return conversionService;
    }

    private void verifyActionSetting(final Action action) {
        verify(action).putValue(eq(Action.ACCELERATOR_KEY), any(KeyStroke.class));
        verify(action).putValue(Action.ACTION_COMMAND_KEY, "wecan");
        verify(action).putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 42);
        verify(action).putValue(eq(Action.LARGE_ICON_KEY), any(Icon.class));
        verify(action).putValue(Action.LONG_DESCRIPTION, "long description");
        verify(action).putValue(Action.MNEMONIC_KEY, 51);
        verify(action).putValue(Action.NAME, "name");
        verify(action).putValue(Action.SELECTED_KEY, true);
        verify(action).putValue(Action.SHORT_DESCRIPTION, "short description");
        verify(action).putValue(eq(Action.SMALL_ICON), any(Icon.class));
        verifyZeroInteractions(action);
    }

    private static Map<String, String> getMessages() {
        final Map<String, String> map = new HashMap<String, String>();
        map.put("myid.action.accelerator", "alt shift X");
        map.put("myid.action.command", "wecan");
        map.put("myid.action.mnemoindex", "42");
        map.put("myid.action.largeicon", "/graphics/reset.png");
        map.put("myid.action.longtext", "long description");
        map.put("myid.action.mnemonic", "51");
        map.put("myid.action.name", "name");
        map.put("myid.action.selected", "true");
        map.put("myid.action.shorttext", "short description");
        map.put("myid.action.smallicon", "/graphics/about.png");
        return map;
    }

    private final class TestAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(final ActionEvent e) {
            // nothing
        }

    }

}
