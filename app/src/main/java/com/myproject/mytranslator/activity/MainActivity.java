package com.myproject.mytranslator.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.myproject.mytranslator.DTO.LanguageInfo;
import com.myproject.mytranslator.R;
import com.myproject.mytranslator.adapter.RecyclerViewAdapter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.myproject.mytranslator.adapter.RecyclerViewAdapter.filteredList;

public class MainActivity extends AppCompatActivity {

    public static int ENGLISH_TEXT_SAVE = 1234;
    public static int ENGLISH_VOICE_SAVE = 5678;
    public static int WORD_DELETE=1112;
    public static ArrayList<LanguageInfo> languageInfoArrayList = new ArrayList<>();
    public static ArrayList<LanguageInfo> languageInfoArrayListTemporary = new ArrayList<>();
    public static RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(languageInfoArrayList);
    private EditText editTextSearch;
    // 리사이클러뷰
    RecyclerView recyclerView = null;
    private String TAG = "MainActivity";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        editTextSearch = findViewById(R.id.editTextSearch);
        Log.d(TAG, "onCreate is called");
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        /*preferences = getSharedPreferences("WORD_ENGLISH", MODE_PRIVATE);
        preferences = getSharedPreferences("WORD_KOREAN", MODE_PRIVATE);
        editor = preferences.edit();*/

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                recyclerViewAdapter.getFilter().filter(s);
            }
        });
        setItemClickListener();
    }

    //리싸이클러뷰 아이템 클릭 리스너 -> 상세 페이지로 이동
    private void setItemClickListener() {

        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                if ((editTextSearch.getText().toString()).equals("")) {
                    LanguageInfo languageInfo = new LanguageInfo();
                    languageInfo = languageInfoArrayList.get(position);
                    Intent intent = new Intent(MainActivity.this, ViewWordDetailsActivity.class);
                    intent.putExtra("CorrespondingWord", languageInfo);
                    intent.putExtra("WordPosition", position);
                    startActivityForResult(intent,WORD_DELETE);
                }else{
                    languageInfoArrayListTemporary.addAll(languageInfoArrayList);
                    languageInfoArrayList.clear();
                    languageInfoArrayList.addAll(filteredList);

                    LanguageInfo languageInfo = new LanguageInfo();
                    languageInfo = languageInfoArrayList.get(position);
                    Intent intent = new Intent(MainActivity.this, ViewWordDetailsActivity.class);
                    intent.putExtra("CorrespondingWord", languageInfo);
                    intent.putExtra("WordPosition", position);
                    startActivityForResult(intent,WORD_DELETE);
                }
            }
        });

    }

    public void onClickAddMemo(View view) {
        show();
    }

    private void show() {
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("텍스트로 입력");
        ListItems.add("음성으로 입력");

        final CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

        final List SelectedItems = new ArrayList();
        int defaultItem = 0;
        SelectedItems.add(defaultItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("선택해 주세요.");
        builder.setSingleChoiceItems(items, defaultItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SelectedItems.clear();
                        SelectedItems.add(which);
                    }
                });
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String msg = "";

                        if (!SelectedItems.isEmpty()) {
                            int index = (int) SelectedItems.get(0);
                            msg = ListItems.get(index);
                        }
                        if (msg.equals("텍스트로 입력")) {
                            Intent intent = new Intent(MainActivity.this, AddEnglishTextActivity.class);
                            startActivityForResult(intent, ENGLISH_TEXT_SAVE);
                        } else {
                            Intent intent = new Intent(MainActivity.this, AddEnglishVoiceActivity.class);
                            startActivityForResult(intent, ENGLISH_VOICE_SAVE);
                        }

                        Toast.makeText(getApplicationContext(), "Items Selected.\n" + msg, Toast.LENGTH_LONG).show();
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String str = "";
        String translatedString = "";
        if (resultCode == RESULT_OK && data != null) {

            if (requestCode == ENGLISH_TEXT_SAVE) {
                String englishText = data.getStringExtra("EnglishText");
                str = englishText;
                TranslateTask asyncTask = new TranslateTask();
                try {
                    LanguageInfo languageInfo = new LanguageInfo();
                    translatedString = asyncTask.execute(str).get();
                    languageInfo.setTextEnglish(str);
                    languageInfo.setTextKorean(translatedString);
                    languageInfoArrayList.add(languageInfo);
                    recyclerViewAdapter.notifyDataSetChanged();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if(requestCode == ENGLISH_VOICE_SAVE) {
                String englishVoice = data.getStringExtra("EnglishVoice");
                str = englishVoice;
                TranslateTask asyncTask = new TranslateTask();
                try {
                    LanguageInfo languageInfo = new LanguageInfo();
                    translatedString = asyncTask.execute(str).get();
                    languageInfo.setTextEnglish(str);
                    languageInfo.setTextKorean(translatedString);
                    languageInfoArrayList.add(languageInfo);
                    recyclerViewAdapter.notifyDataSetChanged();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if(requestCode==WORD_DELETE){
                int position = data.getIntExtra("Position",0);
                if ((editTextSearch.getText().toString()).equals("")) {
                    languageInfoArrayList.remove(position);
                } else {
                    if (languageInfoArrayListTemporary.contains(languageInfoArrayList.get(position))) {
                        languageInfoArrayListTemporary.remove(languageInfoArrayList.get(position));
                        languageInfoArrayList.clear();
                        languageInfoArrayList.addAll(languageInfoArrayListTemporary);
                        languageInfoArrayListTemporary.clear();
                    }
                }
                editTextSearch.setText("");
                recyclerViewAdapter.notifyDataSetChanged();

            }

        }
    }

    public void onClickTranslate(View view) {

        Intent intent = new Intent(MainActivity.this, TranslateActivity.class);
        startActivity(intent);

    }


    public class TranslateTask extends AsyncTask<String, Void, String> {


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
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
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
                if (responseCode == 200) { // 정상 호출
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
                //return ;

                //JSON데이터를 자바객체로 변환해야 한다.

                Gson gson = new GsonBuilder().create();
                JsonParser parser = new JsonParser();
                JsonElement rootObj = parser.parse(response.toString())
                        //원하는 데이터 까지 찾아 들어간다.
                        .getAsJsonObject().get("message")
                        .getAsJsonObject().get("result");
                //안드로이드 객체에 담기
                TranslatedItem items = gson.fromJson(rootObj.toString(), TranslatedItem.class);
                //Log.d("result", items.getTranslatedText());
                //번역결과를 텍스트뷰에 넣는다.
                return (items.getTranslatedText());

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
