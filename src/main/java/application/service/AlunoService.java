package application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlunoService implements UserDetailsService {
    @Autowired private alunoRepository alunoRepository;
    @Autowired private passwordEncoder passwordEncoder;

    public Aluno salvar(Aluno aluno) {
        aluno.setSenha(passwordEncoder.encode(aluno.getSenha()));
        return alunoRepository.save(aluno);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return alunoRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}
