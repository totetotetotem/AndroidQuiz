package totem.androidquiz;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPrefs();
        downloadImage();
        logWrite();
        sendPacket();
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
                }
            });
        }
        stringFromJNI();
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
                    Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                    cipher.init(Cipher.DECRYPT_MODE, key, iv);
                    byte[] decrypted = cipher.doFinal(bytes);
                    Log.d("test", Integer.valueOf(decrypted.length).toString());
                    try {
                        FileOutputStream fileOutputstream = openFileOutput("out.png", Context.MODE_PRIVATE);
                        fileOutputstream.write(decrypted);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Bitmap bmp = BitmapFactory.decodeByteArray(decrypted, 16, decrypted.length);
                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                    if(imageView != null) imageView.setImageBitmap(bmp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        task.execute();
    }




    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String getPath();
    public native String stringFromJNI();
    public native void logWrite();
    public native void sendPacket();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
