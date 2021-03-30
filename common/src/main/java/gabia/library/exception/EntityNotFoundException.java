package gabia.library.exception;

/**
 * @author Wade
 * This class is that manages a entity not found exception.
 */

public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
