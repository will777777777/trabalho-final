package application.service;

import application.exception.RecursoNaoEncontradoException;
import application.model.Aluno;
import application.model.Curso;
import application.model.Matricula;
import application.record.AlunoDTO;
import application.record.AlunoSimplificadoDTO;
import application.record.CursoDTO;
import application.record.ModuloDTO;
import application.repository.AlunoRepository;
import application.repository.CursoRepository;
import application.repository.MatriculaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;
    private final MatriculaRepository matriculaRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AlunoService(AlunoRepository alunoRepository, 
                        CursoRepository cursoRepository,
                        MatriculaRepository matriculaRepository,
                        PasswordEncoder passwordEncoder) {
        this.alunoRepository = alunoRepository;
        this.cursoRepository = cursoRepository;
        this.matriculaRepository = matriculaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AlunoSimplificadoDTO criarAluno(AlunoDTO alunoDTO) {
        if (alunoRepository.existsByEmail(alunoDTO.email())) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        Aluno aluno = new Aluno();
        aluno.setNome(alunoDTO.nome());
        aluno.setEmail(alunoDTO.email());
        aluno.setSenha(passwordEncoder.encode(alunoDTO.senha()));

        Aluno alunoSalvo = alunoRepository.save(aluno);
        return new AlunoSimplificadoDTO(
            alunoSalvo.getId(),
            alunoSalvo.getNome(),
            alunoSalvo.getEmail()
        );
    }

    @Transactional(readOnly = true)
    public AlunoDTO buscarAlunoPorId(Long id) {
        Aluno aluno = alunoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado com ID: " + id));
        
        return mapToAlunoDTO(aluno);
    }

    @Transactional(readOnly = true)
    public List<AlunoSimplificadoDTO> listarAlunos() {
        return alunoRepository.findAll().stream()
            .map(aluno -> new AlunoSimplificadoDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getEmail()
            ))
            .collect(Collectors.toList());
    }

    @Transactional
    public void matricularEmCurso(Long alunoId, Long cursoId) {
        Aluno aluno = alunoRepository.findById(alunoId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado com ID: " + alunoId));
        
        Curso curso = cursoRepository.findById(cursoId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Curso não encontrado com ID: " + cursoId));
        
        if (matriculaRepository.existsByAlunoIdAndCursoId(alunoId, cursoId)) {
            throw new IllegalArgumentException("Aluno já está matriculado neste curso");
        }
        
        Matricula matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setCurso(curso);
        matricula.setDataMatricula(LocalDateTime.now());
        
        matriculaRepository.save(matricula);
    }

    @Transactional
    public void cancelarMatricula(Long alunoId, Long cursoId) {
        Matricula matricula = matriculaRepository.findByAlunoIdAndCursoId(alunoId, cursoId)
            .orElseThrow(() -> new RecursoNaoEncontradoException(
                "Matrícula não encontrada para o aluno ID: " + alunoId + " e curso ID: " + cursoId));
        
        matriculaRepository.delete(matricula);
    }

    @Transactional(readOnly = true)
    public List<CursoDTO> listarCursosDoAluno(Long aluno)