package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.logging.Logger;

import static uk.gov.dwp.uc.pairtest.constant.TicketConstants.*;
import static uk.gov.dwp.uc.pairtest.util.TicketValidationUtil.validate;

/**
 * This Service class help to purchase tickets
 */
public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    private static final Logger LOGGER = Logger.getLogger(TicketServiceImpl.class.getName());
    private final TicketPaymentService paymentService;
    private final SeatReservationService reservationService;

    /**
     * Constructor
     *
     * @param paymentService     Service to make payment
     * @param reservationService Service to reserve seats
     */
    public TicketServiceImpl(TicketPaymentService paymentService,
                             SeatReservationService reservationService) {
        this.paymentService = paymentService;
        this.reservationService = reservationService;
    }

    /**
     * This method purchases tickets
     *
     * @param accountId          Account id of the Purchaser
     * @param ticketTypeRequests The types of tickets to be purchased
     * @throws InvalidPurchaseException If any acceptable business criteria fails
     */
    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        validate(accountId == 0, accountId, INVALID_ACCOUNT_ID_ERROR);

        int adultTickets = 0;
        int childTickets = 0;
        int infantTickets = 0;
        for (TicketTypeRequest request : ticketTypeRequests) {
            validate(request.getNoOfTickets() < 0, accountId, INVALID_PURCHASE_ERROR);
            if (request.getTicketType().equals(TicketTypeRequest.Type.INFANT)) {
                infantTickets += request.getNoOfTickets();
            } else if (request.getTicketType().equals(TicketTypeRequest.Type.CHILD)) {
                childTickets += request.getNoOfTickets();
            } else if (request.getTicketType().equals(TicketTypeRequest.Type.ADULT)) {
                adultTickets += request.getNoOfTickets();
            } else {
                validate(true, accountId, INVALID_TYPE_ERROR);
            }
        }

        validate(adultTickets < 1, accountId, NO_ADULT_ERROR);
        validate(adultTickets < infantTickets, accountId, A_LOT_OF_INFANTS_ERROR);

        int totalTickets = adultTickets + childTickets + infantTickets;
        validate(totalTickets > SINGLE_BUY_MAX_TICKETS, accountId, MORE_THAN_25_TICKETS_ERROR);

        reserveSeats(accountId, adultTickets, childTickets);
        confirmPayment(accountId, adultTickets, childTickets, infantTickets);
    }


    /**
     * @param accountId     Account id of the Purchaser
     * @param adultTickets  No of Adult tickets
     * @param childTickets  No of Child tickets
     * @param infantTickets No of Infant tickets
     */
    private void confirmPayment(Long accountId, int adultTickets, int childTickets, int infantTickets) {
        // Included infant tickets to the calculation, if infants get charged in the future.
        int totalPrice = adultTickets * ADULT_TICKET_PRICE
                + childTickets * CHILD_TICKET_PRICE
                + infantTickets * INFANT_TICKET_PRICE;
        // Call Payment Service
        paymentService.makePayment(accountId, totalPrice);
        LOGGER.info("Successfully paid from account " + accountId);
    }

    /**
     * @param accountId    Account id of the Purchaser
     * @param adultTickets No of Adult tickets
     * @param childTickets No of Child tickets
     */
    private void reserveSeats(Long accountId, int adultTickets, int childTickets) {
        int totalSeats = adultTickets + childTickets;
        // Call Reservation service
        reservationService.reserveSeat(accountId, totalSeats);
        LOGGER.info("Successfully reserved seats for account " + accountId);
    }

}
