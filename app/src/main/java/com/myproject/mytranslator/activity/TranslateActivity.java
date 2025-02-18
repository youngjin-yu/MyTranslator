package com.myproject.mytranslator.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.myproject.mytranslator.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TranslateActivity extends AppCompatActivity {

    private Button btTranslate;
    private EditText editTextSource;
    private TextView tv_result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        editTextSource = (EditText) findViewById(R.id.editTextSource);
        tv_result = (TextView) findViewById(R.id.tv_result);
        btTranslate = (Button) findViewById(R.id.buttonTranslate);



        //번역 실행버튼 클릭이벤트
        btTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

        //소스에 입력된 내용이 있는지 체크하고 넘어가자.
                if (editTextSource.getText().toString().length() == 0) {
                    Toast.makeText(TranslateActivity.this, "번역할 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                    editTextSource.requestFocus();
                    return;
                }

        //실행버튼을 클릭하면 AsyncTask를 이용 요청하고 결과를 반환받아서 화면에 표시하도록 해보자.
                NaverTranslateTask asyncTask = new NaverTranslateTask();
                String sText = editTextSource.getText().toString();
                asyncTask.execute(sText);

            }
        });


        //음성인식 실행버튼 이벤트

    }

    public void onClick(View view) {
    }


    //ASYNCTASK
    public class NaverTranslateTask extends AsyncTask<String, Void, String> {

        //Naver
        String clientId = "c5k2hLFXIlr82AMrwpZ9";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "XUigHX6yby";//애플리케이션 클라이언트 시크릿값";
        //언어선택도 나중에 사용자가 선택할 수 있게 옵션 처리해 주면 된다.

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //AsyncTask 메인처리
        @Override
        protected String doInBackground(String... strings) {
            //네이버 파파고 예제

            String sourceText = strings[0];

            //2020.04.26+(s)
            try {
                String text = URLEncoder.encode(sourceText, "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // post request
                String postParams = "source=en&target=ko&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                return response.toString();

            } catch (Exception e) {
                Log.d("error", e.getMessage());
                return null;
            }
            //2020.04.26+(e)
        }

        //번역된 결과를 받아서 처리
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.d("background result", s.toString()); //네이버에 보내주는 응답결과가 JSON 데이터이다.

            //JSON데이터를 자바객체로 변환해야 한다.

            Gson gson = new GsonBuilder().create();
            JsonParser parser = new JsonParser();
            JsonElement rootObj = parser.parse(s.toString())
            //원하는 데이터 까지 찾아 들어간다.
                    .getAsJsonObject().get("message")
                    .getAsJsonObject().get("result");
            //안드로이드 객체에 담기
            TranslatedItem items = gson.fromJson(rootObj.toString(), TranslatedItem.class);
            //Log.d("result", items.getTranslatedText());
            //번역결과를 텍스트뷰에 넣는다.
            tv_result.setText(items.getTranslatedText());
        }

        //자바용 그릇
        private class TranslatedItem {
            String translatedText;

            public String getTranslatedText() {
                return translatedText;
            }
        }
    }
}


