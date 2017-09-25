package com.davidremington.infiltratr.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.davidremington.infiltratr.R;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.loginPageImageView) ImageView loginPageBackgroundImage;
    @BindView(R.id.startButton) Button startButton;
    @BindView(R.id.titleTextView) TextView titleText;

    private static final String TITLE_FONT = "Rustico.ttf";
    private static final String BACKGROUND_ROOT_URL = "http://i64.photobucket.com/albums/h164/hewhoiswithoutaname";
    private static final String BACKGROUND_IMAGE = "background_zpsz298orhf.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        Typeface titleTextFont = Typeface.createFromAsset(getAssets(), TITLE_FONT);
        titleText.setTypeface(titleTextFont);

        Picasso.with(this)
                .load(getBackgroundUrl())
                .resize(800, 500)
                .centerCrop()
                .into(loginPageBackgroundImage);

    }

    @OnClick(R.id.startButton)
    public void onClick(View view) {
        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    private String getBackgroundUrl() {
        return String.format("%s/%s", BACKGROUND_ROOT_URL, BACKGROUND_IMAGE);
    }
}
