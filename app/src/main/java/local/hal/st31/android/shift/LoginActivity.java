package local.hal.st31.android.shift;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends BaseActivity {

    private static final String ACCESS_URL = "http://flexibleshift.sakura.ne.jp/shift_app_backend/controllers/login_controller.php";
    private String mail;
    private String password;
    private String nickName = null;
    EditText etMail;
    EditText etPassword;
    private int status = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.activity_login);
        etMail = findViewById(R.id.mail);
        etPassword = findViewById(R.id.password);

    }

    private void init() {
        SharedPreferences sp = getSharedPreferences("login", getApplicationContext().MODE_PRIVATE);
        String savedMail= sp.getString("mail",null);
        String savedPassword = sp.getString("password",null);

        if(savedMail != null && savedPassword !=null){
//            LoginThread loginThread = new LoginThread();
//            loginThread.execute(ACCESS_URL,savedMail,savedPassword);
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.putExtra("nickName",nickName);
            startActivity(intent);
            LoginActivity.this.finish();
        }
    }

    public void loginButtonClick(View view){
        mail = etMail.getText().toString();
        password = etPassword.getText().toString();
        if(mail.equals("")){
            Toast.makeText(getApplicationContext(),"メールアドレスを入力してください",Toast.LENGTH_LONG).show();
        }else if(password.equals("")){
            Toast.makeText(getApplicationContext(),"パスワードを入力してください",Toast.LENGTH_LONG).show();
        }else{
            LoginThread loginThread = new LoginThread();
            loginThread.execute(ACCESS_URL,mail,password);
        }
    }

    private class LoginThread extends AsyncTask<String, Void, String> {
        private static final String DEBUG_TAG = "loginThread";

        @Override
        protected String doInBackground(String... params) {
            String uri = params[0];
            String strmail = params[1];
            String strPassword = params[2];

            String postData ="mail="+strmail+"&password="+strPassword;
            HttpURLConnection con = null;
            InputStream is = null;
            String result = "";

            try {
                URL url = new URL(uri);
                con = (HttpURLConnection) url.openConnection();
//                con.setRequestProperty("Content-Type", "application/json; utf-8"); // 追記
//                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");
                con.setConnectTimeout(2000);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();
                int status = con.getResponseCode();
                if(status != 200){
                    throw new IOException("ステータスコード："+status);
                }
                is = con.getInputStream();
                result = is2String(is);
            } catch (MalformedURLException ex) {
                Log.e(DEBUG_TAG, "URL変換失敗", ex);
            } catch (IOException ex) {
                Log.e(DEBUG_TAG, "通信失敗", ex);
            } finally {
                if (con != null) {
                    con.disconnect();
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        Log.e(DEBUG_TAG, "InputStream解放失敗", ex);
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            String mail = null;
            String pass = null;
            int userId = 0;
            try {
                JSONObject object = new JSONObject(result);
                userId = object.getInt("user_id");
                mail = object.getString("email");
                pass = object.getString("password");
                nickName = object.getString("nickname");
                status = object.getInt("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(status == 1){
                SharedPreferences sp = getSharedPreferences("login", getApplicationContext().MODE_PRIVATE);
                sp.edit().putString("mail",mail).putString("password",pass).putInt("userId",userId).apply();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("nickName",nickName);
                startActivity(intent);
                LoginActivity.this.finish();
            }else{
                Toast.makeText(getApplicationContext(),"メールアドレスとパスワードをチェックしてからも一回試し下さい",Toast.LENGTH_LONG).show();
            }

        }

    }



    private String is2String(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuffer sb = new StringBuffer();
        char[] b = new char[1024];
        int line;
        while (0 <= (line = reader.read(b))) {
            sb.append(b, 0, line);
        }
        return sb.toString();
    }
}
