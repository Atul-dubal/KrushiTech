package apcoders.in.krushitech;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import es.dmoral.toasty.Toasty;

public class PhoneLoginActivity extends AppCompatActivity {

    Button get_otp_btn;
    TextInputEditText phone_Number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        get_otp_btn = findViewById(R.id.get_otp_btn);
        phone_Number = findViewById(R.id.phone_number);


        get_otp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone_number = "+91" + phone_Number.getText().toString();
                if (phone_number.length() == 13) {

                    Intent i = new Intent(PhoneLoginActivity.this, OTP_verification_Activity.class);
                    i.putExtra("phone_number", phone_number);
                    startActivity(i);
                } else {
                    Toasty.warning(PhoneLoginActivity.this, "Enter Valid Phone Number ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}