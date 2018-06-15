package com.example.spirit.androiddemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spirit.androiddemo.utils.ConstanceField;
import com.example.spirit.androiddemo.utils.Util;

public class ImageShowActivity extends Activity {
    private ImageView ivImage;
    private String title;
    private String path;
    private ImageView ivExit;
    private TextView tvTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show);

        initView();
        initVariable();
        initUI();
    }

    private void initView() {
        ivImage = findViewById(R.id.iv_image);
        ivExit = findViewById(R.id.iv_exit);
        tvTitle = findViewById(R.id.tv_title);
    }

    private void initVariable() {
        Intent intent = getIntent();
        title = intent.getStringExtra(ConstanceField.TOP_TITLE);
        path = intent.getStringExtra(ConstanceField.PATH);
    }

    private void initUI() {
        ivImage.setImageBitmap(Util.getBitMap(path));
        tvTitle.setText(title);
        ivExit.setColorFilter(Color.WHITE);
        ivExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
