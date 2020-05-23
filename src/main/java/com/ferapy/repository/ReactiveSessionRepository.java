package com.ferapy.repository;

import com.ferapy.document.SessionDO;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ReactiveSessionRepository extends ReactiveMongoRepository<SessionDO, String> {

    Mono<SessionDO> findFirstByTalkerIsNullAndIsVideoSession(boolean isVideoSession);
    Mono<SessionDO> findFirstByListenerIsNullAndIsVideoSession(boolean isVideoSession);

}
