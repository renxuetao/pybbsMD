package cn.tomoya.android.md.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.tomoya.android.md.BuildConfig;
import cn.tomoya.android.md.R;
import cn.tomoya.android.md.ui.base.StatusBarActivity;
import cn.tomoya.android.md.ui.listener.NavigationFinishClickListener;
import cn.tomoya.android.md.ui.util.Navigator;
import cn.tomoya.android.md.ui.util.ThemeUtils;

public class AboutActivity extends StatusBarActivity {

    public static final String VERSION_TEXT = BuildConfig.VERSION_NAME + "-build-" + BuildConfig.VERSION_CODE;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.tv_version)
    protected TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.configThemeBeforeOnCreate(this, R.style.AppThemeLight_FitsStatusBar, R.style.AppThemeDark_FitsStatusBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        toolbar.setNavigationOnClickListener(new NavigationFinishClickListener(this));

        tvVersion.setText(VERSION_TEXT);
    }

    @OnClick(R.id.btn_version)
    protected void onBtnVersionClick() {
        // nothing to do
    }

    @OnClick(R.id.btn_open_source_url)
    protected void onBtnOpenSourceUrlClick() {
        Navigator.openInBrowser(this, getString(R.string.open_source_url_content));
    }

    @OnClick(R.id.btn_about_cnode)
    protected void onBtnAboutCNodeClick() {
        Navigator.openInBrowser(this, getString(R.string.about_cnode_content));
    }

    @OnClick(R.id.btn_about_author)
    protected void onBtnAboutAuthorClick() {
        Navigator.openInBrowser(this, getString(R.string.about_author_content));
    }

    @OnClick(R.id.btn_open_in_market)
    protected void onBtnOpenInMarketClick() {
        Navigator.openInMarket(this);
    }

    @OnClick(R.id.btn_advice_feedback)
    protected void onBtnAdviceFeedbackClick() {
        Navigator.openEmail(
                this,
                "py2qiuse@gmail.com",
                "?????? pybbsMD-" + VERSION_TEXT + " ??????????????????",
                "???????????????Android " + Build.VERSION.RELEASE + " - " + Build.MANUFACTURER + " - " + Build.MODEL + "\n???????????????????????????????????????????????????\n\n"
        );
    }

    @OnClick(R.id.btn_open_source_license)
    protected void onBtnOpenSourceLicenseClick() {
//        startActivity(new Intent(this, LicenseActivity.class));
    }

}
