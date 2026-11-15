
package com.framsikt.snacks;

public class ChatService {

    public void notifyVolunteers(Office office, String refillId) {
        System.out.println(
            "CHAT: Refill needed at " + office
            + ". Request ID: " + refillId);
    }
}
