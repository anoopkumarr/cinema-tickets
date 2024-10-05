package uk.gov.dwp.uc.pairtest.exception;

/**
 * This is the custom business exception
 */
public class InvalidPurchaseException extends RuntimeException {
    public InvalidPurchaseException(String message) {
        super(message);
    }
}
