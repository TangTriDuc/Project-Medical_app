package com.example.covidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.covidapp.Models.HelperClass;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText loginEmail, loginPassword, loginUsername;
    TextView signupRedirectText;
    Button loginButton;
    FirebaseAuth auth;
    DatabaseReference usersRef;
    FirebaseDatabase database;

    FirebaseDatabase firebaseDatabase;

    TextView forgotPassword;

    //Make show/hide password
    boolean passwordVisible;


    //Make checkbox remember me
    SharedPreferences sharedPreferences;
    CheckBox rememberMeCheckBox;
    //Process google sign in
    private static final int RC_SIGN_IN = 123;
    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog progressDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.login_username);
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signUpRedirectText);
        forgotPassword = findViewById(R.id.forgot_password);
        rememberMeCheckBox = findViewById(R.id.checkbox);
        SignInButton signInButton = findViewById(R.id.google_sign_in_button);
        signInButton.setColorScheme(SignInButton.COLOR_DARK); // Change to COLOR_DARK for a darker color scheme


        //Receive auth from firebase when click login with username
        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        firebaseDatabase = FirebaseDatabase.getInstance();



        //Make function go back in action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Go Back Home Page"); //Thiết lập tiêu đề nếu muốn
        actionBar.setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Xly show or hide password
        loginPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= loginPassword.getRight() - loginPassword.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = loginPassword.getSelectionEnd();
                        if (passwordVisible) {
                            //Thiết lập drawable ảnh
                            loginPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_off_24, 0);

                            //Hide password
                            loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        } else {
                            //Thiết lập drawable ảnh
                            loginPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_24, 0);

                            //Show password
                            loginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        loginPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        //Process btn login with only email authentication firebase
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String email = loginEmail.getText().toString();
//                String pass = loginPassword.getText().toString();
//
//                //Process check email with firebase
//                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                    if (!pass.isEmpty()) {
//                        auth.signInWithEmailAndPassword(email, pass)
//                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                                    @Override
//                                    public void onSuccess(AuthResult authResult) {
//                                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
//
//                                        // Save user's credentials if "Remember Me" is checked
//                                        if (rememberMeCheckBox.isChecked()) {
//                                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                                            editor.putString("email", email);
//                                            editor.putString("password", pass);
//                                            editor.apply();
//                                        }
//
//                                        startActivity(new Intent(LoginActivity.this, CovidStatistics.class));
//                                        finish();
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                    } else {
//                        loginPassword.setError("Your Field Password Empty");
//                    }
//                } else if (email.isEmpty()) {
//                    loginEmail.setError("Your Field Email Empty");
//                } else {
//                    loginEmail.setError("Your Field Email Is Not Correct");
//                }
//            }
//        });

        //Process login with both realtime database and authentication firebase
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {
                    // Sign in with email and password
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    // Login successful
                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                    // Save user's credentials if "Remember Me" is checked
                                    if (rememberMeCheckBox.isChecked()) {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("email", email);
                                        editor.putString("password", password);
                                        editor.apply();
                                    }

                                    startActivity(new Intent(LoginActivity.this, CovidStatistics.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Login failed
                                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    String username = loginUsername.getText().toString();

                    if (!username.isEmpty() && !password.isEmpty()) {
                        // Check if the username exists in the Realtime Database
                        usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                        String userEmail = userSnapshot.child("email").getValue(String.class);

                                        // Sign in with the retrieved email and password
                                        auth.signInWithEmailAndPassword(userEmail, password)
                                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                    @Override
                                                    public void onSuccess(AuthResult authResult) {
                                                        // Login successful
                                                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                                        // Save user's credentials if "Remember Me" is checked
                                                        if (rememberMeCheckBox.isChecked()) {
                                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                                            editor.putString("email", userEmail);
                                                            editor.putString("password", password);
                                                            editor.putString("username", username);
                                                            editor.apply();
                                                        }

                                                        startActivity(new Intent(LoginActivity.this, CovidStatistics.class));
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Login failed
                                                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                } else {
                                    // Username not found
                                    Toast.makeText(LoginActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Database error occurred
                                Toast.makeText(LoginActivity.this, "Database error occurred", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Email/Username or password is empty
                        if (email.isEmpty()) {
                            loginEmail.setError("Email is required");
                        } else if (username.isEmpty()) {
                            loginUsername.setError("Username is required");
                        }
                        loginPassword.setError("Password is required");
                    }
                }

            }
        });

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Creating account");
        progressDialog.setMessage("We are creating your account");

        //Process button sign in with google account
        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set click listener for Google Sign-In button
        SignInButton googleSignInButton = findViewById(R.id.google_sign_in_button);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }

            private void signInWithGoogle() {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                signInIntent.putExtra("prompt", "select_account");
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }


        });


        //Process textView of Not yet registered? Sign Up
        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        //Make checkbox remember me
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String savedEmail = sharedPreferences.getString("email", null);
        String savedPassword = sharedPreferences.getString("password", null);

        if (savedEmail != null && savedPassword != null) {
            auth.signInWithEmailAndPassword(savedEmail, savedPassword)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(LoginActivity.this, "Logged in with saved user & password", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, CovidStatistics.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Failed to log in with saved user & password", Toast.LENGTH_SHORT).show();
                        }
                    });
        }



        //Process textView Forgot Password only authentication firebase
//        forgotPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
//                EditText emailBox = dialogView.findViewById(R.id.emailBox);
//
//                builder.setView(dialogView);
//                AlertDialog dialog = builder.create();
//
//                //dialogView dùng để gọi tới dialog_forgot.xml và impact tới btn reset
//                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        String userEmail = emailBox.getText().toString();
//
//                        //Check UserEmail xem một chuỗi có rỗng hay không
//                        if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
//                            Toast.makeText(LoginActivity.this, "Enter your registered email address", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(LoginActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
//                                    dialog.dismiss();
//                                } else {
//                                    Toast.makeText(LoginActivity.this, "Unable to send, failed!", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    }
//                });
//
//                //dialogView dùng để gọi tới dialog_forgot.xml và impact tới btn cancel
//                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialog.dismiss();
//                    }
//                });
//                if (dialog.getWindow() != null) {
//                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//                }
//                dialog.show();
//            }
//        });


        //Process textView Forgot Password both realtime database & authentication firebase
        // Process textView Forgot Password
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a dialog for password recovery
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
                EditText emailBox = dialogView.findViewById(R.id.emailBox);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                // Handle the reset button click
                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userEmail = emailBox.getText().toString();

                        // Check if the entered email is valid
                        if (TextUtils.isEmpty(userEmail) || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                            Toast.makeText(LoginActivity.this, "Enter your registered email address", Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Send password reset email
                        auth.sendPasswordResetEmail(userEmail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            //Process delete password in realtime after forget password
                                            // Save the email in the Realtime Database
                                            String userId = auth.getCurrentUser().getUid();
                                            DatabaseReference userRef = usersRef.child(userId);
                                            userRef.child("email").setValue(userEmail);

                                            // Delete the password from the Realtime Database
                                            userRef.child("password").removeValue()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(LoginActivity.this, "Password deleted from the database", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(LoginActivity.this, "Failed to delete password from the database", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                            Toast.makeText(LoginActivity.this, "Check your email for password reset", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();

////                                            //Only save in authentication firebase
//                                            Toast.makeText(LoginActivity.this, "Check your email for password reset in your Gmail", Toast.LENGTH_LONG).show();
//                                            dialog.dismiss();
                                        } else {
                                            // Handle specific exceptions
                                            try {
                                                throw Objects.requireNonNull(task.getException());
                                            } catch (FirebaseAuthInvalidUserException e) {
                                                Toast.makeText(LoginActivity.this, "This email address is not registered", Toast.LENGTH_LONG).show();
                                            } catch (FirebaseAuthRecentLoginRequiredException e) {
                                                Toast.makeText(LoginActivity.this, "You recently logged in, please try again later", Toast.LENGTH_LONG).show();
                                            } catch (Exception e) {
                                                Toast.makeText(LoginActivity.this, "Unable to send password reset email", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                });
                    }
                });

                // Handle the cancel button click
                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }

                dialog.show();
            }
        });

    }

    //Process button sign in with google account
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign-In failed
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign-in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this, "Google Sign-In successful", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = auth.getCurrentUser();
                            // Process to save the user data to the Firebase Realtime Database
                            saveUserDataToDatabase(user);

                            //Process show profile in nav_drawer after google sign in
                            saveUserDataToSharedPreferences(user);

                            // Move to the next activity
                            startActivity(new Intent(LoginActivity.this, CovidStatistics.class));
                            finish();
                        } else {
                            // Sign-in failed
                            Toast.makeText(LoginActivity.this, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    //Process show profile in nav_drawer after google sign in
                    private void saveUserDataToSharedPreferences(FirebaseUser user) {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", user.getDisplayName());
                        editor.putString("email", user.getEmail());
                        editor.apply();

                    }

                    // Process to save the user data to the Firebase Realtime Database
                    private void saveUserDataToDatabase(FirebaseUser user) {

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference userRef = database.getReference("users").child(user.getUid());

                        // Save user data to the database
                        userRef.child("name").setValue(user.getDisplayName());
                        userRef.child("email").setValue(user.getEmail());
                        // Add more fields as needed

                        // Alternatively, you can save the user object directly
                        // userRef.setValue(user);
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

