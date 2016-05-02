package com.david_remington.infiltratr;

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

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.landingPageImageView) ImageView mLandingPageImageView;
    @Bind(R.id.startButton) Button mStartButton;
    @Bind(R.id.titleTextView) TextView mTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Typeface myCustomFont = Typeface.createFromAsset(getAssets(), "Rustico.ttf");
        mTitleTextView.setTypeface(myCustomFont);

        Picasso.with(this)
                .load("http://i64.photobucket.com/albums/h164/hewhoiswithoutaname/background_zpsz298orhf.jpg")
                .resize(800, 500)
                .centerCrop()
                .into(mLandingPageImageView);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }
}
