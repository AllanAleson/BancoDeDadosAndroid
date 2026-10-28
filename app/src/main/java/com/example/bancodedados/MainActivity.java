package com.example.bancodedados;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BancoHelper banco;
    private TextInputEditText textNome, textId;
    private TextView resultado;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        banco = new BancoHelper(this);
        textNome = findViewById(R.id.textNome);
        textId = findViewById(R.id.textId);
        resultado = findViewById(R.id.textResultado);
        if (state != null) resultado.setText(state.getCharSequence("resultado", getText(R.string.resultado_inicial)));
        findViewById(R.id.btnGravar).setOnClickListener(v -> executar(this::gravar));
        findViewById(R.id.btnConsultar).setOnClickListener(v -> executar(this::consultar));
        findViewById(R.id.btnAtualizar).setOnClickListener(v -> executar(this::atualizar));
        findViewById(R.id.btnDeletar).setOnClickListener(v -> executar(this::confirmarExclusao));
    }
    private void executar(Runnable acao) {
        try { acao.run(); }
        catch (SQLiteException e) { resultado.setText(R.string.erro_banco); }
    }
    private String nomeValido() {
        String nome = textNome.getText().toString().trim();
        if (nome.isEmpty() || nome.length() > 100) {
            textNome.setError(getString(R.string.nome_obrigatorio));
            textNome.requestFocus();
            return null;
        }
        textNome.setError(null);
        return nome;
    }
    private Long codigoValido() {
        try {
            long id = Long.parseLong(textId.getText().toString().trim());
            if (id > 0) { textId.setError(null); return id; }
        } catch (NumberFormatException ignored) { }
        textId.setError(getString(R.string.codigo_invalido));
        textId.requestFocus();
        return null;
    }
    private void gravar() {
        String nome = nomeValido();
        if (nome == null) return;
        long id = banco.cadastrar(nome);
        textNome.setText("");
        textId.setText("");
        textId.setError(null);
        mostrarTodos(getString(R.string.gravado, id));
    }
    private void consultar() {
        if (textId.getText().toString().trim().isEmpty()) {
            textId.setError(null);
            mostrarTodos(null);
            return;
        }
        Long id = codigoValido();
        if (id == null) return;
        List<BancoHelper.Usuario> lista = banco.consultar(id);
        if (lista.isEmpty()) { resultado.setText(R.string.nao_encontrado); return; }
        BancoHelper.Usuario usuario = lista.get(0);
        textNome.setText(usuario.nome);
        textNome.setError(null);
        resultado.setText(getString(R.string.linha_usuario, usuario.id, usuario.nome));
    }
    private void atualizar() {
        Long id = codigoValido();
        if (id == null) return;
        String nome = nomeValido();
        if (nome == null) return;
        if (banco.atualizar(id, nome) == 0) resultado.setText(R.string.nao_encontrado);
        else mostrarTodos(getString(R.string.atualizado, id));
    }
    private void confirmarExclusao() {
        Long id = codigoValido();
        if (id == null) return;
        List<BancoHelper.Usuario> lista = banco.consultar(id);
        if (lista.isEmpty()) { resultado.setText(R.string.nao_encontrado); return; }
        new MaterialAlertDialogBuilder(this).setTitle(R.string.deletar)
                .setMessage(getString(R.string.confirmar_exclusao, lista.get(0).nome, id))
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.deletar, (dialog, which) -> executar(() -> {
                    if (banco.deletar(id) == 0) resultado.setText(R.string.nao_encontrado);
                    else {
                        textId.setText(""); textNome.setText("");
                        mostrarTodos(getString(R.string.deletado, id));
                    }
                })).show();
    }
    private void mostrarTodos(String mensagem) {
        List<BancoHelper.Usuario> lista = banco.consultar(null);
        StringBuilder texto = new StringBuilder();
        if (mensagem != null) texto.append(mensagem).append("\n\n");
        if (lista.isEmpty()) texto.append(getString(R.string.banco_vazio));
        else {
            texto.append(getString(R.string.total_usuarios, lista.size()));
            for (BancoHelper.Usuario usuario : lista)
                texto.append("\n\n").append(getString(R.string.linha_usuario, usuario.id, usuario.nome));
        }
        resultado.setText(texto);
    }
    @Override protected void onSaveInstanceState(Bundle state) {
        state.putCharSequence("resultado", resultado.getText());
        super.onSaveInstanceState(state);
    }
    @Override protected void onDestroy() { banco.close(); super.onDestroy(); }
}
