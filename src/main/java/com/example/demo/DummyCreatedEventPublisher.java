package com.example.demo;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Component
class DummyCreatedEventPublisher implements
    ApplicationListener<DummyCreatedEvent>,
    Consumer<FluxSink<DummyCreatedEvent>> {

    private final Executor executor;
    private final BlockingQueue<DummyCreatedEvent> queue =
        new LinkedBlockingQueue<>();

    DummyCreatedEventPublisher(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void onApplicationEvent(DummyCreatedEvent event) {
        this.queue.offer(event);
    }

     @Override
    public void accept(FluxSink<DummyCreatedEvent> sink) {
        this.executor.execute(() -> {
            while (true)
                try {
                    DummyCreatedEvent event = queue.take();
                    sink.next(event);
                }
                catch (InterruptedException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
        });
    }
}
