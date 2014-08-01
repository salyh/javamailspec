package javax.mail;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Declares one or more MailSessionDefinition annotations.
 *
 * @see MailSessionDefinition
 * @since JavaMail 1.5
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MailSessionDefinitions {
    MailSessionDefinition[] value();
}