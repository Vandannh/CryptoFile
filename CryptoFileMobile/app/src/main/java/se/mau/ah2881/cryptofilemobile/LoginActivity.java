package se.mau.ah2881.cryptofilemobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import controller.Controller;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        //Login-button
        final Button loginButton = findViewById(R.id.buttonSignIn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText usernameText = (EditText) findViewById(R.id.loginUsername);
                String username = usernameText.getText().toString(); //retrieving username
                EditText passwordText = (EditText) findViewById(R.id.loginPassword);
                String password = passwordText.getText().toString();
                LoginThread t = new LoginThread(username, password);
                t.start();
            }
        });

        //Sign up-button
        final Button signUpButton = findViewById(R.id.buttonNoAccount);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToRegistration();
            }
        });
    }

    public void goToHomeScreen(){
        startActivity(new Intent(this, HomeActivity.class));
    }

    public void goToRegistration(){
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private class LoginThread extends Thread {
        String username;
        String password;

        public LoginThread(String username, String password){
            this.username = username;
            this.password = password;
        }

        @Override
        public void run() {
            final Controller controller = new Controller();
            if(controller.login(username, password)){
                goToHomeScreen();
            }
        }
    }
}
