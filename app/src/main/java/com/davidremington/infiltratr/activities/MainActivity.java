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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.davidremington.infiltratr.R;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.landingPageImageView) ImageView mLandingPageImageView;
    @Bind(R.id.startButton) Button mStartButton;
    @Bind(R.id.titleTextView) TextView mTitleTextView;

    private static final String FONT = "Rustico.ttf";
    private static final String BACKGROUND_ROOT_URL = "http://i64.photobucket.com/albums/h164/hewhoiswithoutaname";
    private static final String BACKGROUND_IMAGE = "background_zpsz298orhf.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Typeface myCustomFont = Typeface.createFromAsset(getAssets(), FONT);
        mTitleTextView.setTypeface(myCustomFont);

        Picasso.with(this)
                .load(getBackgroundUrl())
                .resize(800, 500)
                .centerCrop()
                .into(mLandingPageImageView);

    }

    @OnClick(R.id.startButton)
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    private String getBackgroundUrl() {
        return String.format("%s/%s", BACKGROUND_ROOT_URL, BACKGROUND_IMAGE);
    }
}
