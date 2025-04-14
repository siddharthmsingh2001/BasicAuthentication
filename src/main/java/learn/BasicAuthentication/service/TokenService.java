package learn.BasicAuthentication.service;

import learn.BasicAuthentication.model.Token;
import learn.BasicAuthentication.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    @Transactional
    public void save(Token token){
        tokenRepository.save(token);
    }


    public Optional<Token> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }
}

