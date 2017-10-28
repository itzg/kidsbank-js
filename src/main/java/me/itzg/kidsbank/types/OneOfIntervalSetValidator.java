package me.itzg.kidsbank.types;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
public class OneOfIntervalSetValidator implements ConstraintValidator<OneOfIntervalSetValidator.OneOfIntervalSet, ScheduledTransaction> {

    @Override
    public boolean isValid(ScheduledTransaction scheduledTransaction,
                           ConstraintValidatorContext context) {
        if (scheduledTransaction.getIntervalType() == null) {
            return false;
        }

        switch (scheduledTransaction.getIntervalType()) {
            case WEEKLY:
                return scheduledTransaction.getWeekly() != null;
            case MONTHLY:
                return scheduledTransaction.getMonthly() != null;
        }
        return false;
    }

    @Target({TYPE, ANNOTATION_TYPE}) // class level constraint
    @Retention(RUNTIME)
    @Constraint(validatedBy = OneOfIntervalSetValidator.class) // validator
    @Documented
    public @interface OneOfIntervalSet {
        String message() default "one of the target intervals need to be set"; // default error message

        Class<?>[] groups() default {}; // required

        Class<? extends Payload>[] payload() default {}; // required
    }
}
