package br.com.rocket.partitura.dto;

import br.com.rocket.partitura.entity.Dificuldade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartituraResponseDTO {

    private Long id;
    private String titulo;
    private String compositor;
    private String instrumento;
    private String genero;
    private Dificuldade dificuldade;
    private Integer ano;
    private String arquivoUrl;
    private String observacoes;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

}
