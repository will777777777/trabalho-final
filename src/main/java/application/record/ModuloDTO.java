package application.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ModuloDTO(
    Long id,
    
    @NotBlank(message = "Título é obrigatório")
    String titulo,
    
    @NotBlank(message = "Descrição é obrigatória")
    String descricao,
    
    @NotNull(message = "ID do curso é obrigatório")
    Long cursoId
) {}

// MatriculaDTO.java
package application.record;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record MatriculaDTO(
    Long id,
    
    @NotNull(message = "ID do aluno é obrigatório")
    Long alunoId,
    
    @NotNull(message = "ID do curso é obrigatório")
    Long cursoId,
    
    LocalDateTime dataMatricula
) {}
