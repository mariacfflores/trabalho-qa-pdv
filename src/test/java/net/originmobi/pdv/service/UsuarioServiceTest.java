
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

    // ===== Entrega 2 =====

    // Cria um usuario real (nao mockado) para conseguir conferir
    // os valores que o service grava nele, como senha e data

    private Usuario novoUsuario(Long codigo, String user, String senha, Long codigoPessoa) {

        Pessoa pessoa = new Pessoa();
        pessoa.setCodigo(codigoPessoa);

        Usuario usuario = new Usuario();
        usuario.setCodigo(codigo);
        usuario.setUser(user);
        usuario.setSenha(senha);
        usuario.setPessoa(pessoa);

        return usuario;
    }

    private GrupoUsuario novoGrupo(Long codigo) {

        GrupoUsuario grupo = new GrupoUsuario();
        grupo.setCodigo(codigo);

        return grupo;
    }

    // CT06 - Nao cadastrar quando o username ja existe

    @Test
    void naoDeveCadastrarUsuarioComUsernameDuplicado() {

        Usuario usuario = novoUsuario(null, "maria", "123456", 1L);

        when(usuarios.findByUserEquals("maria")).thenReturn(new Usuario());

        String resultado = usuarioService.cadastrar(usuario);

        assertEquals("Usuário já existe", resultado);

        verify(usuarios, never()).save(any());
    }

    // CT07 - Nao cadastrar quando a pessoa ja esta vinculada a outro usuario

    @Test
    void naoDeveCadastrarUsuarioComPessoaJaVinculada() {

        Usuario usuario = novoUsuario(null, "maria", "123456", 1L);

        when(usuarios.findByUserEquals("maria")).thenReturn(null);
        when(usuarios.findByPessoaCodigoEquals(1L)).thenReturn(new Usuario());

        String resultado = usuarioService.cadastrar(usuario);

        assertEquals("Pessoa já vinculada a outro usuário", resultado);

        verify(usuarios, never()).save(any());
    }

    // CT08 - Username duplicado tem prioridade sobre pessoa ja vinculada

    @Test
    void deveRetornarUsuarioExistenteQuandoUsernameEPessoaJaExistem() {

        Usuario usuario = novoUsuario(null, "maria", "123456", 1L);

        when(usuarios.findByUserEquals("maria")).thenReturn(new Usuario());
        when(usuarios.findByPessoaCodigoEquals(1L)).thenReturn(new Usuario());

        String resultado = usuarioService.cadastrar(usuario);

        assertEquals("Usuário já existe", resultado);

        verify(usuarios, never()).save(any());
    }

    // CT09 - A senha deve ser gravada criptografada (BCrypt) e a data de cadastro preenchida

    @Test
    void deveCriptografarSenhaEPreencherDataNoCadastro() {

        Usuario usuario = novoUsuario(null, "maria", "123456", 1L);

        usuarioService.cadastrar(usuario);

        assertNotEquals("123456", usuario.getSenha());
        assertTrue(new BCryptPasswordEncoder().matches("123456", usuario.getSenha()));
        assertNotNull(usuario.getData_cadastro());
    }

    // CT10 - Na atualizacao, nao deve consultar duplicidade de username nem de pessoa

    @Test
    void naoDeveVerificarDuplicidadeNaAtualizacao() {

        Usuario usuario = novoUsuario(1L, "maria", "654321", 1L);

        usuarioService.cadastrar(usuario);

        verify(usuarios, never()).findByUserEquals(any());
        verify(usuarios, never()).findByPessoaCodigoEquals(any());
        verify(usuarios).save(usuario);
    }

    // CT11 - Erro ao salvar na atualizacao deve retornar a mensagem da excecao

    @Test
    void deveRetornarMensagemDoErroQuandoAtualizacaoFalhar() {

        Usuario usuario = novoUsuario(1L, "maria", "654321", 1L);

        when(usuarios.save(usuario)).thenThrow(new RuntimeException("Erro no banco"));

        String resultado = usuarioService.cadastrar(usuario);

        assertEquals("Erro no banco", resultado);
    }

    // CT12 - Lista vazia quando nao ha usuarios cadastrados

    @Test
    void deveRetornarListaVaziaQuandoNaoHaUsuarios() {

        when(usuarios.findAll()).thenReturn(new ArrayList<>());

        List<Usuario> resultado = usuarioService.lista();

        assertTrue(resultado.isEmpty());
    }

    // CT13 - Nao adicionar grupo que o usuario ja possui

    @Test
    void naoDeveAdicionarGrupoJaExistente() {

        Usuario usuario = new Usuario();
        GrupoUsuario grupo = novoGrupo(2L);

        List<GrupoUsuario> listaGrupos = new ArrayList<>();
        listaGrupos.add(grupo);
        usuario.setGrupoUsuario(listaGrupos);

        when(usuarios.findByCodigoIn(1L)).thenReturn(usuario);
        when(grupos.buscaGrupo(2L)).thenReturn(grupo);

        String resultado = usuarioService.addGrupo(1L, 2L);

        assertEquals("ja existe", resultado);
        assertEquals(1, listaGrupos.size());

        verify(usuarios, never()).save(any());
    }

    // CT14 - Remover grupo vinculado ao usuario

    @Test
    void deveRemoverGrupoDoUsuario() {

        Usuario usuario = new Usuario();
        GrupoUsuario grupo1 = novoGrupo(1L);
        GrupoUsuario grupo2 = novoGrupo(2L);

        List<GrupoUsuario> todosGrupos = new ArrayList<>(Arrays.asList(grupo1, grupo2));

        when(usuarios.findByCodigoIn(1L)).thenReturn(usuario);
        when(grupos.buscaGrupo(2L)).thenReturn(novoGrupo(2L));
        when(grupos.buscaGrupos(usuario)).thenReturn(todosGrupos);

        String resultado = usuarioService.removeGrupo(1L, 2L);

        assertEquals("ok", resultado);
        assertEquals(Arrays.asList(grupo1), usuario.getGrupoUsuario());

        verify(usuarios).save(usuario);
    }

    // CT15 - Remover grupo que o usuario nao possui mantem a lista igual

    @Test
    void deveManterGruposQuandoGrupoRemovidoNaoEstaVinculado() {

        Usuario usuario = new Usuario();
        GrupoUsuario grupo1 = novoGrupo(1L);

        List<GrupoUsuario> todosGrupos = new ArrayList<>(Arrays.asList(grupo1));

        when(usuarios.findByCodigoIn(1L)).thenReturn(usuario);
        when(grupos.buscaGrupo(3L)).thenReturn(novoGrupo(3L));
        when(grupos.buscaGrupos(usuario)).thenReturn(todosGrupos);

        String resultado = usuarioService.removeGrupo(1L, 3L);

        assertEquals("ok", resultado);
        assertEquals(Arrays.asList(grupo1), usuario.getGrupoUsuario());

        verify(usuarios).save(usuario);
    }

    // CT16 - Erro ao salvar na remocao de grupo e ignorado e retorna "ok"

    @Test
    void deveRetornarOkMesmoQuandoSalvarFalhaNaRemocaoDeGrupo() {

        Usuario usuario = new Usuario();

        when(usuarios.findByCodigoIn(1L)).thenReturn(usuario);
        when(grupos.buscaGrupo(2L)).thenReturn(novoGrupo(2L));
        when(grupos.buscaGrupos(usuario)).thenReturn(new ArrayList<>(Arrays.asList(novoGrupo(2L))));
        when(usuarios.save(usuario)).thenThrow(new RuntimeException("Erro no banco"));

        String resultado = usuarioService.removeGrupo(1L, 2L);

        assertEquals("ok", resultado);
    }

    // CT17 - Remover grupo com codigo maior que 127
    // O Java so reaproveita objetos Long entre -128 e 127. Acima disso,
    // comparar com == olha a referencia e nao o valor, entao o grupo nao e removido.

    @Test
    void deveRemoverGrupoComCodigoMaiorQue127() {

        Usuario usuario = new Usuario();
        GrupoUsuario grupo = novoGrupo(Long.valueOf(1000L));

        List<GrupoUsuario> todosGrupos = new ArrayList<>(Arrays.asList(grupo));

        when(usuarios.findByCodigoIn(1L)).thenReturn(usuario);
        when(grupos.buscaGrupo(1000L)).thenReturn(novoGrupo(Long.valueOf(1000L)));
        when(grupos.buscaGrupos(usuario)).thenReturn(todosGrupos);

        usuarioService.removeGrupo(1L, 1000L);

        assertTrue(usuario.getGrupoUsuario().isEmpty());
    }

    // CT18 - Buscar usuario inexistente retorna null

    @Test
    void deveRetornarNullQuandoUsuarioNaoExiste() {

        when(usuarios.findByUserEquals("inexistente")).thenReturn(null);

        Usuario resultado = usuarioService.buscaUsuario("inexistente");

        assertNull(resultado);
    }
}