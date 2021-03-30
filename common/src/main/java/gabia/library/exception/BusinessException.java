package gabia.library.exception;

/**
 * @author Wade
 * This class is that manages a business exception.
 */

public class BusinessException extends RuntimeException {

    private final String errorMessage;


    public BusinessException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
