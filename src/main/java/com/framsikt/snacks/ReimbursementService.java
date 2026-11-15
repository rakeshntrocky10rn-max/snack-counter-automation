
package com.framsikt.snacks;

public class ReimbursementService {

    public boolean submitClaim(Refill refill) {

        if (refill.getStatus() != RefillStatus.REFILLED) {
            return false;
        }

        System.out.println("Submitting HR claim: "
                + refill.getId());

        refill.markReimbursed();

        return true;
    }
}
