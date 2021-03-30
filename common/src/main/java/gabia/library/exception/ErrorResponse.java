package gabia.library.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Wade
 * This class is common class returned when exception occurs.
 */

@Getter
@NoArgsConstructor
public class ErrorResponse {

    private String message;
    private List<FieldError> errors;

    private ErrorResponse(String errorMessage, final List<FieldError> errors) {
        this.message = errorMessage;
        this.errors = errors;
    }

    private ErrorResponse(final String errorMessage) {
        this.message = errorMessage;
        this.errors = new ArrayList<>();
    }

    public static ErrorResponse of(final String errorMessage, final BindingResult bindingResult) {
        return new ErrorResponse(errorMessage, FieldError.of(bindingResult));
    }

    public static ErrorResponse of(final String errorMessage) {
        return new ErrorResponse(errorMessage);
    }

    @Getter
    @NoArgsConstructor
    public static class FieldError {
        private String field;
        private String value;
        private String reason;

        private FieldError(final String field, final String value, final String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        private static List<FieldError> of(final BindingResult bindingResult) {
            return bindingResult.getFieldErrors().stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
    }
}
