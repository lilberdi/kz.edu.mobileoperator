package kz.edu.mobileoperator.exception;

/**
 * Общее бизнес-исключение для проверки бизнес-правил на сервисном слое.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}


