package br.com.rocket.partitura.mapper;

import br.com.rocket.partitura.dto.PartituraRequestDTO;
import br.com.rocket.partitura.dto.PartituraResponseDTO;
import br.com.rocket.partitura.entity.Partitura;
import org.springframework.stereotype.Component;

@Component
public class PartituraMapper {

    public Partitura toEntity(PartituraRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Partitura.builder()
                .titulo(dto.getTitulo())
                .compositor(dto.getCompositor())
                .instrumento(dto.getInstrumento())
                .genero(dto.getGenero())
                .dificuldade(dto.getDificuldade())
                .ano(dto.getAno())
                .arquivoUrl(dto.getArquivoUrl())
                .observacoes(dto.getObservacoes())
                .build();
    }

    public PartituraResponseDTO toResponseDTO(Partitura entity) {
        if (entity == null) {
            return null;
        }
        return PartituraResponseDTO.builder()
                .id(entity.getId())
                .titulo(entity.getTitulo())
                .compositor(entity.getCompositor())
                .instrumento(entity.getInstrumento())
                .genero(entity.getGenero())
                .dificuldade(entity.getDificuldade())
                .ano(entity.getAno())
                .arquivoUrl(entity.getArquivoUrl())
                .observacoes(entity.getObservacoes())
                .criadoEm(entity.getCriadoEm())
                .atualizadoEm(entity.getAtualizadoEm())
                .build();
    }

    public void updateEntityFromDTO(PartituraRequestDTO dto, Partitura entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setTitulo(dto.getTitulo());
        entity.setCompositor(dto.getCompositor());
        entity.setInstrumento(dto.getInstrumento());
        entity.setGenero(dto.getGenero());
        entity.setDificuldade(dto.getDificuldade());
        entity.setAno(dto.getAno());
        entity.setArquivoUrl(dto.getArquivoUrl());
        entity.setObservacoes(dto.getObservacoes());
    }

}
