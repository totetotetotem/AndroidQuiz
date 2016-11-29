package totem.androidquiz;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

public class MainActivity extends AppCompatActivity {
    Boolean[] clearFlag = new Boolean[6];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPrefs();
        downloadImage();
        logWrite();
        sendPacket();
        checkState();
        Button button = (Button) findViewById(R.id.button);
        if(button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences prefs = getPreferences(MODE_WORLD_READABLE);
                    boolean hoge = prefs.getBoolean("putFlag", false);
                    if(hoge) {
                        TextView textView = (TextView) findViewById(R.id.flagText);
                        if(textView != null) {
                            textView.setText(stringFromJNI());
                        }
                    }
                    checkFlag();
                }
            });
        }
        stringFromJNI();
    }

    protected void checkFlag() {
        EditText editText = (EditText) findViewById(R.id.editText);
        List<String> flags = Arrays.asList(
                "77ac3d4abbf3e551f5ec719ddceaf8f9a075d1e731135c17c8075e46d575a9e8",
                "1d2ffe6406ea98fb2c90aa504d20cf5972bb06da45d748660b062ca177799c2d",
                "0037192f2c9d8f182b3c35d4e3e33b37cda1091654f50ac756b5d7d0469154ee",
                "80f3419aa3a46e2e558fc98ebf8d9c527e450f7739907a5f5d8900c6dec55b02",
                "ed76dd137890c7354d621002607b60d9623170d8a21bb486404255302bb58ce3",
                "8a75aa3e15e5e93cf4527a0498cebbfd46987337b87e571f67bd570ec77d0154"
        );
        ArrayList<Integer> views = new ArrayList<> (Arrays.asList(
                R.id.flagView1, R.id.flagView2, R.id.flagView3,
                R.id.flagView4, R.id.flagView5, R.id.flagView6
        ));
        if(editText != null) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                String input = String.valueOf(Hex.encodeHex(md.digest(editText.getText().toString().getBytes())));
                Integer matched = flags.indexOf(input);
                if(matched == -1) {
                    Toast.makeText(this, "input is not flag", Toast.LENGTH_SHORT).show();
                } else {
                    TextView textView = (TextView)findViewById(views.get(matched));
                    if(textView != null) {
                        textView.setBackgroundColor(Color.YELLOW);
                        textView.setTextColor(Color.BLACK);
                        clearFlag[matched] = Boolean.TRUE;
                        Toast.makeText(this, "Congratulation!", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void checkState() {
        SharedPreferences prefs = getSharedPreferences("save", MODE_PRIVATE);
        for(int i = 0; i < 6; i++) {
            String salt = prefs.getString("salt"+Integer.valueOf(i).toString(), "No salt");
            String hoge = prefs.getString("hoge"+Integer.valueOf(i).toString(), "No hoge");
            try {
            String hash =
                    String.valueOf(Hex.encodeHex(MessageDigest.getInstance("SHA-256").digest(cryptStr(salt, Boolean.TRUE).getBytes())));
                if(hoge.equals(hash)) {
                    ArrayList<Integer> views = new ArrayList<>(Arrays.asList(
                            R.id.flagView1, R.id.flagView2, R.id.flagView3,
                            R.id.flagView4, R.id.flagView5, R.id.flagView6
                    ));
                    TextView textView = (TextView) findViewById(views.get(i));
                    if (textView != null) {
                        textView.setBackgroundColor(Color.YELLOW);
                        textView.setTextColor(Color.BLACK);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void initPrefs() {
        SharedPreferences prefs = getPreferences(MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("putFlag", false);
        editor.apply();
    }

    protected void downloadImage() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            byte[] bytes;
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    URL url = new URL(getPath());
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setConnectTimeout(20000);
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    int res = urlConnection.getResponseCode();

                    if (res != HttpURLConnection.HTTP_OK) {
                        return null;
                    } else {
                        byte[] data = new byte[4096];
                        InputStream is = urlConnection.getInputStream();
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        int len;
                        while((len = is.read(data, 0, data.length))!= -1) {
                            buffer.write(data, 0, len);
                        }
                        buffer.flush();
                        bytes = buffer.toByteArray();
                        is.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                String ENCRYPT_KEY = "ImageIsEncrypted";
                String ENCRYPT_IV = "AES_is_secure_vv";
                try {
                    byte[] keyByte = ENCRYPT_KEY.getBytes("UTF-8");
                    byte[] ivByte = ENCRYPT_IV.getBytes("UTF-8");
                    SecretKeySpec key = new SecretKeySpec(keyByte, "AES");
                    IvParameterSpec iv = new IvParameterSpec(ivByte);
                    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    cipher.init(Cipher.DECRYPT_MODE, key, iv);
                    byte[] decrypted = cipher.doFinal(bytes);
                    Bitmap bmp = BitmapFactory.decodeByteArray(decrypted, 0, decrypted.length);
                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                    if(imageView != null) imageView.setImageBitmap(bmp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        task.execute();
    }

    @Override
    protected void onPause() {
        SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for(int i = 0; i < 6; i++) {
            try {
            String salt = generateSalt();
                editor.putString("salt"+Integer.valueOf(i).toString(), salt);
                editor.putString("hoge"+Integer.valueOf(i).toString(),
                        String.valueOf(Hex.encodeHex(MessageDigest.getInstance("SHA-256").digest(cryptStr(salt, clearFlag[i]).getBytes()))));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        editor.apply();
    }

    protected String generateSalt() {
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            byte[] salt = new byte[32];
            random.nextBytes(salt);
            return new String(salt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String getPath();
    public native String stringFromJNI();
    public native void logWrite();
    public native void sendPacket();
    public native String cryptStr(String str, Boolean b);

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
