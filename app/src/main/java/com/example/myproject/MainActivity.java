package com.example.myproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;

import io.github.namankhurpia.DAO.DAOImpl;
import io.github.namankhurpia.Pojo.Image.ImageRequest;
import io.github.namankhurpia.Pojo.Image.ImageResponse;
import io.github.namankhurpia.Service.EasyopenaiService;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;//顯示圖片用
    private EditText editText;//輸入文字用
    private static final String accessToken = "填入產生OpenAI API的accessToken";

    public void sendImagePrompt(ImageRequest imageRequest){
        try{
            //呼叫EasyopenaiService
            ImageResponse response = new EasyopenaiService(
                    new DAOImpl()).
                    createImage(accessToken,
                            imageRequest);
            //取得文生圖片的圖片網址
            String url = response.data.get(0).getUrl();
            Log.d("Howard",url);//顯示url測試用
//因為sendImagePrompt 會在新的執行續中運行所以必須運行在runOnUiThread
            runOnUiThread(()->{
                //呼叫Glide在imageView內顯示url的圖片
                Glide.with(this)
                        .load(url)
                        .into(imageView);
                //顯示提示訊息
                Toast.makeText(this, "顯示圖片中..",
                        Toast.LENGTH_LONG).show();
            });

        }catch(IOException ex){
            Log.e("Howard",ex.toString());//如有錯誤可顯示
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //找出元件
        imageView = findViewById(R.id.imageView);
        editText =  findViewById(R.id.editTextText);
        editText.setText("dog");
        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(v->{
            //取得輸入的文生圖指令
            String prompt = editText.getText().toString();
            //設定傳給Open AI的 文生圖指令 與選擇dall-e-2模型
            ImageRequest imageRequest  = ImageRequest.builder()
                    .prompt(prompt)
                    .model("dall-e-2")
                    .build();
            //開啟新的執行序,呼叫sendImagePrompt
            new Thread(()->{
                sendImagePrompt(imageRequest);
            }).start();
            Toast.makeText(this, "產生圖片中..",
                    Toast.LENGTH_LONG).show();
        });
    }
}