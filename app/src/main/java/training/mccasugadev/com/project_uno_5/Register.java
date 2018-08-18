package training.mccasugadev.com.project_uno_5;

import android.content.Context;
import android.content.DialogInterface;
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

public class Register extends AppCompatActivity {

    Button registerGButton, backToLoginButton;
    EditText regFirstName, regLastName, regAge, regUsername, regPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize the buttons
        registerGButton = findViewById(R.id.registerGButton);
        backToLoginButton = findViewById(R.id.backToLoginButton);

        // Initialize the fields
        regFirstName = findViewById(R.id.firstNameEditText);
        regLastName = findViewById(R.id.lastNameEditText);
        regAge = findViewById(R.id.ageEditText);
        regUsername = findViewById(R.id.usernameEditText);
        regPassword = findViewById(R.id.passwordEditText);

        registerGButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegister(v);
            }
        });

        backToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToLoginPage = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(backToLoginPage);
            }
        });
    }

    protected void onRegister(View view) {
        System.out.println("onRegister function");
        String reg_firstname = regFirstName.getText().toString();
        String reg_lastname = regLastName.getText().toString();
        int reg_age = Integer.parseInt(regAge.getText().toString());
        String reg_username = regUsername.getText().toString();
        String reg_password = regPassword.getText().toString();

        BackgroudWorkerRegister backgroudWorkerRegister = new BackgroudWorkerRegister(this, reg_age);
        backgroudWorkerRegister.execute(reg_firstname, reg_lastname, reg_username, reg_password);
    }

    private class BackgroudWorkerRegister extends AsyncTask<String, Void, String> {
        Context context;
        AlertDialog alertDialog;
        int regAge;

        @Override
        protected String doInBackground(String... strings) {
            System.out.println("doInBackground Function");
            String registerURL = "http://192.168.1.101/android_test/register.php";

            try {
                URL theURL = new URL(registerURL);   // Create our URL for registration

                // Open our POST HttpConnection
                HttpURLConnection httpURLConnection = (HttpURLConnection) theURL.openConnection();
                httpURLConnection.setRequestMethod("POST");  // Sets request method to POST

                // Sets the connection to enable both input and output
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                // Open the outputstream for feeding data to the server
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                // Create our request headers
                String rFirstNameEnc = URLEncoder.encode("reg_fname", "UTF-8") + "=" + URLEncoder.encode(strings[0], "UTF-8");
                String rLastNameEnc = URLEncoder.encode("reg_lname", "UTF-8") + "=" + URLEncoder.encode(strings[1], "UTF-8");
                String rAgeEnc = URLEncoder.encode("reg_age", "UTF-8") + "=" + URLEncoder.encode(this.regAge + "", "UTF-8");
                String rUsernameEnc = URLEncoder.encode("reg_username", "UTF-8") + "=" + URLEncoder.encode(strings[2], "UTF-8");
                String rPasswordEnc = URLEncoder.encode("reg_password", "UTF-8") + "=" + URLEncoder.encode(strings[3], "UTF-8");

                // Concatenate all request key-value pairs
                String postRequest = rFirstNameEnc + "&" + rLastNameEnc + "&" + rAgeEnc + "&" + rUsernameEnc + "&" + rPasswordEnc;

                // Write the request to our URL
                bufferedWriter.write(postRequest);

                // Close unused resources
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                // Open the input stream to get response from the server
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String serverResult = "";

                // Read response from the server
                for (String line = bufferedReader.readLine(); line != null;) {
                    serverResult += line;
                    line = null;
                }

                // Close unused resources
                bufferedReader.close();
//                inputStream.close();

                // Terminate our connection to the server
                httpURLConnection.disconnect();

                return serverResult;
            } catch (MalformedURLException e) {
                System.err.println("URL Error");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }

        public BackgroudWorkerRegister(Context context, int regAge) {
            this.context = context;
            this.regAge = regAge;
        }

        @Override
        protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(this.context).create();
            alertDialog.setTitle("Registration Status");
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Intent goBackToLogin = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(goBackToLogin);
                }
            });
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.print("OnPostExecute");
            alertDialog.setMessage(s);
            alertDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
