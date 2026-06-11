package com.example.dam_af;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

public class BancoHelper extends SQLiteOpenHelper{
    private static final String BANCO =
            "locais.db";

    private static final int VERSAO = 1;

    public static final String TABELA =
            "locais_salvos";

    public BancoHelper(Context context) {

        super(
                context,
                BANCO,
                null,
                VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE " +
                        TABELA +
                        "(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "nome TEXT," +
                        "categoria TEXT," +
                        "observacao TEXT," +
                        "latitude REAL," +
                        "longitude REAL" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABELA);

        onCreate(db);
    }

    public long salvarLocal(
            String nome,
            String categoria,
            String observacao,
            double latitude,
            double longitude) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("nome", nome);
        values.put("categoria", categoria);
        values.put("observacao", observacao);
        values.put("latitude", latitude);
        values.put("longitude", longitude);

        return db.insert(
                TABELA,
                null,
                values);
    }

    public List<LocalPlace> listarLocais() {

        List<LocalPlace> lista =
                new ArrayList<>();

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM " + TABELA,
                        null);

        while(cursor.moveToNext()) {

            lista.add(
                    new LocalPlace(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getDouble(4),
                            cursor.getDouble(5)
                    )
            );
        }

        cursor.close();

        return lista;
    }

    public int atualizarLocal(
            int id,
            String categoria,
            String observacao) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("categoria", categoria);
        values.put("observacao", observacao);

        return db.update(
                TABELA,
                values,
                "id=?",
                new String[]{
                        String.valueOf(id)
                });
    }

    public int excluirLocal(int id) {

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABELA,
                "id=?",
                new String[]{
                        String.valueOf(id)
                });
    }

}
