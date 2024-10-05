package uk.gov.dwp.uc.pairtest.constant;

public class TicketConstants {

    // Private Constructor
    private TicketConstants() {
    }

    public static final int SINGLE_BUY_MAX_TICKETS = 25;
    public static final int ADULT_TICKET_PRICE = 25;
    public static final int CHILD_TICKET_PRICE = 15;
    //if we want to charge the infant ticket, change the price here.
    public static final int INFANT_TICKET_PRICE = 0;

    //errors
    public static final String INVALID_ACCOUNT_ID_ERROR = "Invalid account id.";
    public static final String NO_ADULT_ERROR = "No responsible adult in the group.";
    public static final String A_LOT_OF_INFANTS_ERROR = "There are more infants than adults. Only one infant can sit on the lap of an adult.";
    public static final String INVALID_PURCHASE_ERROR = "Invalid purchase.";
    public static final String MORE_THAN_25_TICKETS_ERROR = "Limit exceeded, maximum allowed limit is " + SINGLE_BUY_MAX_TICKETS + ".";
    public static final String INVALID_TYPE_ERROR = "Invalid ticket type, only adult, child and infant type is allowed.";
}
