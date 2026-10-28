package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import net.originmobi.pdv.model.GrupoUsuario;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.repository.GrupoUsuarioRepository;
import net.originmobi.pdv.repository.UsuarioRepository;

class UsuarioServiceIntegrationTest {

    private UsuarioService usuarioService;
    private GrupoUsuarioService grupoService;

    private UsuarioRepository usuarioRepository;
    private GrupoUsuarioRepository grupoRepository;

    @BeforeEach
    void setUp() {

        // Duas unidades reais que serao integradas
        usuarioService = new UsuarioService();
        grupoService = new GrupoUsuarioService();

        // Dubles apenas para o acesso ao banco
        usuarioRepository = mock(UsuarioRepository.class);
        grupoRepository = mock(GrupoUsuarioRepository.class);

        // UsuarioService -> GrupoUsuarioService real
        ReflectionTestUtils.setField(usuarioService, "usuarios", usuarioRepository);
        ReflectionTestUtils.setField(usuarioService, "grupos", grupoService);

        // GrupoUsuarioService -> repository simulado
        ReflectionTestUtils.setField(grupoService, "grupousuarios", grupoRepository);
    }

    private GrupoUsuario novoGrupo(Long codigo) {

        GrupoUsuario grupo = new GrupoUsuario();
        grupo.setCodigo(codigo);

        return grupo;
    }

    // CT-INT-01 - Adicionar ao usuario o grupo buscado pela GrupoUsuarioService

    @Test
    void deveIntegrarUsuarioEGrupoAoAdicionarGrupo() {

        Usuario usuario = new Usuario();
        usuario.setGrupoUsuario(new ArrayList<>());

        GrupoUsuario grupo = novoGrupo(2L);

        when(usuarioRepository.findByCodigoIn(1L)).thenReturn(usuario);
        when(grupoRepository.findByCodigoIn(2L)).thenReturn(grupo);

        String resultado = usuarioService.addGrupo(1L, 2L);

        assertEquals("ok", resultado);

        verify(grupoRepository).findByCodigoIn(2L);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());

        assertEquals(Arrays.asList(grupo), captor.getValue().getGrupoUsuario());
    }

    // CT-INT-02 - Nao adicionar grupo que o usuario ja possui

    @Test
    void naoDeveAdicionarGrupoJaExistenteNaIntegracao() {

        GrupoUsuario grupo = novoGrupo(2L);

        Usuario usuario = new Usuario();
        usuario.setGrupoUsuario(new ArrayList<>(Arrays.asList(grupo)));

        when(usuarioRepository.findByCodigoIn(1L)).thenReturn(usuario);
        when(grupoRepository.findByCodigoIn(2L)).thenReturn(grupo);

        String resultado = usuarioService.addGrupo(1L, 2L);

        assertEquals("ja existe", resultado);
        assertEquals(1, usuario.getGrupoUsuario().size());

        verify(usuarioRepository, never()).save(any());
    }

    // CT-INT-03 - Remover grupo usando a lista de grupos do usuario
    // devolvida pela GrupoUsuarioService

    @Test
    void deveIntegrarUsuarioEGrupoAoRemoverGrupo() {

        Usuario usuario = new Usuario();

        GrupoUsuario grupo1 = novoGrupo(1L);
        GrupoUsuario grupo2 = novoGrupo(2L);

        List<GrupoUsuario> gruposDoUsuario = new ArrayList<>(Arrays.asList(grupo1, grupo2));

        when(usuarioRepository.findByCodigoIn(1L)).thenReturn(usuario);
        when(grupoRepository.findByCodigoIn(2L)).thenReturn(novoGrupo(2L));
        when(grupoRepository.findByUsuarioIn(usuario)).thenReturn(gruposDoUsuario);

        String resultado = usuarioService.removeGrupo(1L, 2L);

        assertEquals("ok", resultado);

        verify(grupoRepository).findByUsuarioIn(usuario);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());

        assertEquals(Arrays.asList(grupo1), captor.getValue().getGrupoUsuario());
    }
}
