package com.davidremington.infiltratr.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
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

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.landingPageImageView) ImageView landingPageBackgroundImage;
    @BindView(R.id.startButton) Button mStartButton;
    @BindView(R.id.titleTextView) TextView mTitleTextView;

    private static final String FONT = "Rustico.ttf";
    private static final String BACKGROUND_ROOT_URL = "http://i64.photobucket.com/albums/h164/hewhoiswithoutaname";
    private static final String BACKGROUND_IMAGE = "background_zpsz298orhf.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        Typeface myCustomFont = Typeface.createFromAsset(getAssets(), FONT);
        mTitleTextView.setTypeface(myCustomFont);

        Picasso.with(this)
                .load(getBackgroundUrl())
                .resize(800, 500)
                .centerCrop()
                .into(landingPageBackgroundImage);

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
