package io.xdag.xdagwallet.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import io.xdag.xdagwallet.BuildConfig;
import io.xdag.xdagwallet.R;
import me.drakeet.multitype.Items;
import me.drakeet.support.about.AbsAboutActivity;
import me.drakeet.support.about.Card;
import me.drakeet.support.about.Category;
import me.drakeet.support.about.License;

/**
 * created by lxm on 2018/7/26.
 */
public class AboutActivity extends AbsAboutActivity {

    @Override
    protected void onCreateHeader(
        @NonNull ImageView icon, @NonNull TextView slogan, @NonNull TextView version) {
        icon.setVisibility(View.GONE);
        slogan.setText(R.string.slogan);
        version.setText(String.format("v%s", BuildConfig.VERSION_NAME));
    }


    @Override
    protected void onTitleViewCreated(@NonNull CollapsingToolbarLayout collapsingToolbar) {
        super.onTitleViewCreated(collapsingToolbar);
        collapsingToolbar.setTitle(getString(R.string.about));
    }


    @Override protected void onItemsCreated(@NonNull Items items) {

        items.add(new Category("很高兴见到您"));
        items.add(new Card(getString(R.string.about_explain)));

        // Source code
        items.add(new Category("Source code"));
        items.add(new License("GitHub", "android-wallet", "", "https://github.com/XDagger/android-wallet"));

        // Developers
        items.add(new Category("Developers"));
        items.add(
            new License("BillLevesque", "Core Developer", "", "https://github.com/amazingMan2017"));
        items.add(new License("ssyijiu", "Developer & Designer", "", "https://github.com/ssyijiu"));

        // Open Source Licenses
        items.add(new Category("Open Source Licenses"));
        items.add(new License("butterknife", "JakeWharton", License.APACHE_2,
            "https://github.com/JakeWharton/butterknife"));
        items.add(new License("retrofit", "square", License.APACHE_2,
            "https://github.com/square/retrofit"));
        items.add(new License("okhttp", "square", License.APACHE_2,
            "https://github.com/square/okhttp"));
        items.add(new License("leakcanary", "square", License.APACHE_2,
            "https://github.com/square/leakcanary"));
        items.add(new License("RxJava", "ReactiveX", License.APACHE_2,
            "https://github.com/ReactiveX/RxJava"));
        items.add(new License("RxAndroid", "ReactiveX", License.APACHE_2,
            "https://github.com/ReactiveX/RxAndroid"));
        items.add(new License("EventBus", "greenrobot", License.APACHE_2,
            "https://github.com/greenrobot/EventBus"));
        items.add(new License("chuck", "jgilfelt", License.APACHE_2,
            "https://github.com/jgilfelt/chuck"));
        items.add(new License("zBarLibrary", "bertsir", License.APACHE_2,
            "https://github.com/bertsir/zBarLibary"));
        items.add(new License("BaseRecyclerViewAdapterHelper", "CymChad", License.APACHE_2,
            "https://github.com/CymChad/BaseRecyclerViewAdapterHelper"));
        items.add(new License("AndPermission", "yanzhenjie", License.APACHE_2,
            "https://github.com/yanzhenjie/AndPermission"));
        items.add(new License("Alerter", "Tapadoo", License.MIT,
            "https://github.com/Tapadoo/Alerter"));
        items.add(new License("ahbottomnavigation", "aurelhubert", License.APACHE_2,
            "https://github.com/aurelhubert/ahbottomnavigation"));
        items.add(new License("about-page", "PureWriter", License.APACHE_2,
            "https://github.com/PureWriter/about-page"));
        items.add(new License("rootbeer", "scottyab", License.APACHE_2,
            "https://github.com/scottyab/rootbeer"));
    }


    public static void start(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }
}
