package application.record;

public class CursoDTO {
    private Long id;
    private String nome;
    private String descricao;
    private int cargaHoraria;

    public CursoDTO(Curso curso) {
        this.id = curso.getId();
        this.nome = curso.getNome();
        this.descricao = curso.getDescricao();
        this.cargaHoraria = curso.getCargaHoraria();
    }
