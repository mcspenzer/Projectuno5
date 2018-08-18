package training.mccasugadev.com.project_uno_5;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button loginButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.usernameTextView);
        password = findViewById(R.id.passwordPassword);

        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // Set event handlers upon button clicks
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogin(v);
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToRegistration = new Intent(getApplicationContext(), Register.class);
                startActivity(goToRegistration);
            }
        });
    }

    protected void onLogin(View view) {
        String login_username = username.getText().toString();
        String login_password = password.getText().toString();
        String login_type = "login";

        BackgroundWorkerLogin backgroundWorkerLogin = new BackgroundWorkerLogin(this);
        backgroundWorkerLogin.execute(login_username, login_password, login_type);
    }

    private class BackgroundWorkerLogin extends AsyncTask<String, Void, String> {
        Context context;
        AlertDialog alertDialog;
        public BackgroundWorkerLogin(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            String type = strings[2];
            String loginURL = "http://192.168.1.101/android_test/login.php";

            try {
                URL theURL = new URL(loginURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) theURL.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                // Write our request
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                // Encode the URL with POST request headers
                String username_encoded = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(strings[0], "UTF-8");
                String password_encoded = URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(strings[1], "UTF-8");

                String postData = username_encoded + "&" + password_encoded;

                bufferedWriter.write(postData); // Write the URL into the HttpURLConnection's output stream

                // Close resources
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                // Read the response of the server
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String result = "";
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                httpURLConnection.disconnect();
                return result;

            } catch (MalformedURLException e) {
                System.err.println("URL Error");
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Input Error");
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Login Status");
        }

        @Override
        protected void onPostExecute(String theString) {
            alertDialog.setMessage(theString);
            alertDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
