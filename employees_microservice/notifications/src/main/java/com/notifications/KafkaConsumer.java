package com.notifications;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class KafkaConsumer {

    private final List<String> messages = new ArrayList<>();

    @KafkaListener(topics = "employees", groupId = "emp")
    public void listen(String message) {
        System.out.println(message);
        messages.add(message);
    }

    public List<String> getList() {
        return messages;
    }
}
