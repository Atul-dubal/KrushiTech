package apcoders.in.krushitech;



import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import apcoders.in.krushitech.adapters.ProductManagementAdapter;
import apcoders.in.krushitech.models.ProductModel;
import apcoders.in.krushitech.utils.FetchAllProducts;

public class ManageEquipmentsActivity extends AppCompatActivity {

    RecyclerView farmer_current_products;
    Toolbar toolbar;
    FloatingActionButton new_equipment_upload_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manage_equipments);

        farmer_current_products = findViewById(R.id.farmer_current_products);
        new_equipment_upload_btn = findViewById(R.id.new_equipment_upload_btn);
        toolbar = findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }


        new_equipment_upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManageEquipmentsActivity.this, Upload_Product_Activity.class));
            }
        });

        fetchProducts();

    }


    private void fetchProducts() {
        FetchAllProducts.FetchByProductsOfFarmers(new FetchAllProducts.FetchProductDataCallback() {
            @Override
            public void onCallback(ArrayList<ProductModel> ProductDataList) {
                farmer_current_products.setLayoutManager(new LinearLayoutManager(ManageEquipmentsActivity.this));
                ProductManagementAdapter adapter = new ProductManagementAdapter(ManageEquipmentsActivity.this, ProductDataList, R.layout.shopping_cart_product_item_layout);
                farmer_current_products.setAdapter(adapter);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchProducts();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}