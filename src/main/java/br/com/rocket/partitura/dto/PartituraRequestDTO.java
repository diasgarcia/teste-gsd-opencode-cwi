package br.com.rocket.partitura.dto;

import br.com.rocket.partitura.entity.Dificuldade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartituraRequestDTO {

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 255, message = "O título deve ter no máximo 255 caracteres")
    private String titulo;

    @NotBlank(message = "O compositor é obrigatório")
    @Size(max = 255, message = "O compositor deve ter no máximo 255 caracteres")
    private String compositor;

    @NotBlank(message = "O instrumento é obrigatório")
    @Size(max = 255, message = "O instrumento deve ter no máximo 255 caracteres")
    private String instrumento;

    @Size(max = 100, message = "O género deve ter no máximo 100 caracteres")
    private String genero;

    @NotNull(message = "A dificuldade é obrigatória")
    private Dificuldade dificuldade;

    private Integer ano;

    @Size(max = 500, message = "A URL do arquivo deve ter no máximo 500 caracteres")
    private String arquivoUrl;

    @Size(max = 10000, message = "As observações devem ter no máximo 10000 caracteres")
    private String observacoes;

}
