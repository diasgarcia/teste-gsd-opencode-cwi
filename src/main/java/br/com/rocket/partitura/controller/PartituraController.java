package br.com.rocket.partitura.controller;

import br.com.rocket.partitura.dto.PartituraRequestDTO;
import br.com.rocket.partitura.dto.PartituraResponseDTO;
import br.com.rocket.partitura.service.PartituraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/partituras")
@RequiredArgsConstructor
public class PartituraController {

    private final PartituraService service;

    @PostMapping
    public ResponseEntity<PartituraResponseDTO> criar(@Valid @RequestBody PartituraRequestDTO dto) {
        PartituraResponseDTO response = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<PartituraResponseDTO>> listar(
            @PageableDefault(size = 20, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PartituraResponseDTO> response = service.listar(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartituraResponseDTO> buscarPorId(@PathVariable Long id) {
        PartituraResponseDTO response = service.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartituraResponseDTO> atualizar(@PathVariable Long id,
                                                           @Valid @RequestBody PartituraRequestDTO dto) {
        PartituraResponseDTO response = service.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }

}
