package com.example.demo;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.util.function.Predicate;

@Log4j2
@DataMongoTest
@Import(DummyService.class)
public class DummyServiceTest {

    private final DummyService service;
    private final DummyRepository repository;

    public DummyServiceTest(@Autowired DummyService service,
                            @Autowired DummyRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @Test
    public void getAll() {
        Flux<Dummy> saved = repository.saveAll(Flux.just(new Dummy(null, "Josh"), new Dummy(null, "Matt"), new Dummy(null, "Jane")));

        Flux<Dummy> composite = service.all().thenMany(saved);

        Predicate<Dummy> match = dummy -> saved.any(saveItem -> saveItem.equals(dummy)).block();

        StepVerifier
            .create(composite)
            .expectNextMatches(match)
            .expectNextMatches(match)
            .expectNextMatches(match)
            .verifyComplete();
    }

    @Test
    public void save() {
        Mono<Dummy> dummyMono = this.service.create("name");
        StepVerifier
            .create(dummyMono)
            .expectNextMatches(saved -> StringUtils.hasText(saved.getId()))
            .verifyComplete();
    }

    @Test
    public void delete() {
        String test = "test";
        Mono<Dummy> deleted = this.service
            .create(test)
            .flatMap(saved -> this.service.delete(saved.getId()));
        StepVerifier
            .create(deleted)
            .expectNextMatches(dummy -> dummy.getName().equalsIgnoreCase(test))
            .verifyComplete();
    }

    @Test
    public void update() {
        Mono<Dummy> saved = this.service
            .create("test")
            .flatMap(p -> this.service.update(p.getId(), "test1"));
        StepVerifier
            .create(saved)
            .expectNextMatches(p -> p.getName().equalsIgnoreCase("test1"))
            .verifyComplete();
    }

    @Test
    public void getById() {
        String test = UUID.randomUUID().toString();
        Mono<Dummy> deleted = this.service
            .create(test)
            .flatMap(saved -> this.service.get(saved.getId()));
        StepVerifier
            .create(deleted)
            .expectNextMatches(dummy -> StringUtils.hasText(dummy.getId()) && test.equalsIgnoreCase(dummy.getName()))
            .verifyComplete();
    }
}