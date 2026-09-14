package com.emine.kelimeoyunu;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView tvSoru, tvKelimeKutusu, tvSkor, tvCan, tvSure;
    EditText etTahmin;
    Button btnKontrol, btnHarfAl,btnAnaMenuyeDon, btnPas;

    DatabaseHelper dbHelper;
    List<Question> soruListesi;

    int mevcutSoruIndex = 0;
    String dogruCevap = "";
    char[] ekrandaGorunenKelime;

    int skor = 0;
    int pasHakki = 2;
    int can = 3;
    int soruBasinaPuan = 100;

    CountDownTimer geriSayimSayaci;
    long kalanSureMs = 30000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPas = findViewById(R.id.btnPas);

        btnPas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pasHakki > 0) {
                    pasHakki--;
                    btnPas.setText("PAS GEÇ (Kalan: " + pasHakki + ")");

                    if (geriSayimSayaci != null) geriSayimSayaci.cancel(); // Süreyi durdur

                    Toast.makeText(MainActivity.this, "Soru Atlandı!", Toast.LENGTH_SHORT).show();
                    mevcutSoruIndex++;
                    soruyuGoster(); // Sonraki soruya geç
                }

                if (pasHakki == 0) {
                    btnPas.setEnabled(false); // Hak bittiyse butonu söndür
                }
            }
        });

        btnAnaMenuyeDon = findViewById(R.id.btnAnaMenuyeDon);

        btnAnaMenuyeDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Önemli: Çalışan geri sayım sayacı varsa durdur ki arkada akmaya devam etmesin
                if (geriSayimSayaci != null) {
                    geriSayimSayaci.cancel();
                }

                // 2. Mevcut oyun ekranını kapat (Böylece arkadaki Kategori ekranı görünür olur)
                finish();
            }
        });

        tvSoru = findViewById(R.id.tvSoru);
        tvKelimeKutusu = findViewById(R.id.tvKelimeKutusu);
        etTahmin = findViewById(R.id.etTahmin);
        btnKontrol = findViewById(R.id.btnKontrol);
        btnHarfAl = findViewById(R.id.btnHarfAl);
        tvSkor = findViewById(R.id.tvSkor);
        tvCan = findViewById(R.id.tvCan);
        tvSure = findViewById(R.id.tvSure);

        // Kategori ekranından gönderilen kategori adını yakalıyoruz
        String secilenKategori = getIntent().getStringExtra("secilenKategori");

        dbHelper = new DatabaseHelper(this);
        // Sadece seçilen kategoriye ait soruları veri tabanından getiriyoruz
        soruListesi = dbHelper.getQuestionsByCategory(secilenKategori);

        tvSkor.setText("SKOR: " + skor);
        tvCan.setText("CAN: ❤️ ❤️ ❤️");
        soruyuGoster();

        btnKontrol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String kullaniciTahmini = etTahmin.getText().toString().trim().toUpperCase();

                if (kullaniciTahmini.equals(dogruCevap)) {
                    if (geriSayimSayaci != null) geriSayimSayaci.cancel();

                    Toast.makeText(MainActivity.this, "Harika! + " + soruBasinaPuan + " Puan", Toast.LENGTH_SHORT).show();
                    skor += soruBasinaPuan;
                    tvSkor.setText("SKOR: " + skor);

                    mevcutSoruIndex++;
                    soruyuGoster();
                } else {
                    Toast.makeText(MainActivity.this, "Yanlış Cevap!", Toast.LENGTH_SHORT).show();
                    canAzalt();
                }
                etTahmin.setText("");
            }
        });

        btnHarfAl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                harfVer();
            }
        });
    }

    private void soruyuGoster() {
        if (mevcutSoruIndex < soruListesi.size()) {
            Question mevcutSoru = soruListesi.get(mevcutSoruIndex);
            tvSoru.setText(mevcutSoru.getQuestion());
            dogruCevap = mevcutSoru.getAnswer();
            soruBasinaPuan = 100;

            ekrandaGorunenKelime = new char[dogruCevap.length()];
            for (int i = 0; i < dogruCevap.length(); i++) {
                ekrandaGorunenKelime[i] = '_';
            }

            ekraniGuncelle();
            btnHarfAl.setEnabled(true);
            sayaciBaslat();
        } else {
            if (geriSayimSayaci != null) geriSayimSayaci.cancel();
            tvSoru.setText("Tebrikler! Tüm soruları bitirdiniz. Şampiyon!");
            tvKelimeKutusu.setText("🏆🏆🏆");
            tvSure.setText("⏱ --");
            oyunuKilitler();
        }
    }

    private void sayaciBaslat() {
        if (geriSayimSayaci != null) {
            geriSayimSayaci.cancel();
        }

        geriSayimSayaci = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                kalanSureMs = millisUntilFinished;
                tvSure.setText("⏱ " + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                tvSure.setText("⏱ 0");
                Toast.makeText(MainActivity.this, "Süre Bitti!", Toast.LENGTH_SHORT).show();
                canAzalt();

                if (can > 0) {
                    mevcutSoruIndex++;
                    soruyuGoster();
                }
            }
        }.start();
    }

    private void canAzalt() {
        can--;
        if (can == 2) tvCan.setText("CAN: ❤️ ❤️");
        else if (can == 1) tvCan.setText("CAN: ❤️");
        else {
            tvCan.setText("CAN: ☠️ GAME OVER");
            tvSoru.setText("Oyun Bitti! Toplam Skorun: " + skor);
            if (geriSayimSayaci != null) geriSayimSayaci.cancel();
            oyunuKilitler();
        }
    }

    private void harfVer() {
        List<Integer> kapaliIndexler = new ArrayList<>();
        for (int i = 0; i < ekrandaGorunenKelime.length; i++) {
            if (ekrandaGorunenKelime[i] == '_') {
                kapaliIndexler.add(i);
            }
        }

        if (kapaliIndexler.size() > 0) {
            Random random = new Random();
            int rastgeleIndex = kapaliIndexler.get(random.nextInt(kapaliIndexler.size()));

            ekrandaGorunenKelime[rastgeleIndex] = dogruCevap.charAt(rastgeleIndex);
            soruBasinaPuan -= 20;
            if (soruBasinaPuan < 0) soruBasinaPuan = 0;

            ekraniGuncelle();

            if (kapaliIndexler.size() == 1) {
                btnHarfAl.setEnabled(false);
            }
        }
    }

    private void ekraniGuncelle() {
        StringBuilder formatliKelime = new StringBuilder();
        for (char harf : ekrandaGorunenKelime) {
            formatliKelime.append(harf).append(" ");
        }
        tvKelimeKutusu.setText(formatliKelime.toString().trim());
    }

    private void oyunuKilitler() {
        btnKontrol.setEnabled(false);
        btnHarfAl.setEnabled(false);
        btnPas.setEnabled(false); // Pas butonunu da kilitle
        etTahmin.setEnabled(false);

        // OYUN BİTTİĞİ AN SKORU VERİ TABANINA KAYDEDİYORUZ
        dbHelper.skorKaydet(skor);
        Toast.makeText(this, "Skorunuz Liderlik Tablosuna Kaydedildi!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (geriSayimSayaci != null) {
            geriSayimSayaci.cancel();
        }
    }
}