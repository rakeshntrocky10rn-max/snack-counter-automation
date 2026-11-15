
package com.framsikt.snacks;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {

        RefillService service = new RefillService();
        ChatService chat = new ChatService();
        ReimbursementService hr = new ReimbursementService();

        Refill refill = service.createRefill(Office.BENGALURU);

        chat.notifyVolunteers(Office.BENGALURU, refill.getId());

        service.claimRefill(refill.getId(), "Rakesh");

        service.completeRefill(
            refill.getId(),
            new BigDecimal("2500"),
            "receipt-001.jpg"
        );

        hr.submitClaim(refill);

        System.out.println("Final status: " + refill.getStatus());
        System.out.println("Monthly spending: " + service.getSpending());
    }
}
