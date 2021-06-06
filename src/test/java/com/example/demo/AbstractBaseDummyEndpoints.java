package com.example.demo;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Log4j2
@WebFluxTest
public abstract class AbstractBaseDummyEndpoints {

    private final WebTestClient client;

    @MockBean
    private DummyRepository repository;

    public AbstractBaseDummyEndpoints(WebTestClient client) {
        this.client = client;
    }

    @Test
    public void getAll() {

        log.info("running  " + this.getClass().getName());

        Mockito
            .when(this.repository.findAll())
            .thenReturn(Flux.just(new Dummy("1", "A"), new Dummy("2", "B")));

        this.client
            .get()
            .uri("/dummies")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
            .expectBody()
            .jsonPath("$.[0].id").isEqualTo("1")
            .jsonPath("$.[0].name").isEqualTo("A")
            .jsonPath("$.[1].id").isEqualTo("2")
            .jsonPath("$.[1].name").isEqualTo("B");
    }

    @Test
    public void save() {
        Dummy data = new Dummy("123", UUID.randomUUID().toString());
        Mockito
            .when(this.repository.save(Mockito.any(Dummy.class)))
            .thenReturn(Mono.just(data));
        MediaType jsonUtf8 = MediaType.APPLICATION_JSON_UTF8;
        this
            .client
            .post()
            .uri("/dummies")
            .contentType(jsonUtf8)
            .body(Mono.just(data), Dummy.class)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().contentType(jsonUtf8);
    }

    @Test
    public void delete() {
        Dummy data = new Dummy("123", UUID.randomUUID().toString());
        Mockito
            .when(this.repository.findById(data.getId()))
            .thenReturn(Mono.just(data));
        Mockito
            .when(this.repository.deleteById(data.getId()))
            .thenReturn(Mono.empty());
        this
            .client
            .delete()
            .uri("/dummies/" + data.getId())
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    public void update() {
        Dummy data = new Dummy("123", UUID.randomUUID().toString() + "name");

        Mockito
            .when(this.repository.findById(data.getId()))
            .thenReturn(Mono.just(data));

        Mockito
            .when(this.repository.save(data))
            .thenReturn(Mono.just(data));

        this
            .client
            .put()
            .uri("/dummies/" + data.getId())
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(Mono.just(data), Dummy.class)
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    public void getById() {

        Dummy data = new Dummy("1", "A");

        Mockito
            .when(this.repository.findById(data.getId()))
            .thenReturn(Mono.just(data));

        this.client
            .get()
            .uri("/dummies/" + data.getId())
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
            .expectBody()
            .jsonPath("$.id").isEqualTo(data.getId())
            .jsonPath("$.name").isEqualTo(data.getName());
    }
}