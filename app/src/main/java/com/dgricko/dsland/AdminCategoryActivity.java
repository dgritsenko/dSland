package com.dgricko.dsland;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity {

    private ImageView sportsProd, capProd, computerProd, shoesProd;
    private ImageView watchProd, tshirtsProd, bagsProd, bikesProd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        sportsProd = (ImageView)findViewById(R.id.product_sports);
        capProd = (ImageView)findViewById(R.id.product_cap);
        computerProd = (ImageView)findViewById(R.id.product_computer);
        shoesProd = (ImageView)findViewById(R.id.product_shoes);

        watchProd = (ImageView)findViewById(R.id.product_watch);
        tshirtsProd = (ImageView)findViewById(R.id.product_tshirt);
        bagsProd = (ImageView)findViewById(R.id.product_bags);
        bikesProd = (ImageView)findViewById(R.id.product_bike);


        sportsProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProductActivity.class);
                intent.putExtra("category", "sportsProd");
                startActivity(intent);
            }
        });
        capProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProductActivity.class);
                intent.putExtra("category", "capProd");
                startActivity(intent);
            }
        });
        computerProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProductActivity.class);
                intent.putExtra("category", "computerProd");
                startActivity(intent);
            }
        });
        shoesProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProductActivity.class);
                intent.putExtra("category", "shoesProd");
                startActivity(intent);
            }
        });

        watchProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProductActivity.class);
                intent.putExtra("category", "watchProd");
                startActivity(intent);
            }
        });
        tshirtsProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProductActivity.class);
                intent.putExtra("category", "tshirtsProd");
                startActivity(intent);
            }
        });
        bagsProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProductActivity.class);
                intent.putExtra("category", "bagsProd");
                startActivity(intent);
            }
        });
        bikesProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProductActivity.class);
                intent.putExtra("category", "bikesProd");
                startActivity(intent);
            }
        });
    }
}