package application.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import application.model.Aluno;
import application.record.AlunoDTO;
import application.record.GenericResponse;
import application.record.JwtDTO;
import application.record.LoginDTO;
import application.repository.AlunoRepository;
import application.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Autenticação")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário", description = "Autentica um usuário e retorna um token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Credenciais inválidas ou erro na requisição")
    })
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            Optional<Aluno> alunoOpt = alunoRepository.findByEmail(loginDTO.email());
            if (alunoOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new GenericResponse("Aluno não encontrado com este email"));
            }

            Aluno aluno = alunoOpt.get();

            if (!passwordEncoder.matches(loginDTO.senha(), aluno.getSenha())) {
                return ResponseEntity.badRequest()
                        .body(new GenericResponse("Senha incorreta"));
            }

            String token = tokenService.generateToken(aluno);

            return ResponseEntity.ok(
                    new JwtDTO(token, aluno.getId(), aluno.getNome(), aluno.getEmail())
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new GenericResponse("Erro no login: " + e.getMessage()));
        }
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar novo usuário", description = "Registra um novo usuário no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro na requisição ou e-mail já em uso")
    })
    public ResponseEntity<?> registro(@RequestBody AlunoDTO alunoDTO) {
        try {
            if (alunoRepository.existsByEmail(alunoDTO.email())) {
                return ResponseEntity.badRequest()
                        .body(new GenericResponse("Email já está em uso!"));
            }

            Aluno aluno = new Aluno();
            aluno.setNome(alunoDTO.nome());
            aluno.setEmail(alunoDTO.email());
            aluno.setSenha(passwordEncoder.encode(alunoDTO.senha()));

            Aluno alunoSalvo = alunoRepository.save(aluno);

            String token = tokenService.generateToken(alunoSalvo);

            return ResponseEntity.ok(
                    new JwtDTO(token, alunoSalvo.getId(), alunoSalvo.getNome(), alunoSalvo.getEmail())
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new GenericResponse("Erro ao registrar: " + e.getMessage()));
        }
    }

    @GetMapping("/teste")
    @Operation(summary = "Testar autenticação", description = "Verifica se a API de autenticação está funcionando")
    @ApiResponse(responseCode = "200", description = "API funcionando corretamente")
    public ResponseEntity<GenericResponse> teste() {
        return ResponseEntity.ok(new GenericResponse("API de autenticação funcionando!"));
    }

    @GetMapping("/usuarios")
    @Operation(summary = "Listar usuários", description = "Lista todos os usuários cadastrados (alunos)")
    @ApiResponse(responseCode = "200", description = "Usuários listados com sucesso")
    public ResponseEntity<?> listarUsuarios() {
        return ResponseEntity.ok(alunoRepository.findAll());
    }
}
