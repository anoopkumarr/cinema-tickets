package uk.gov.dwp.uc.pairtest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.mockito.Mockito.*;
import static uk.gov.dwp.uc.pairtest.constant.TicketConstants.*;

public class TicketServiceImplTest {
    private TicketPaymentService paymentService;
    private SeatReservationService reservationService;

    private TicketService ticketService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() {
        paymentService = mock(TicketPaymentService.class);
        reservationService = mock(SeatReservationService.class);
        ticketService = new TicketServiceImpl(paymentService, reservationService);
    }

    @Test
    public void givenInvalidAccountId_whenPurchasingTicket_thenPurchaseFailed() {
        TicketTypeRequest request1 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);
        TicketTypeRequest request2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);
        TicketTypeRequest request3 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        thrown.expect(InvalidPurchaseException.class);
        thrown.expectMessage(INVALID_ACCOUNT_ID_ERROR);

        ticketService.purchaseTickets(0L, request1, request2, request3);
    }

    @Test
    public void givenNegativeTicketCount_whenPurchasingTicket_thenPurchaseFailed() {
        TicketTypeRequest request1 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, -1);
        TicketTypeRequest request2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);
        TicketTypeRequest request3 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        thrown.expect(InvalidPurchaseException.class);
        thrown.expectMessage(INVALID_PURCHASE_ERROR);

        ticketService.purchaseTickets(2L, request1, request2, request3);
    }

    @Test
    public void givenZeroAdult_whenPurchasingTicket_thenPurchaseFailed() {
        TicketTypeRequest request1 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        TicketTypeRequest request2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);
        TicketTypeRequest request3 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);

        thrown.expect(InvalidPurchaseException.class);
        thrown.expectMessage(NO_ADULT_ERROR);

        ticketService.purchaseTickets(2L, request1, request2, request3);
    }

    @Test
    public void givenMoreInfantsThanAdults_whenPurchasingTicket_thenPurchaseFailed() {
        TicketTypeRequest request1 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 10);
        TicketTypeRequest request2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);
        TicketTypeRequest request3 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);

        thrown.expect(InvalidPurchaseException.class);
        thrown.expectMessage(A_LOT_OF_INFANTS_ERROR);

        ticketService.purchaseTickets(2L, request1, request2, request3);
    }

    @Test
    public void givenMoreThan25Tickets_whenPurchasingTicket_thenPurchaseFailed() {
        TicketTypeRequest request1 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 10);
        TicketTypeRequest request2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 10);
        TicketTypeRequest request3 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 10);

        thrown.expect(InvalidPurchaseException.class);
        thrown.expectMessage(MORE_THAN_25_TICKETS_ERROR);

        ticketService.purchaseTickets(2L, request1, request2, request3);
    }

    @Test
    public void givenSingleInfant_whenPurchasingTicket_thenPurchaseFailed() {
        TicketTypeRequest request1 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        thrown.expect(InvalidPurchaseException.class);
        thrown.expectMessage(NO_ADULT_ERROR);

        ticketService.purchaseTickets(2L, request1);
    }

    @Test
    public void givenSingleChild_whenPurchasingTicket_thenPurchaseFailed() {
        TicketTypeRequest request1 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        thrown.expect(InvalidPurchaseException.class);
        thrown.expectMessage(NO_ADULT_ERROR);

        ticketService.purchaseTickets(2L, request1);
    }

    @Test
    public void givenSingleAdult_whenPurchasingTicket_thenPurchaseSuccess() {
        TicketTypeRequest request1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        ticketService.purchaseTickets(2L, request1);

        verify(paymentService, times(1)).makePayment(anyLong(), anyInt());
        verify(reservationService, times(1)).reserveSeat(anyLong(), anyInt());
    }

    @Test
    public void givenSingleAdultAndThreeChildren_whenPurchasingTicket_thenPurchaseSuccess() {
        TicketTypeRequest request1 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);
        TicketTypeRequest request2 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        ticketService.purchaseTickets(2L, request1, request2);

        verify(paymentService, times(1)).makePayment(anyLong(), anyInt());
        verify(reservationService, times(1)).reserveSeat(anyLong(), anyInt());
    }

    @Test
    public void givenSingleAdultAndSingleInfant_whenPurchasingTicket_thenPurchaseSuccess() {
        TicketTypeRequest request1 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        TicketTypeRequest request2 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        ticketService.purchaseTickets(2L, request1, request2);

        verify(paymentService, times(1)).makePayment(anyLong(), anyInt());
        verify(reservationService, times(1)).reserveSeat(anyLong(), anyInt());
    }

    @Test
    public void givenSingleAdultAndSingleInfantAndSingleChild_whenPurchasingTicket_thenPurchaseSuccess() {
        TicketTypeRequest request1 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        TicketTypeRequest request2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest request3 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        ticketService.purchaseTickets(2L, request1, request2, request3);

        verify(paymentService, times(1)).makePayment(anyLong(), anyInt());
        verify(reservationService, times(1)).reserveSeat(anyLong(), anyInt());
    }

    @Test
    public void givenAllowedTypesMultipleTimes_whenPurchasingTicket_thenPurchaseSuccess() {
        TicketTypeRequest request1 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        TicketTypeRequest request2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest request3 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest request4 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        TicketTypeRequest request5 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);
        TicketTypeRequest request6 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        ticketService.purchaseTickets(2L, request1, request2, request3, request4, request5, request6);

        verify(paymentService, times(1)).makePayment(anyLong(), anyInt());
        verify(reservationService, times(1)).reserveSeat(anyLong(), anyInt());
    }
}
