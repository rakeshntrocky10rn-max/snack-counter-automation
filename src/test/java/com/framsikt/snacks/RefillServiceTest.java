
package com.framsikt.snacks;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class RefillServiceTest {

    @Test
    void twoVolunteersCannotClaimSameRefill() {

        RefillService service = new RefillService();

        Refill refill = service.createRefill(Office.BENGALURU);

        assertTrue(service.claimRefill(refill.getId(), "Rakesh"));

        assertFalse(service.claimRefill(refill.getId(), "John"));

        assertEquals("Rakesh", refill.getVolunteer());
    }

    @Test
    void shouldRejectPurchaseExceedingBudget() {

        RefillService service = new RefillService();

        Refill refill = service.createRefill(Office.BENGALURU);

        service.claimRefill(refill.getId(), "Rakesh");

        assertThrows(IllegalStateException.class, () ->
            service.completeRefill(
                refill.getId(),
                new BigDecimal("16000"),
                "receipt.jpg"
            )
        );
    }

    @Test
    void shouldCompleteReimbursementLifecycle() {

        RefillService service = new RefillService();
        ReimbursementService hr = new ReimbursementService();

        Refill refill = service.createRefill(Office.BENGALURU);

        assertTrue(service.claimRefill(refill.getId(), "Rakesh"));

        service.completeRefill(
            refill.getId(),
            new BigDecimal("2500"),
            "receipt.jpg"
        );

        assertEquals(RefillStatus.REFILLED, refill.getStatus());

        assertTrue(hr.submitClaim(refill));

        assertEquals(RefillStatus.REIMBURSED, refill.getStatus());
    }

    @Test
    void shouldTrackSpendingSeparatelyForBothCurrencies() {

        RefillService service = new RefillService();

        Refill oslo = service.createRefill(Office.OSLO);
        service.claimRefill(oslo.getId(), "John");
        service.completeRefill(
            oslo.getId(), new BigDecimal("500"), "oslo.jpg"
        );

        Refill bengaluru = service.createRefill(Office.BENGALURU);
        service.claimRefill(bengaluru.getId(), "Rakesh");
        service.completeRefill(
            bengaluru.getId(), new BigDecimal("2000"), "blr.jpg"
        );

        assertEquals(
            new BigDecimal("500"),
            service.getSpending().get(Office.OSLO)
        );

        assertEquals(
            new BigDecimal("2000"),
            service.getSpending().get(Office.BENGALURU)
        );
    }
}
