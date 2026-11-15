
package com.framsikt.snacks;

import java.util.*;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.concurrent.ConcurrentHashMap;

public class RefillService {

    private final Map<String, Refill> refills =
            new ConcurrentHashMap<>();

    private final Map<Office, BigDecimal> budgets =
            new EnumMap<>(Office.class);

    private final Map<Office, BigDecimal> spending =
            new EnumMap<>(Office.class);

    public RefillService() {
        budgets.put(Office.OSLO, new BigDecimal("6000"));
        budgets.put(Office.BENGALURU, new BigDecimal("15000"));

        spending.put(Office.OSLO, BigDecimal.ZERO);
        spending.put(Office.BENGALURU, BigDecimal.ZERO);
    }

    public Refill createRefill(Office office) {
        Refill refill = new Refill(office);
        refills.put(refill.getId(), refill);
        return refill;
    }

    public synchronized boolean claimRefill(
            String id, String volunteer) {

        Refill refill = refills.get(id);

        if (refill == null) return false;

        return refill.claim(volunteer);
    }

    public synchronized void completeRefill(
            String id, BigDecimal amount, String receipt) {

        Refill refill = refills.get(id);

        if (refill == null) {
            throw new IllegalArgumentException("Refill not found");
        }

        Office office = refill.getOffice();

        String currency = office == Office.OSLO ? "NOK" : "INR";

        BigDecimal newTotal = spending.get(office).add(amount);

        if (newTotal.compareTo(budgets.get(office)) > 0) {
            throw new IllegalStateException("Monthly budget exceeded");
        }

        refill.complete(amount, currency, receipt);

        spending.put(office, newTotal);
    }

    public synchronized void reassignExpiredClaims() {
        for (Refill refill : refills.values()) {
            if (refill.getStatus() == RefillStatus.CLAIMED
                    && refill.getClaimDeadline() != null
                    && refill.getClaimDeadline()
                        .isBefore(java.time.LocalDateTime.now())) {
                refill.releaseClaim();
            }
        }
    }

    public Map<Office, BigDecimal> getSpending() {
        return Collections.unmodifiableMap(
                new EnumMap<>(spending));
    }

    public Collection<Refill> getAllRefills() {
        return Collections.unmodifiableCollection(
                new ArrayList<>(refills.values()));
    }
}
