package application.record;

public record TokenDTO(
    String token,
    String tipo,
    Long id,
    String nome,
    String email
) {}