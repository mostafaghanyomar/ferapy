package com.ferapy.service;

import com.ferapy.document.SessionDO;
import com.ferapy.dto.SessionDTO;
import com.ferapy.repository.ReactiveSessionRepository;
import com.ferapy.security.JWTUser;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import static com.ferapy.security.JWTUtil.copyBeanProperties;
import static com.ferapy.security.JWTUtil.getCurrentJWTUser;

@Service
@Transactional
@AllArgsConstructor
public class MatcherService {
    private ReactiveSessionRepository sessionRepository;

    public Mono<ResponseEntity> talk(String sessionId, boolean video, boolean anonymous) {
        final JWTUser t = getCurrentJWTUser();
        return Mono.justOrEmpty(sessionId)
                .flatMap(id -> sessionRepository.findById(id))
                .switchIfEmpty(sessionRepository.findFirstByTalkerIsNullAndIsVideoSession(video))
                .switchIfEmpty(Mono.just(new SessionDO().setIsTalkerAnonymous(anonymous).setIsVideoSession(video)))
                .flatMap(s -> this.createZoomSession(s.setTalker(t.getId()), t))
                .flatMap(s -> sessionRepository.save(s.audit()))
                .map(s -> copyBeanProperties(s, new SessionDTO()))
                .map(ResponseEntity::ok);
    }

    public Mono<ResponseEntity> listen(String sessionId, boolean video) {
        final JWTUser l = getCurrentJWTUser();
        return Mono.justOrEmpty(sessionId)
                .flatMap(id -> sessionRepository.findById(id))
                .switchIfEmpty(sessionRepository.findFirstByListenerIsNullAndIsVideoSession(video))
                .switchIfEmpty(Mono.just(new SessionDO().setIsVideoSession(video)))
                .flatMap(s -> this.createZoomSession(s.setListener(l.getId()), l))
                .flatMap(s -> sessionRepository.save(s.audit()))
                .map(s -> copyBeanProperties(s, new SessionDTO()))
                .map(ResponseEntity::ok);
    }

    private Mono<SessionDO> createZoomSession(SessionDO s, JWTUser u) {
        return Mono.justOrEmpty(s
                //TODO create zoom session
//                .setZoomId("123-123-123")
        );
    }

}
