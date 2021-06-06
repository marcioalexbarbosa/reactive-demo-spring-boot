package com.example.demo;

import org.springframework.context.ApplicationEvent;

public class DummyCreatedEvent extends ApplicationEvent {

    public DummyCreatedEvent(Dummy source) {
        super(source);
    }
}
