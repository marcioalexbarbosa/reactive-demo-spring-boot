package com.example.demo;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
class DummyService {

    private final ApplicationEventPublisher publisher;
    private final DummyRepository dummyRepository;

    DummyService(ApplicationEventPublisher publisher, DummyRepository dummyRepository) {
        this.publisher = publisher;
        this.dummyRepository = dummyRepository;
    }

    public Flux<Dummy> all() {
        return this.dummyRepository.findAll();
    }

    public Mono<Dummy> get(String id) {
        return this.dummyRepository.findById(id);
    }

    public Mono<Dummy> update(String id, String name) {
        return this.dummyRepository
                .findById(id)
                .map(p -> new Dummy(p.getId(), name))
                .flatMap(this.dummyRepository::save);
    }

    public Mono<Dummy> delete(String id) {
        return this.dummyRepository
                .findById(id)
                .flatMap(p -> this.dummyRepository.deleteById(p.getId()).thenReturn(p));
    }

    public Mono<Dummy> create(String name) {
        return this.dummyRepository
                .save(new Dummy(null, name))
                .doOnSuccess(dummy -> this.publisher.publishEvent(new DummyCreatedEvent(dummy)));
    }
}