package com.framsikt.snacks;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Refill {

    private final String id;
    private final Office office;
    private RefillStatus status;
    private String volunteer;
    private BigDecimal amount;
    private String currency;
    private String receipt;
    private LocalDateTime createdAt;
    private LocalDateTime claimDeadline;

    public Refill(Office office) {
        this.id = UUID.randomUUID().toString();
        this.office = office;
        this.status = RefillStatus.OPEN;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Office getOffice() {
        return office;
    }

    public RefillStatus getStatus() {
        return status;
    }

    public String getVolunteer() {
        return volunteer;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getReceipt() {
        return receipt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getClaimDeadline() {
        return claimDeadline;
    }

    public synchronized boolean claim(String volunteer) {
        if (status != RefillStatus.OPEN) {
            return false;
        }

        this.volunteer = volunteer;
        this.claimDeadline = LocalDateTime.now().plusHours(24);
        this.status = RefillStatus.CLAIMED;
        return true;
    }

    public synchronized void complete(
            BigDecimal amount, String currency, String receipt) {

        if (status != RefillStatus.CLAIMED) {
            throw new IllegalStateException("Refill is not claimed");
        }

        this.amount = amount;
        this.currency = currency;
        this.receipt = receipt;
        this.status = RefillStatus.REFILLED;
    }

    public synchronized void markReimbursed() {
        if (status != RefillStatus.REFILLED) {
            throw new IllegalStateException("Refill is not ready for reimbursement");
        }

        this.status = RefillStatus.REIMBURSED;
    }

    public synchronized void releaseClaim() {
        if (status == RefillStatus.CLAIMED) {
            this.volunteer = null;
            this.claimDeadline = null;
            this.status = RefillStatus.OPEN;
        }
    }
}

