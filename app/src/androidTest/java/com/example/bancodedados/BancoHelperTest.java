package com.example.bancodedados;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class BancoHelperTest {
    private Context context;
    private BancoHelper banco;
    private static final String ARQUIVO = "crud-teste.db";
    @Before public void preparar() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.deleteDatabase(ARQUIVO);
        banco = new BancoHelper(context, ARQUIVO);
    }
    @After public void limpar() {
        banco.close();
        context.deleteDatabase(ARQUIVO);
    }
    @Test public void cicloCrudPreservaUsuariosComMesmoNome() {
        long primeiro = banco.cadastrar("  Ana D'Ávila  ");
        long segundo = banco.cadastrar("Ana D'Ávila");
        assertNotEquals(primeiro, segundo);
        assertEquals(2, banco.consultar(null).size());
        assertEquals("Ana D'Ávila", banco.consultar(primeiro).get(0).nome);
        assertEquals(1, banco.atualizar(primeiro, "Ana Maria"));
        assertEquals("Ana Maria", banco.consultar(primeiro).get(0).nome);
        assertEquals("Ana D'Ávila", banco.consultar(segundo).get(0).nome);
        banco.close();
        banco = new BancoHelper(context, ARQUIVO);
        assertEquals("Ana Maria", banco.consultar(primeiro).get(0).nome);
        assertEquals(1, banco.deletar(primeiro));
        assertTrue(banco.consultar(primeiro).isEmpty());
        assertEquals(0, banco.atualizar(primeiro, "Ausente"));
        assertEquals(0, banco.deletar(primeiro));
        assertEquals(1, banco.consultar(null).size());
    }
    @Test public void rejeitaNomeVazio() {
        try {
            banco.cadastrar("   ");
            fail("Nome vazio deve ser rejeitado");
        } catch (IllegalArgumentException esperado) {
            assertTrue(banco.consultar(null).isEmpty());
        }
    }
}
