package cn.edu.gdmec.s07150808.inputdemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private String uploadFile = "file:///android_asset/j1607.jpg";
    private String srcfile ="j1607.jpg";

    // 服务器上接收文件的处理页面，这里根据需要换成自己的
    private String actionUrl = "http://191.168.191.1/testupload.php";
    EditText editText1,editText2;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText1 = (EditText) findViewById(R.id.edit1);
        editText2 = (EditText) findViewById(R.id.edit2);
        button = (Button) findViewById(R.id.power);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(runnable).start();
            }
        });
    }
    Handler mHandler=new Handler(){
        @Override
        public  void handleMessage(Message mes){
            String ss=mes.obj.toString();
            Toast.makeText(MainActivity.this,ss, Toast.LENGTH_SHORT).show();
            editText2.setText(actionUrl);
            editText1.setText(srcfile);
        }
    };
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            uploadFile(srcfile,actionUrl);
            Message mes=new Message();
            mes.obj="file upload successfully(上传成功)!";
            mHandler.sendMessage(mes);
        }
    };

    /* 上传文件至Server。phpUrl：接收文件的处理页面 */
    private void uploadFile(String uploadFile,String phpUrl)
    {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        try
        {
            URL url = new URL(phpUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            // 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
            // 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
            httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
            // 允许输入输出流
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            // 使用POST方法
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(
                    httpURLConnection.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
                    + uploadFile
                    + "\""
                    + end);
            dos.writeBytes(end);

            InputStream fis = getResources().getAssets().open(uploadFile);//从assets 文件夹中获取文件并读取数据

            byte[] buffer = new byte[8192]; // 8k
            int count = 0;
            // 读取文件
            while ((count = fis.read(buffer)) != -1)
            {
                dos.write(buffer, 0, count);
            }
            fis.close();

            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();

            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String result = br.readLine();

            //  Toast.makeText(this, result, Toast.LENGTH_LONG).show();
            dos.close();
            is.close();

        } catch (Exception e)
        {
            e.printStackTrace();
            // setTitle(e.getMessage());
        }
    }
}
