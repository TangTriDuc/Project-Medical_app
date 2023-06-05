package com.example.covidapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidapp.Models.Message;
import com.example.covidapp.Models.MessageAdapter;
import com.google.android.material.navigation.NavigationView;

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

public class ChatGPTActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;


    //Make navigation drawer
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, global, ChatAI, about, logout;

    //Make logout
    SharedPreferences sharedPreferences;

    //Take process from Message.java
    List<Message> messageList;

    //Take process from MessageAdapter.java
    MessageAdapter messageAdapter;

    //Take from okhttp
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        //Take process from Message.java
        messageList = new ArrayList<>();

        //RecyclerView
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

        //Process show navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        global = findViewById(R.id.global);
        ChatAI = findViewById(R.id.chatAi);
        about = findViewById(R.id.about);
        logout = findViewById(R.id.logout);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(ChatGPTActivity.this, MainActivity.class);
            }
        });

        global.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(ChatGPTActivity.this, CovidStatistics.class);

            }
        });

        ChatAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(ChatGPTActivity.this, AboutActivity.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        //Process button send
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = messageEditText.getText().toString().trim();
                //Toast.makeText(ChatGPTActivity.this, question, Toast.LENGTH_SHORT).show();

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


    //Process logout
//    private void logout() {
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.remove("username");
//        editor.remove("password");
//        editor.apply();
//
//        Toast.makeText(ChatGPTActivity.this, "Logout Successful!", Toast.LENGTH_SHORT).show();
//
//        Intent intent = new Intent(ChatGPTActivity.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//
//    }

    //Process logout with alert dialog
    private void logout() {
        // Create the AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the title and message of the dialog
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");

        // Set the positive button and its callback
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Yes", perform logout
                performLogout();
            }
        });

        // Set the negative button and its callback
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "No", dismiss the dialog
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void performLogout() {
        // Remove the username and password from shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username");
        editor.remove("email");
        editor.remove("password");
        editor.apply();

        // Show the "Logout Successful!" toast
        Toast.makeText(ChatGPTActivity.this, "Logout Successful!", Toast.LENGTH_SHORT).show();

        // Start the LoginActivity
        Intent intent = new Intent(ChatGPTActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

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
                    //JSONObject jsonObject = null;
                    try {
                        JSONObject jsonObject= new JSONObject(response.body().string());
                        JSONArray jsonArray = null;
                        jsonArray = jsonObject.getJSONArray("choices");
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


    //Process show navigation drawer
    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class secondActivity) {
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }
}