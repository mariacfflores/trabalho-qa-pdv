
package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.GrupoUsuario;
import net.originmobi.pdv.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarios;

    @Mock
    private GrupoUsuarioService grupos;

    @InjectMocks
    private UsuarioService usuarioService;

    // Cadastrar um novo usuario com sucesso

    @Test
    void deveCadastrarUsuarioComSucesso() {

        Usuario usuario = mock(Usuario.class);
        Pessoa pessoa = mock(Pessoa.class);

        when(usuario.getCodigo()).thenReturn(null);
        when(usuario.getSenha()).thenReturn("123456");
        when(usuario.getUser()).thenReturn("Maria");
        when(usuario.getPessoa()).thenReturn(pessoa);
        when(pessoa.getCodigo()).thenReturn(1L);

        String resultado = usuarioService.cadastrar(usuario);

        assertEquals("Usuário salvo com sucesso", resultado);

        verify(usuarios).save(usuario);
    }

    // Atualizar um usuario existente com sucesso

    @Test
    void deveAtualizarUsuarioComSucesso() {

        Usuario usuario = mock(Usuario.class);

        when(usuario.getCodigo()).thenReturn(1L);
        when(usuario.getSenha()).thenReturn("654321");

        String resultado = usuarioService.cadastrar(usuario);

        assertEquals("Usuário atualizado com sucesso", resultado);

        verify(usuarios).save(usuario);
    }

    // Listar usuarios cadastrados

    @Test
    void deveListarUsuariosComSucesso() {

        Usuario usuario1 = mock(Usuario.class);
        Usuario usuario2 = mock(Usuario.class);

        List<Usuario> lista = Arrays.asList(usuario1, usuario2);

        when(usuarios.findAll()).thenReturn(lista);

        List<Usuario> resultado = usuarioService.lista();

        assertEquals(2, resultado.size());
        assertEquals(lista, resultado);
    }

    // Adicionar grupo ao usuario com sucesso

    @Test
    void deveAdicionarGrupoAoUsuarioComSucesso() {

        Usuario usuario = mock(Usuario.class);
        GrupoUsuario grupo = mock(GrupoUsuario.class);

        List<GrupoUsuario> listaGrupos = new ArrayList<>();

        when(usuarios.findByCodigoIn(1L)).thenReturn(usuario);
        when(grupos.buscaGrupo(2L)).thenReturn(grupo);
        when(usuario.getGrupoUsuario()).thenReturn(listaGrupos);

        String resultado = usuarioService.addGrupo(1L, 2L);

        assertEquals("ok", resultado);

        assertTrue(listaGrupos.contains(grupo));

        verify(usuarios).save(usuario);
    }

    // Buscar usuario existente pelo username

    @Test
    void deveBuscarUsuarioComSucesso() {

        Usuario usuario = mock(Usuario.class);

        when(usuarios.findByUserEquals("maria"))
                .thenReturn(usuario);

        Usuario resultado = usuarioService.buscaUsuario("maria");

        assertSame(usuario, resultado);
    }
}