package uk.gov.dwp.uc.pairtest.util;

import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.logging.Logger;

/**
 * This is the Utilty class to validate the business rules
 */
public class TicketValidationUtil {

    private static final Logger LOGGER = Logger.getLogger(TicketServiceImpl.class.getName());

    /**
     * @param condition Business condtion
     * @param accountId Account id of the purchaser
     * @param message   Error message for the business condition
     */
    public static void validate(boolean condition, Long accountId, String message) {
        if (condition) {
            LOGGER.severe(message + " Account id: " + accountId);
            throw new InvalidPurchaseException(message);
        }
    }

    private TicketValidationUtil() {
    }
}

