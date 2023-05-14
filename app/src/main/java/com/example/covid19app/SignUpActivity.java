package com.example.covid19app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText signupEmail, signupPassword;
    Button signupButton;
    TextView loginRedirectText;

    //xly show/hide pass
    boolean passwordVisible;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Process FirebaseAuth
        auth = FirebaseAuth.getInstance();

        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);

        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        //Make function go back in action bar
        getSupportActionBar().setTitle("Go Back Home Page");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //Xly show or hide password
        signupPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= signupPassword.getRight() - signupPassword.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = signupPassword.getSelectionEnd();
                        if (passwordVisible) {
                            //Thiết lập drawable ảnh
                            signupPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_off_24, 0);

                            //Hide password
                            signupPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        } else {
                            //Thiết lập drawable ảnh
                            signupPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_24, 0);

                            //Show password
                            signupPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        signupPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        //Process button SignUp
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Process user&pass để trả về một chuỗi đã được chuyển đổi từ giá trị nhập liệu trong trường văn bản và loại bỏ các ký tự trắng không cần thiết ở đầu và cuối chuỗi.
                String user = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();

                if (user.isEmpty()){
                    signupEmail.setError("Your Email is empty");
                }
                if (pass.isEmpty()){
                    signupPassword.setError("Your Password is empty");
                } else{
                    auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));

                            } else {
                                Toast.makeText(SignUpActivity.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        //Process textview of Already an user?Login
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
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