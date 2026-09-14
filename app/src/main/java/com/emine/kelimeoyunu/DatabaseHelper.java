package com.emine.kelimeoyunu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "KelimeOyunuFinal.db"; // Yeni sürüm ismi
    private static final int DATABASE_VERSION = 1;

    // Sorular Tablosu
    public static final String TABLE_QUESTIONS = "sorular";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_QUESTION = "soru";
    public static final String COLUMN_ANSWER = "cevap";
    public static final String COLUMN_CATEGORY = "kategori";

    // Skorlar Tablosu (YENİ)
    public static final String TABLE_SCORES = "skorlar";
    public static final String COLUMN_SCORE_ID = "score_id";
    public static final String COLUMN_SCORE_VALUE = "skor_degeri";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Soru Tablosu Oluşturma
        String CREATE_QUESTIONS_TABLE = "CREATE TABLE " + TABLE_QUESTIONS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_QUESTION + " TEXT,"
                + COLUMN_ANSWER + " TEXT,"
                + COLUMN_CATEGORY + " TEXT" + ")";
        db.execSQL(CREATE_QUESTIONS_TABLE);

        // Skor Tablosu Oluşturma (YENİ)
        String CREATE_SCORES_TABLE = "CREATE TABLE " + TABLE_SCORES + "("
                + COLUMN_SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SCORE_VALUE + " INTEGER" + ")";
        db.execSQL(CREATE_SCORES_TABLE);

        sorulariYukle(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        onCreate(db);
    }

    private void sorulariYukle(SQLiteDatabase db) {
        insertQuestion(db, "Türkiye'nin başkenti neresidir?", "ANKARA", "Genel");
        insertQuestion(db, "Gece gökyüzünde gördüğümüz dünyanın tek uydusu?", "AY", "Genel");
        insertQuestion(db, "İstiklal Marşı'mızın şairi kimdir?", "MEHMETAKİF", "Genel");
        insertQuestion(db, "Pusulada 'N' harfi hangi yönü gösterir?", "KUZEY", "Genel");
        insertQuestion(db, "Cumhuriyetimiz hangi yılda ilan edilmiştir?", "BİNDOKUZYÜZYİRMİÜÇ", "Genel");
        insertQuestion(db, "Dünyanın en uzun nehri hangisidir?", "NİL", "Genel");
        insertQuestion(db, "Fatih Sultan Mehmet'in fethettiği şehir?", "İSTANBUL", "Genel");
        insertQuestion(db, "Üç tarafı denizlerle çevrili kara parçası?", "YARIMADA", "Genel");
        insertQuestion(db, "Mona Lisa tablosunu yapan ünlü ressam?", "DAVINCI", "Genel");
        insertQuestion(db, "Yazları sıcak, kışları ılık ve yağışlı iklim?", "AKDENİZ", "Genel");

        insertQuestion(db, "Bilgisayarda verilerin kalıcı saklandığı yer?", "HARDDİSK", "Bilim");
        insertQuestion(db, "Isı ve ışık kaynağımız olan dev yıldız?", "GÜNEŞ", "Bilim");
        insertQuestion(db, "Suyun kimyasal formülü nedir?", "HİKİO", "Bilim");
        insertQuestion(db, "Kendi kendine beslenebilen canlılar?", "OTOTROF", "Bilim");
        insertQuestion(db, "Akıllı telefonlarda kullanılan açık kaynaklı sistem?", "ANDROID", "Bilim");
        insertQuestion(db, "Yer çekimi kanununu bulan bilim insanı?", "NEWTON", "Bilim");
        insertQuestion(db, "İnternet sitelerinin başındaki protokol?", "HTTP", "Bilim");
        insertQuestion(db, "Bilgisayarın beyni olan merkezi işlem birimi?", "CPU", "Bilim");
        insertQuestion(db, "Kırmızı gezegen olarak da bilinen yer?", "MARS", "Bilim");
        insertQuestion(db, "Suyun katı haline ne ad verilir?", "BUZ", "Bilim");

        insertQuestion(db, "Futbolda bir takım sahaya kaç oyuncu ile çıkar?", "ONBİR", "Spor");
        insertQuestion(db, "Dünya Kupasını en çok kazanan ülke?", "BREZİLYA", "Spor");
        insertQuestion(db, "Basketbolda her takım sahada kaç oyuncu olur?", "BEŞ", "Spor");
        insertQuestion(db, "Olimpiyat Oyunları kaç yılda bir düzenlenir?", "DÖRT", "Spor");
        insertQuestion(db, "Tenis maçlarının oynandığı alan?", "KORT", "Spor");
        insertQuestion(db, "Milli okçumuz, Olimpiyat şampiyonumuz?", "METEGAZOZ", "Spor");
        insertQuestion(db, "Futbolda kalecinin topa elle dokunabildiği alan?", "CEZASAHASI", "Spor");
        insertQuestion(db, "Formül 1 yarışlarının sürüldüğü özel yol?", "PİST", "Spor");
        insertQuestion(db, "Güreşte sporcuların kapıştığı minder alan?", "MAT", "Spor");
        insertQuestion(db, "Yağlı güreş festivalinin adı?", "KIRKPINAR", "Spor");
    }

    private void insertQuestion(SQLiteDatabase db, String soru, String cevap, String kategori) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUESTION, soru);
        values.put(COLUMN_ANSWER, cevap.toUpperCase().trim());
        values.put(COLUMN_CATEGORY, kategori);
        db.insert(TABLE_QUESTIONS, null, values);
    }

    public List<Question> getQuestionsByCategory(String kategori) {
        List<Question> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_QUESTIONS + " WHERE " + COLUMN_CATEGORY + " = ?", new String[]{kategori});
        if (cursor.moveToFirst()) {
            do {
                Question q = new Question(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
                list.add(q);
            } while (cursor.moveToNext());
        }
        cursor.close();
        Collections.shuffle(list);
        return list;
    }

    // SKOR KAYDETME FONKSİYONU (YENİ)
    public void skorKaydet(int skor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCORE_VALUE, skor);
        db.insert(TABLE_SCORES, null, values);
    }

    // EN YÜKSEK 5 SKORU GETİREN FONKSİYONU (YENİ)
    public List<Integer> getHighScores() {
        List<Integer> scores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Büyükten küçüğe sıralayıp ilk 5 skoru çekiyoruz
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCORES + " ORDER BY " + COLUMN_SCORE_VALUE + " DESC LIMIT 5", null);
        if (cursor.moveToFirst()) {
            do {
                scores.add(cursor.getInt(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return scores;
    }
}