package com.example.covid19app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatGPTActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;

    //Take process from Message.java
    List<Message> messageList;

    //Take process from MessageAdapter.java
    MessageAdapter messageAdapter;

    //Take from okhttp
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(40, TimeUnit.SECONDS)
            .build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        //Take process from Message.java
        messageList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycle_view);
        welcomeTextView = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);

        //Setup recycle view
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //Make function go back in action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Go Back Global Statistics"); //Thiết lập tiêu đề nếu muốn
        actionBar.setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Process button send
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = messageEditText.getText().toString().trim();
                // Toast.makeText(MainActivity.this, question, Toast.LENGTH_SHORT).show();

                //Process when click btn send after write then it was added to chat
                addToChat(question, Message.SENT_BY_ME);

                //Process when click btn send after write then it was added to chat. And this text in chat box will disable
                messageEditText.setText("");

                //After send text to ask will make call API from chatgpt
                callAPI(question);
                welcomeTextView.setVisibility(View.GONE);
            }
        });
    }

    //Process when to add send the message in messageList
    void addToChat(String message, String sentBy) {

        //chạy mã trong luồng giao diện người dùng (UI thread) như là hiện ra khi nhận dc data
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    //Process respond to chat from add to chat
    void addResponse(String response) {
        messageList.remove(messageList.size()-1);
        addToChat(response, Message.SENT_BY_BOT);
    }

    //Process to call api from chatgpt (json body)
    //String question from send btn
    void callAPI(String question) {
        messageList.add(new Message("Typing...", Message.SENT_BY_BOT));
        //okhttp
        JSONObject jsonBody = new JSONObject();
        //start to add model from request body in API reference of chatgpt
        try {
            jsonBody.put("model", "gpt-3.5-turbo" );
            JSONArray messageArr = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("role", "user");
            obj.put("content", question);

            messageArr.put(obj);
            jsonBody.put("messages", messageArr);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        //Phản hồi từ call API
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                //Take from page openai
                .url("https://api.openai.com/v1/chat/completions")
                //.header("Authorization","Bearer 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")
                .header("Authorization","Bearer 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")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to" + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    //response lấy từ choices
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        //String result = jsonArray.getJSONObject(0).getString("text");
                        String result = jsonArray.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }else {
                    addResponse("Failed to load response due to" + response.body().string());
                }
            }

        });

    }

    //Process ActionBack to go back in action bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

}