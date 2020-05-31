package com.myproject.mytranslator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.myproject.mytranslator.DTO.LanguageInfo;
import com.myproject.mytranslator.R;

public class ViewWordDetailsActivity extends AppCompatActivity {

    private TextView textKorean;
    private TextView textEnglish;
    private LanguageInfo languageInfo;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_word_details);

        setComp();
        getData();
        setData();
    }


    public void setComp() {
        textEnglish = findViewById(R.id.detailTextViewEnglish);
        textKorean = findViewById(R.id.detailTextViewKorean);
    }

    public void getData() {
        languageInfo = (LanguageInfo) (getIntent().getParcelableExtra("CorrespondingWord"));
        position = getIntent().getIntExtra("WordPosition", 0);
    }

    public void setData() {
        if (this.languageInfo != null) {

            textEnglish.setText(languageInfo.getTextEnglish());
            textKorean.setText(String.valueOf(languageInfo.getTextKorean()));
        }
    }

    public void onClickDelete(View view) {

        Intent intent = new Intent();
        intent.putExtra("Position", position);
        setResult(RESULT_OK, intent);
        finish();

    }
}
