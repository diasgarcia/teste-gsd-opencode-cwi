package br.com.rocket.partitura.service;

import br.com.rocket.partitura.dto.PartituraRequestDTO;
import br.com.rocket.partitura.dto.PartituraResponseDTO;
import br.com.rocket.partitura.entity.Dificuldade;
import br.com.rocket.partitura.entity.Partitura;
import br.com.rocket.partitura.exception.PartituraNotFoundException;
import br.com.rocket.partitura.mapper.PartituraMapper;
import br.com.rocket.partitura.repository.PartituraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PartituraService - Testes unitários")
class PartituraServiceTest {

    @Mock
    private PartituraRepository repository;

    @Mock
    private PartituraMapper mapper;

    @InjectMocks
    private PartituraService service;

    private Partitura partitura;
    private PartituraRequestDTO requestDTO;
    private PartituraResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 5, 12, 0);

        requestDTO = PartituraRequestDTO.builder()
                .titulo("Für Elise")
                .compositor("Ludwig van Beethoven")
                .instrumento("Piano")
                .genero("Clássico")
                .dificuldade(Dificuldade.INTERMEDIARIO)
                .ano(1810)
                .arquivoUrl("https://example.com/fur-elise.pdf")
                .build();

        partitura = Partitura.builder()
                .id(1L)
                .titulo("Für Elise")
                .compositor("Ludwig van Beethoven")
                .instrumento("Piano")
                .genero("Clássico")
                .dificuldade(Dificuldade.INTERMEDIARIO)
                .ano(1810)
                .arquivoUrl("https://example.com/fur-elise.pdf")
                .criadoEm(now)
                .atualizadoEm(now)
                .build();

        responseDTO = PartituraResponseDTO.builder()
                .id(1L)
                .titulo("Für Elise")
                .compositor("Ludwig van Beethoven")
                .instrumento("Piano")
                .genero("Clássico")
                .dificuldade(Dificuldade.INTERMEDIARIO)
                .ano(1810)
                .arquivoUrl("https://example.com/fur-elise.pdf")
                .criadoEm(now)
                .atualizadoEm(now)
                .build();
    }

    // ── criar ────────────────────────────────────────────────────

    @Test
    @DisplayName("criar - deve criar partitura com sucesso")
    void criar_deveCriarPartituraComSucesso() {
        when(mapper.toEntity(requestDTO)).thenReturn(partitura);
        when(repository.save(partitura)).thenReturn(partitura);
        when(mapper.toResponseDTO(partitura)).thenReturn(responseDTO);

        PartituraResponseDTO result = service.criar(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitulo()).isEqualTo("Für Elise");
        assertThat(result.getCompositor()).isEqualTo("Ludwig van Beethoven");
        assertThat(result.getDificuldade()).isEqualTo(Dificuldade.INTERMEDIARIO);

        verify(mapper).toEntity(requestDTO);
        verify(repository).save(partitura);
        verify(mapper).toResponseDTO(partitura);
    }

    @Test
    @DisplayName("criar - deve criar partitura apenas com campos obrigatórios")
    void criar_deveCriarPartituraApenasComCamposObrigatorios() {
        PartituraRequestDTO minimalDTO = PartituraRequestDTO.builder()
                .titulo("Estudo Simples")
                .compositor("Carlo Domeniconi")
                .instrumento("Violão")
                .dificuldade(Dificuldade.INICIANTE)
                .build();

        Partitura minimalEntity = Partitura.builder()
                .id(2L)
                .titulo("Estudo Simples")
                .compositor("Carlo Domeniconi")
                .instrumento("Violão")
                .dificuldade(Dificuldade.INICIANTE)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        PartituraResponseDTO minimalResponse = PartituraResponseDTO.builder()
                .id(2L)
                .titulo("Estudo Simples")
                .compositor("Carlo Domeniconi")
                .instrumento("Violão")
                .dificuldade(Dificuldade.INICIANTE)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        when(mapper.toEntity(minimalDTO)).thenReturn(minimalEntity);
        when(repository.save(minimalEntity)).thenReturn(minimalEntity);
        when(mapper.toResponseDTO(minimalEntity)).thenReturn(minimalResponse);

        PartituraResponseDTO result = service.criar(minimalDTO);

        assertThat(result).isNotNull();
        assertThat(result.getTitulo()).isEqualTo("Estudo Simples");
        assertThat(result.getGenero()).isNull();
        assertThat(result.getAno()).isNull();
    }

    // ── listar ───────────────────────────────────────────────────

    @Test
    @DisplayName("listar - deve retornar página de partituras")
    void listar_deveRetornarPaginaDePartituras() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Partitura> page = new PageImpl<>(List.of(partitura), pageable, 1);

        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.toResponseDTO(partitura)).thenReturn(responseDTO);

        Page<PartituraResponseDTO> result = service.listar(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitulo()).isEqualTo("Für Elise");

        verify(repository).findAll(pageable);
    }

    @Test
    @DisplayName("listar - deve retornar página vazia quando não há partituras")
    void listar_deveRetornarPaginaVazia() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Partitura> emptyPage = Page.empty();

        when(repository.findAll(pageable)).thenReturn(emptyPage);

        Page<PartituraResponseDTO> result = service.listar(pageable);

        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isTrue();

        verify(repository).findAll(pageable);
    }

    // ── buscarPorId ──────────────────────────────────────────────

    @Test
    @DisplayName("buscarPorId - deve retornar partitura quando encontrada")
    void buscarPorId_deveRetornarPartituraQuandoEncontrada() {
        when(repository.findById(1L)).thenReturn(Optional.of(partitura));
        when(mapper.toResponseDTO(partitura)).thenReturn(responseDTO);

        PartituraResponseDTO result = service.buscarPorId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitulo()).isEqualTo("Für Elise");

        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId - deve lançar PartituraNotFoundException quando ID não existe")
    void buscarPorId_deveLancarExceptionQuandoIdNaoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(999L))
                .isInstanceOf(PartituraNotFoundException.class)
                .hasMessageContaining("999");

        verify(repository).findById(999L);
    }

    // ── atualizar ────────────────────────────────────────────────

    @Test
    @DisplayName("atualizar - deve atualizar partitura com sucesso")
    void atualizar_deveAtualizarPartituraComSucesso() {
        PartituraRequestDTO updateDTO = PartituraRequestDTO.builder()
                .titulo("Für Elise (Revisada)")
                .compositor("Ludwig van Beethoven")
                .instrumento("Piano")
                .genero("Clássico")
                .dificuldade(Dificuldade.AVANCADO)
                .ano(1810)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(partitura));
        when(repository.save(any(Partitura.class))).thenReturn(partitura);
        when(mapper.toResponseDTO(any(Partitura.class))).thenReturn(responseDTO);

        PartituraResponseDTO result = service.atualizar(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(mapper).updateEntityFromDTO(updateDTO, partitura);
        verify(repository).save(partitura);
    }

    @Test
    @DisplayName("atualizar - deve lançar PartituraNotFoundException quando ID não existe")
    void atualizar_deveLancarExceptionQuandoIdNaoExiste() {
        PartituraRequestDTO updateDTO = PartituraRequestDTO.builder()
                .titulo("Título")
                .compositor("Compositor")
                .instrumento("Instrumento")
                .dificuldade(Dificuldade.INICIANTE)
                .build();

        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizar(999L, updateDTO))
                .isInstanceOf(PartituraNotFoundException.class)
                .hasMessageContaining("999");

        verify(repository).findById(999L);
        verify(repository, never()).save(any());
    }

    // ── remover ──────────────────────────────────────────────────

    @Test
    @DisplayName("remover - deve remover partitura quando existir")
    void remover_deveRemoverPartituraQuandoExistir() {
        when(repository.existsById(1L)).thenReturn(true);

        service.remover(1L);

        verify(repository).existsById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("remover - deve lançar PartituraNotFoundException quando ID não existe")
    void remover_deveLancarExceptionQuandoIdNaoExiste() {
        when(repository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> service.remover(999L))
                .isInstanceOf(PartituraNotFoundException.class)
                .hasMessageContaining("999");

        verify(repository).existsById(999L);
        verify(repository, never()).deleteById(any());
    }

    // ── integridade DTO → Entity ─────────────────────────────────

    @Test
    @DisplayName("criar - deve preservar todos os campos do DTO na entidade")
    void criar_devePreservarTodosOsCampos() {
        when(mapper.toEntity(requestDTO)).thenReturn(partitura);
        when(repository.save(partitura)).thenReturn(partitura);
        when(mapper.toResponseDTO(partitura)).thenReturn(responseDTO);

        PartituraResponseDTO result = service.criar(requestDTO);

        assertThat(result.getTitulo()).isEqualTo(requestDTO.getTitulo());
        assertThat(result.getCompositor()).isEqualTo(requestDTO.getCompositor());
        assertThat(result.getInstrumento()).isEqualTo(requestDTO.getInstrumento());
        assertThat(result.getGenero()).isEqualTo(requestDTO.getGenero());
        assertThat(result.getDificuldade()).isEqualTo(requestDTO.getDificuldade());
        assertThat(result.getAno()).isEqualTo(requestDTO.getAno());
        assertThat(result.getArquivoUrl()).isEqualTo(requestDTO.getArquivoUrl());
    }

}
