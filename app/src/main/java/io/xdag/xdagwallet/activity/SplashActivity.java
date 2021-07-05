package io.xdag.xdagwallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import io.xdag.xdagwallet.R;


import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.wallet.WalletUtils;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnNewWallet = null;
    private Button btnImportWallet = null;
    //private SetPwdDialogFragment dialogSetPwd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setBackgroundDrawable(null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initData();
        initView();
    }

    // check wallet file is exist
    // TODO: check crc or md5 of wallet.data
    private void initData() {
        if(WalletUtils.loadWallet().exists()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

    private void initView() {
        ViewPager viewPager = findViewById(R.id.intro);
        if (viewPager != null) {
            viewPager.setPageTransformer(false, new DepthPageTransformer());
            viewPager.setAdapter(new IntroPagerAdapter());
        }

        btnNewWallet = findViewById(R.id.new_account_action);
        btnImportWallet = findViewById(R.id.import_account_action);
        btnNewWallet.setOnClickListener(this);
        btnImportWallet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.new_account_action:
                // TODO:最好闪屏页就创建钱包，不要在MainActivity里面创建
                Intent goNewAccount = new Intent(this,CreateWalletActivity.class);
                startActivity(goNewAccount);
                this.finish();
                break;
            case R.id.import_account_action:
//                Intent goImportAccount = new Intent(this,ImportWalletActivity.class);
//                startActivityForResult(goImportAccount,Constants.REQUEST_CODE_IMPORT_WALLET);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            Intent goNewMain = new Intent(this,MainActivity.class);
            startActivity(goNewMain);
            this.finish();
        }
    }

    private static class IntroPagerAdapter extends PagerAdapter {
        private int[] titles = new int[] {
                R.string.more_suggest,
                R.string.more_suggest,
                R.string.more_suggest,
                R.string.more_suggest,
        };
        private int[] messages = new int[] {
                R.string.more_suggest,
                R.string.more_suggest,
                R.string.more_suggest,
                R.string.more_suggest,
        };
        private int[] images = new int[] {
                R.mipmap.onboarding_lock,
                R.mipmap.onboarding_erc20,
                R.mipmap.onboarding_open_source,
                R.mipmap.onboarding_rocket
        };

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.layout_page_intro, container, false);
            ((TextView) view.findViewById(R.id.title)).setText(titles[position]);
            ((TextView) view.findViewById(R.id.message)).setText(messages[position]);
            ((ImageView) view.findViewById(R.id.img)).setImageResource(images[position]);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    private static class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
