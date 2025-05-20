package application.record;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CursoDTO(
    Long id,
    
    @NotBlank(message = "Nome é obrigatório")
    String nome,
    
    @NotBlank(message = "Descrição é obrigatória")
    String descricao,
    
    @NotNull(message = "Carga horária é obrigatória")
    @Min(value = 1, message = "Carga horária deve ser pelo menos 1 hora")
    Integer cargaHoraria,
    
    List<ModuloDTO> modulos,
    
    List<AlunoSimplificadoDTO> alunos
) {}