package kz.edu.mobileoperator.exception;

/**
 * Бросается, когда требуемый объект не найден в базе данных.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}


