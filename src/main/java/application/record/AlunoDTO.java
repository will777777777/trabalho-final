package application.record;

import application.model.Aluno;

public class AlunoDTO {
    private Long id;
    private String nome;
    private String email;

    public AlunoDTO(Aluno aluno) {
        this.id = aluno.getId();
        this.nome = aluno.getNome();
        this.email = aluno.getEmail();
    }

    // Getters e Setters
}