package br.com.rocket.partitura.service;

import br.com.rocket.partitura.dto.PartituraRequestDTO;
import br.com.rocket.partitura.dto.PartituraResponseDTO;
import br.com.rocket.partitura.entity.Partitura;
import br.com.rocket.partitura.exception.PartituraNotFoundException;
import br.com.rocket.partitura.mapper.PartituraMapper;
import br.com.rocket.partitura.repository.PartituraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartituraService {

    private final PartituraRepository repository;
    private final PartituraMapper mapper;

    @Transactional
    public PartituraResponseDTO criar(PartituraRequestDTO dto) {
        Partitura partitura = mapper.toEntity(dto);
        Partitura saved = repository.save(partitura);
        return mapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<PartituraResponseDTO> listar(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public PartituraResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new PartituraNotFoundException(id));
    }

    @Transactional
    public PartituraResponseDTO atualizar(Long id, PartituraRequestDTO dto) {
        Partitura partitura = repository.findById(id)
                .orElseThrow(() -> new PartituraNotFoundException(id));
        mapper.updateEntityFromDTO(dto, partitura);
        Partitura updated = repository.save(partitura);
        return mapper.toResponseDTO(updated);
    }

    @Transactional
    public void remover(Long id) {
        if (!repository.existsById(id)) {
            throw new PartituraNotFoundException(id);
        }
        repository.deleteById(id);
    }

}
