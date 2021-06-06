package com.example.demo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

interface DummyRepository extends ReactiveMongoRepository<Dummy, String> {
}
