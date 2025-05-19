package application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import application.model.Aluno;
import application.model.Curso;

@Service
public class CursoService {
    @Autowired private cursoRepository cursoRepository;
    @Autowired private alunoRepository alunoRepository;

    public Curso salvar(Curso curso) {
        return cursoRepository.save(curso);
    }

    public void matricularAluno(Long cursoId, Long alunoId) {
        Curso curso = cursoRepository.findById(cursoId).orElseThrow();
        Aluno aluno = alunoRepository.findById(alunoId).orElseThrow();
        aluno.getCursos().add(curso);
        alunoRepository.save(aluno);
    }

    public List<Curso> listarCursosDoAluno(Long alunoId) {
        Aluno aluno = alunoRepository.findById(alunoId).orElseThrow();
        return aluno.getCursos();
    }
}
