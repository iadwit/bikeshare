package com.student.bikeshare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.student.bikeshare.models.DataModifiedModel;
import com.student.bikeshare.models.MailModel;
import com.student.bikeshare.models.ResponseModel;
import com.student.bikeshare.retorfitClientAndService.RetrofitClient;
import com.student.bikeshare.retorfitClientAndService.WebServices;

import org.json.JSONObject;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements PaymentResultWithDataListener {

    private final Context context = MainActivity.this;
    private TextView tvSt;
    private String lastUpdatedTime;
    private String startTime;
    private Vibrator vibrator;
    private TextToSpeech textToSpeech;
    private Button btnPay;
    private Checkout checkout;
    private final String adminEmail = "hritikg2110@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findId();
        getDataFromServer(true);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnPay.getText().equals(getString(R.string.start))) {
                    startPayment();
                } else {
                    set_field(1, "0");
                    sendMail("stop");
                }
            }
        });
    }

    public void startPayment() {
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Bike Share");
            options.put("description", "Bike renting service");
            options.put("currency", "INR"); // Use your currency code
            options.put("amount", "10000"); // Amount in paise (100 INR = 10000 paise)

            checkout.open(this, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findId() {

        Checkout.preload(getApplicationContext());

        checkout = new Checkout();
        checkout.setKeyID("rzp_test_gUynu79BDnJdkq");
        textToSpeech = new TextToSpeech(getApplicationContext(), status -> textToSpeech.setLanguage(Locale.getDefault()));
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tvSt = findViewById(R.id.tv_st);
        lastUpdatedTime = getString(R.string.last_update_time);
        startTime = getString(R.string.start_time);
        btnPay = findViewById(R.id.btn_pay);
    }

    private void getDataFromServer(boolean b) {
        WebServices webServices = RetrofitClient.getClient(Constants.SERVER_NAME).create(WebServices.class);
        webServices.get_fields(19, "android").enqueue(new Callback<DataModifiedModel>() {
            @Override
            public void onResponse(Call<DataModifiedModel> call, Response<DataModifiedModel> response) {
                DataModifiedModel dataModel = response.body();
                if (dataModel != null) {
                    tvSt.setText(String.format(startTime, dataModel.getField1().getLast_update_time()));
                    if (dataModel.getField1().getValue().equals("1")) {
                        tvSt.setVisibility(View.VISIBLE);
                        btnPay.setText(getString(R.string.stop));
                    } else {
                        tvSt.setVisibility(View.GONE);
                        btnPay.setText(getString(R.string.start));
                    }
                }
            }

            @Override
            public void onFailure(Call<DataModifiedModel> call, Throwable t) {
                Toast.makeText(context, "There must be some connectivity issue", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void set_field(int field, String value) {
        WebServices webServices = RetrofitClient.getClient(Constants.SERVER_NAME).create(WebServices.class);
        webServices.set_field(19, field, value).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                System.out.println(response.body());
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {

            }
        });
    }

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 5000;

    @Override
    protected void onResume() {
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                getDataFromServer(false);
            }
        }, delay);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                FirebaseUtil.logout();
                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();

        set_field(1, "1");
        btnPay.setText(getString(R.string.stop));
        sendMail("start");
    }

    private void sendMail(String status) {
        WebServices webServices = RetrofitClient.getClient(Constants.SERVER_NAME).create(WebServices.class);
        MailModel mailModel = new MailModel(
                adminEmail,
                FirebaseUtil.getUserEmailId(),
                "Bike ride : " + status,
                "Hi, \nWe have " + status + " the ride.");

        webServices.sendMail(mailModel).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                System.out.println(response.body());
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {

            }
        });
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Toast.makeText(this, "Payment Failed: " + s, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the result to the Razorpay checkout
        checkout.onActivityResult(requestCode, resultCode, data);
    }

}