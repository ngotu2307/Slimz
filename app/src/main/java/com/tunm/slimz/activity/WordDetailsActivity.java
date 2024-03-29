package com.tunm.slimz.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;
import com.tunm.slimz.R;
import com.tunm.slimz.activity.base.BaseActivity;
import com.tunm.slimz.adapter.ViewPagerAdapter;
import com.tunm.slimz.fragment.EngEngFragment;
import com.tunm.slimz.fragment.EngVietFragment;
import com.tunm.slimz.fragment.NoteFragment;
import com.tunm.slimz.fragment.SynonymFragment;
import com.tunm.slimz.fragment.TechnicalFragment;
import com.tunm.slimz.fragment.stack.WordStack;
import com.tunm.slimz.MyApp;

public class WordDetailsActivity extends BaseActivity implements View.OnClickListener {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private ImageButton btBack;
    private ImageView btStar, btStarYellow, btStarBlue, btStarPink;
    private TextView tvWordName;
    private String receivedWordName;

    private static final String[] arrSelectStarColor= {"yellow", "blue", "pink"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconics.init(getApplicationContext());
        Iconics.registerFont(new GoogleMaterial());

        tvWordName = findViewById(R.id.tvWordName);
        btBack = findViewById(R.id.btBack);
        btStar = findViewById(R.id.btStar);
        btStarYellow = findViewById(R.id.btStarYellow);
        btStarBlue = findViewById(R.id.btStarBlue);
        btStarPink = findViewById(R.id.btStarPink);
        btBack.setOnClickListener(this);
        btStar.setOnClickListener(this);
        btStarYellow.setOnClickListener(this);
        btStarBlue.setOnClickListener(this);
        btStarPink.setOnClickListener(this);

        Intent intent = getIntent();
        // From MainActivity
        receivedWordName = intent.getStringExtra("wordFromActivity");

        // From EngVietFragment
        if(receivedWordName == null) {
            receivedWordName = intent.getStringExtra(("wordFromFragment"));
            if (receivedWordName == null) {
                receivedWordName = intent.getStringExtra(("wordFromStack"));
            } else {
                WordStack.addToStack(receivedWordName);
            }
            Intent i = getIntent();
            i.putExtra("resendWord", receivedWordName);
        }
        if(receivedWordName != null) {
            MyApp.getDictionaryDB().addHistoryWord(this, receivedWordName);
            WordStack.addToStack(receivedWordName);
        }
        Log.v("wordstack", String.valueOf(WordStack.stackOfWords));

        checkWordFavorite(receivedWordName);

        tvWordName.setText(receivedWordName);

        mViewPager = findViewById(R.id.viewpager);
        setupViewPager(mViewPager);
        mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_word_details;
    }

    @Override
    protected void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        } else {
            Log.v(TAG, "getSupportActionBar null");
        }
    }

    @Override
    protected void loadAd() {
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void checkWordFavorite(String receivedWordName) {
        String isFavoriteWordColor = MyApp.getDictionaryDB().getColorFavoriteWordByName(this, receivedWordName);
        switch (isFavoriteWordColor) {
            case "noColor":
                btStar.setVisibility(View.VISIBLE);
                break;
            case "yellow":
                btStarYellow.setVisibility(View.VISIBLE);
                break;
            case "blue":
                btStarBlue.setVisibility(View.VISIBLE);
                break;
            case "pink":
                btStarPink.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFrag(new EngVietFragment(), "ENG - VIET");
        mViewPagerAdapter.addFrag(new TechnicalFragment(), "TECHNICAL");
        mViewPagerAdapter.addFrag(new SynonymFragment(), "SYNONYM");
        mViewPagerAdapter.addFrag(new EngEngFragment(), "ENG - ENG");
        mViewPagerAdapter.addFrag(new NoteFragment(), "NOTE");
        viewPager.setAdapter(mViewPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        int stackSize = WordStack.stackOfWords.size();
        if(stackSize > 1) {
            Intent intent = new Intent(this, WordDetailsActivity.class);
            WordStack.stackOfWords.pop();
            intent.putExtra("wordFromStack", WordStack.stackOfWords.peek());
            startActivity(intent);
        } else if (stackSize == 1) {
            WordStack.stackOfWords.pop();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btBack:
                onBackPressed();
                break;
            case R.id.btStar:
                openDialogColor();
                break;
            case R.id.btStarYellow:
                btStar.setVisibility(View.VISIBLE);
                btStarYellow.setVisibility(View.GONE);
                MyApp.getDictionaryDB().removeFavoriteWord(this, receivedWordName);
                break;
            case R.id.btStarBlue:
                btStar.setVisibility(View.VISIBLE);
                btStarBlue.setVisibility(View.GONE);

                break;
            case R.id.btStarPink:
                btStar.setVisibility(View.VISIBLE);
                btStarPink.setVisibility(View.GONE);
                break;
        }
    }

    private void openDialogColor() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_select_favorite);
        dialog.show();
        final ImageButton btStarYellowDialog = dialog.findViewById(R.id.btStarYellow);
        final ImageButton btStarBlueDialog = dialog.findViewById(R.id.btStarBlue);
        final ImageButton btStarPinkDialog = dialog.findViewById(R.id.btStarPink);
        btStarYellowDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btStar.setVisibility(View.GONE);
                btStarBlue.setVisibility(View.GONE);
                btStarPink.setVisibility(View.GONE);
                btStarYellow.setVisibility(View.VISIBLE);
                MyApp.getDictionaryDB().addFavoriteWord(WordDetailsActivity.this, receivedWordName, arrSelectStarColor[0]);
                dialog.dismiss();
            }
        });
        btStarBlueDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btStar.setVisibility(View.GONE);
                btStarYellow.setVisibility(View.GONE);
                btStarPink.setVisibility(View.GONE);
                btStarBlue.setVisibility(View.VISIBLE);
                MyApp.getDictionaryDB().addFavoriteWord(WordDetailsActivity.this, receivedWordName, arrSelectStarColor[1]);
                dialog.dismiss();
            }
        });
        btStarPinkDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btStar.setVisibility(View.GONE);
                btStarYellow.setVisibility(View.GONE);
                btStarBlue.setVisibility(View.GONE);
                btStarPink.setVisibility(View.VISIBLE);
                MyApp.getDictionaryDB().addFavoriteWord(WordDetailsActivity.this, receivedWordName, arrSelectStarColor[2]);
                dialog.dismiss();
            }
        });
    }
}

