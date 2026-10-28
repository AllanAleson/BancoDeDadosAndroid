package com.example.bancodedados;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class BancoHelper extends SQLiteOpenHelper {
    public BancoHelper(Context context) { this(context, "usuarios.db"); }
    BancoHelper(Context context, String arquivo) { super(context, arquivo, null, 1); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT NOT NULL CHECK(length(trim(nome)) BETWEEN 1 AND 100))");
    }
    @Override public void onUpgrade(SQLiteDatabase db, int antiga, int nova) {
        // Futuras mudanças de estrutura devem ser feitas com migrações.
    }
    private ContentValues valores(String nome) {
        String valor = nome == null ? "" : nome.trim();
        if (valor.isEmpty() || valor.length() > 100) throw new IllegalArgumentException("Nome inválido");
        ContentValues dados = new ContentValues();
        dados.put("nome", valor);
        return dados;
    }
    public long cadastrar(String nome) {
        return getWritableDatabase().insertOrThrow("usuarios", null, valores(nome));
    }
    public List<Usuario> consultar(Long id) {
        List<Usuario> lista = new ArrayList<>();
        // Parâmetros separados do SQL protegem a consulta de entradas indevidas.
        try (Cursor c = getReadableDatabase().query("usuarios", new String[]{"id", "nome"},
                id == null ? null : "id = ?", id == null ? null : new String[]{id.toString()}, null, null, "id ASC")) {
            while (c.moveToNext()) lista.add(new Usuario(c.getLong(0), c.getString(1)));
        }
        return lista;
    }
    public int atualizar(long id, String nome) {
        return getWritableDatabase().update("usuarios", valores(nome), "id = ?", new String[]{String.valueOf(id)});
    }
    public int deletar(long id) {
        return getWritableDatabase().delete("usuarios", "id = ?", new String[]{String.valueOf(id)});
    }
    public static class Usuario {
        public final long id;
        public final String nome;
        Usuario(long id, String nome) { this.id = id; this.nome = nome; }
    }
}
