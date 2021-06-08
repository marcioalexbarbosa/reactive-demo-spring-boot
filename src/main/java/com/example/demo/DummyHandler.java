package com.example.demo;

import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Random;

@Component
class DummyHandler {

    private final DummyService dummyService;

    DummyHandler(DummyService dummyService) {
        this.dummyService = dummyService;
    }

    Mono<ServerResponse> getById(ServerRequest r) {
        return defaultReadResponse(this.dummyService.get(id(r)));
    }

    Mono<ServerResponse> all(ServerRequest r) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(dummyService.all(), Dummy.class);
    }

    Mono<ServerResponse> deleteById(ServerRequest r) {
        return defaultReadResponse(this.dummyService.delete(id(r)));
    }

    Mono<ServerResponse> updateById(ServerRequest r) {
        Flux<Dummy> id = r.bodyToFlux(Dummy.class)
                .flatMap(p -> this.dummyService.update(id(r), p.getName()));
        return defaultReadResponse(id);
    }

    Mono<ServerResponse> create(ServerRequest request) {
        Flux<Dummy> flux = request
                .bodyToFlux(Dummy.class)
                .flatMap(toWrite -> this.dummyService.create(randomString()));
        return defaultWriteResponse(flux);
    }

    public String randomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 8;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    // <3>
    private static Mono<ServerResponse> defaultWriteResponse(Publisher<Dummy> dummies) {
        return Mono
                .from(dummies)
                .flatMap(p -> ServerResponse
                        .created(URI.create("/dummies/" + p.getId()))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .build()
                );
    }

    // <4>
    private static Mono<ServerResponse> defaultReadResponse(Publisher<Dummy> dummies) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(dummies, Dummy.class);
    }

    private static String id(ServerRequest r) {
        return r.pathVariable("id");
    }
}
