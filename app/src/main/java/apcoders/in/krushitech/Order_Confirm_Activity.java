package apcoders.in.krushitech;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class Order_Confirm_Activity extends AppCompatActivity {
    Button continueShoppingBtn;
    TextView viewOrdersText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_order_confirm);

        LottieAnimationView check_mark_animation = findViewById(R.id.check_mark_animation);
        LottieAnimationView order_confirm_animation = findViewById(R.id.orer_confirm_animation);
        LottieAnimationView crackers_animation = findViewById(R.id.crackers_animation);

        continueShoppingBtn = findViewById(R.id.continueShoppingBtn);
        viewOrdersText = findViewById(R.id.viewOrdersText);

        viewOrdersText.setOnClickListener(v -> {
            Intent i = new Intent(Order_Confirm_Activity.this,MainActivity.class);
            i.putExtra("display_fragment","my_orders");
            startActivity(i);
            finish();
        });

        continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Order_Confirm_Activity.this, MainActivity.class));
                finish();
            }
        });


        crackers_animation.playAnimation();
        check_mark_animation.playAnimation();
        order_confirm_animation.playAnimation();
    }
}