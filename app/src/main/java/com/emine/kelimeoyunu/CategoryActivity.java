package com.emine.kelimeoyunu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    Button btnGenel, btnBilim, btnSpor;
    TextView tvLiderlikTablosu; // Yeni eklenen alan
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        btnGenel = findViewById(R.id.btnGenel);
        btnBilim = findViewById(R.id.btnBilim);
        btnSpor = findViewById(R.id.btnSpor);
        tvLiderlikTablosu = findViewById(R.id.tvLiderlikTablosu);

        dbHelper = new DatabaseHelper(this);

        btnGenel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { oyunaBasla("Genel"); }
        });

        btnBilim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { oyunaBasla("Bilim"); }
        });

        btnSpor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { oyunaBasla("Spor"); }
        });

        // Skor tablosunu yükle
        skorlariGoster();
    }

    // Her ana menüye dönüldüğünde en yüksek skorların güncellenmesi için buraya da ekliyoruz
    @Override
    protected void onResume() {
        super.onResume();
        skorlariGoster();
    }

    private void skorlariGoster() {
        List<Integer> enYuksekSkorlar = dbHelper.getHighScores();

        if (enYuksekSkorlar.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < enYuksekSkorlar.size(); i++) {
                sb.append(AlternativeRankIcon(i)).append(" ").append(i + 1).append(". Skor: ")
                        .append(enYuksekSkorlar.get(i)).append(" Puan\n");
            }
            tvLiderlikTablosu.setText(sb.toString().trim());
        } else {
            tvLiderlikTablosu.setText("Henüz kayıtlı skor yok. İlk oynayan sen ol!");
        }
    }

    private String AlternativeRankIcon(int index) {
        if (index == 0) return "🥇";
        if (index == 1) return "🥈";
        if (index == 2) return "🥉";
        return "✨";
    }

    private void oyunaBasla(String kategoriAdi) {
        Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
        intent.putExtra("secilenKategori", kategoriAdi);
        startActivity(intent);
    }
}