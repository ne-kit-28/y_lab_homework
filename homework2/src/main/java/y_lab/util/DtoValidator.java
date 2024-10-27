package y_lab.util;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.util.Set;

public class DtoValidator {

    private static final ValidatorFactory factory;
    private static final Validator validator;

    static {
        try {
            // Создаем фабрику валидаторов с использованием ParameterMessageInterpolator
            factory = Validation.byProvider(HibernateValidator.class)
                    .configure()
                    .messageInterpolator(new ParameterMessageInterpolator())
                    .buildValidatorFactory();
            validator = factory.getValidator();
        } catch (Exception e) {
            throw new IllegalStateException("Validator initialization failed", e);
        }
    }

    public static <T> void validate(T dto) throws IllegalArgumentException {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException(sb.toString());
        }
    }
}

