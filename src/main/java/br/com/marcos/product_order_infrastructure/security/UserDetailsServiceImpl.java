package br.com.marcos.product_order_infrastructure.security;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.marcos.product_order_infrastructure.repository.UserRepository;

@Service("userDetailsService")
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    public UserDetailsServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Como sua classe User JÁ implementa UserDetails, basta retorná-la diretamente.
        // O Spring Security usará os métodos getPassword() e getAuthorities() que você configurou nela.
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }
}