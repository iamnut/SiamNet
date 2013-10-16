package com.siamnet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity {
	EditText username;
	EditText password;
	//EditText debug;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ScrollView setBG = (ScrollView) findViewById(R.id.scrollView1);
		setBG.setBackgroundResource(R.drawable.bg);
		
		UpdateWifiState_Show();
		//Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
		
		username = (EditText) findViewById(R.id.editText1);
		password = (EditText) findViewById(R.id.editText2);
		//debug = (EditText) findViewById(R.id.editText3);
		
		username.setText(LoadPreferences("username"));
		password.setText(LoadPreferences("password"));
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				/*EditText username = (EditText) findViewById(R.id.editText1);
				EditText password = (EditText) findViewById(R.id.editText2);
				EditText debug = (EditText) findViewById(R.id.editText3);*/
				
				//debug.setText(page);
				
				//debug.setText(GetWifiState());
				//Toast.makeText(MainActivity.this, username.getText().toString()+" - "+password.getText().toString(), Toast.LENGTH_SHORT).show();
				
				if (GetWifiName() != null && GetWifiName().substring(0, 2).equals("WL")) //WL
				{
					//if (CheckAccInternet() == false)
						SingIn(username.getText().toString(), password.getText().toString());
					//else
						//Toast.makeText(MainActivity.this, "สามารถอินเตอร์เน็ตได้อยู่แล้ว", Toast.LENGTH_SHORT).show();
				}
				else if (GetWifiName() != null)
				{
					
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
					alertDialog.setTitle("เอ๊ะ! แน่ใจแล้ว เหรอ");
					alertDialog.setMessage("เครือข่าย wifi ที่เชื่อมต่ออาจจะ ไม่ใช่ของมหาลัย ต้องการทำต่อ ?");
					//alertDialog.setIcon(R.drawable.tick);
					alertDialog.setPositiveButton("ใช่เอาเลย", new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                // Write your code here to execute after dialog closed
		                	//if (CheckAccInternet() == false)
								SingIn(username.getText().toString(), password.getText().toString());
							//else
								//Toast.makeText(MainActivity.this, "สามารถอินเตอร์เน็ตได้อยู่แล้ว", Toast.LENGTH_SHORT).show();
		                }
					});
					alertDialog.setNegativeButton("ไม่เอาอ่ะ", new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                // Write your code here to execute after dialog closed
		                	Toast.makeText(MainActivity.this, "ไว้ลองใหม่นะ", Toast.LENGTH_SHORT).show();
		                }
					});
					alertDialog.show();
				}
				else
				{
					Toast.makeText(MainActivity.this, "โปรดเชื่อมต่อ  wifi ก่อน", Toast.LENGTH_SHORT).show();
				}
				
			}
        	
        });
		
		Button btnSingOut = (Button) findViewById(R.id.button2);
        btnSingOut.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		if (GetWifiName() != null)
        		{
        			SingOut();
        		}
        		else Toast.makeText(MainActivity.this, "โปรดเชื่อมต่อ  wifi ก่อน", Toast.LENGTH_SHORT).show();
        	}
        });
		
		Button btnSave = (Button) findViewById(R.id.button3);
		btnSave.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		SavePreferences("username", username.getText().toString());
        		SavePreferences("password", password.getText().toString());
        		Toast.makeText(MainActivity.this, "ข้อมูล " +username.getText().toString()+ " ถูกบันทึกแล้ว", Toast.LENGTH_SHORT).show();
        	}
        });
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	/*
	 * my Method
	 */
	
	public String inputStreamAsString(InputStream stream) throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line = null;

        while ((line = br.readLine()) != null)
        {
            sb.append(line + "");
        }

        br.close();
        String result = sb.toString();
        stream.close();
        return result.substring(0, result.length() - 1);
    }
    
    public String eBase64(String str)
    {
    	return (Base64.encodeToString(str.getBytes(), Base64.DEFAULT)).replace("=", "%253D");
    }
    
    public void SingIn(String username, String password)
    {
    	String urlParameters = "?q=0&s=1&un=" +eBase64(username)+ "&pw=" +eBase64(password)+ "&MULTI_LANG=eng";
    	
    	try
    	{
    		URL url = new URL("http://119.46.81.254:82/cgi-bin/auth.cgi");
    		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    		conn.setDoInput(true);
    		conn.setDoOutput(true);
    		conn.setInstanceFollowRedirects(false);
    		conn.setRequestMethod("POST");
    		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    		conn.setRequestProperty("charset", "utf-8");
    		conn.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
    		conn.setUseCaches(false);
    		
    		DataOutputStream DataOutStream = new DataOutputStream(conn.getOutputStream());
    		DataOutStream.writeBytes(urlParameters);
    		DataOutStream.flush();
    		DataOutStream.close();
    		
    		String page = inputStreamAsString((InputStream) conn.getContent());
    		
    		String text = "N/A";
    		if (page.toString().contains("Authentication Succeeded"))
    			text = "เข้าสู่ระบบอินเตอร์เน็ตสำเร็จ";
    		else if (page.toString().contains("Username not found or incorrect password !"))
    			text = "ข้อมูลไม่ถูกต้อง กรุณาลองใหม่";
    		else if (page.toString().contains("Since the user has logged in, you cannot login this user."))
    			text = "ชื่อผู้ใช้นี้ กำลังถูกใช้งาน";
    		else if (page.toString().contains("Network is unreachable"))
    			text = "กรุณาเชื่อมต่อ wifi ของมหาลัยก่อน";
    		else text = "เกิดข้อผิดพลาดที่ไม่รู้จัก";
    		Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    		
    	}
    	catch (Exception e) { Toast.makeText(MainActivity.this, "Network Error: 999", Toast.LENGTH_SHORT).show(); }
    	
    	
    }
    
    public void SingOut()
    {
    	try
    	{
    		new URL("http://119.46.81.254:82/cgi-bin/auth.cgi?q=1&s=1").getContent();
    	}
    	catch (Exception e) { }
    	Toast.makeText(MainActivity.this, "ออกจากระบบแล้ว", Toast.LENGTH_SHORT).show();
    }

    /*public int GetWifiState()
    {
    	WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
    	return wifiManager.getConnectionInfo().getNetworkId();
    }*/
    
    public String GetWifiName()
    {
    	WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
    	return wifiManager.getConnectionInfo().getSSID();
    }

    /*public boolean CheckAccInternet()
    {
    	try
    	{ new URL("http://google.co.th").getContent(); }
    	catch (Exception e) { return false; }
    	return true;
    }*/
    
    public void SavePreferences(String key, String value)
    {
    	SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
    	SharedPreferences.Editor editor = sharedPreferences.edit();
    	editor.putString(key, value);
    	editor.commit();
    }
    
    public String LoadPreferences(String key)
    {
    	SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }
    
    /*
     * My Work
     */
    
     public void UpdateWifiState_Show()
    {
    	TextView txtWifiName = (TextView) findViewById(R.id.txtWifiName);
    	if (GetWifiName() != null)
    	{
    		txtWifiName.setTextColor(Color.BLACK);
    		txtWifiName.setText("เชื่อมต่ออยู่กับ\n" +GetWifiName());
    	}
    	else
    	{
    		txtWifiName.setTextColor(Color.RED);
    		txtWifiName.setText("คุณยังไม่ได้เชื่อมต่อกับเครือข่ายใดๆ");
    	}
    	
    }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		UpdateWifiState_Show();
	}
}