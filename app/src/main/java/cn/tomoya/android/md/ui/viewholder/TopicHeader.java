package cn.tomoya.android.md.ui.viewholder;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.tomoya.android.md.R;
import cn.tomoya.android.md.model.entity.Topic;
import cn.tomoya.android.md.model.entity.TopicWithReply;
import cn.tomoya.android.md.presenter.contract.ITopicHeaderPresenter;
import cn.tomoya.android.md.presenter.implement.TopicHeaderPresenter;
import cn.tomoya.android.md.ui.activity.LoginActivity;
import cn.tomoya.android.md.ui.activity.UserDetailActivity;
import cn.tomoya.android.md.ui.view.ITopicHeaderView;
import cn.tomoya.android.md.ui.widget.ContentWebView;
import cn.tomoya.android.md.util.FormatUtils;

public class TopicHeader implements ITopicHeaderView {

    @BindView(R.id.layout_content)
    protected ViewGroup layoutContent;

    @BindView(R.id.icon_good)
    protected View iconGood;

    @BindView(R.id.tv_title)
    protected TextView tvTitle;

    @BindView(R.id.img_avatar)
    protected ImageView imgAvatar;

    @BindView(R.id.ctv_tab)
    protected CheckedTextView ctvTab;

    @BindView(R.id.tv_login_name)
    protected TextView tvLoginName;

    @BindView(R.id.tv_create_time)
    protected TextView tvCreateTime;

    @BindView(R.id.tv_visit_count)
    protected TextView tvVisitCount;

    @BindView(R.id.btn_favorite)
    protected ImageView btnFavorite;

    @BindView(R.id.web_content)
    protected ContentWebView webContent;

    @BindView(R.id.layout_no_reply)
    protected ViewGroup layoutNoReply;

    @BindView(R.id.layout_reply_count)
    protected ViewGroup layoutReplyCount;

    @BindView(R.id.tv_reply_count)
    protected TextView tvReplyCount;

    private final Activity activity;
    private Topic topic;
    private boolean isCollect;

    private final ITopicHeaderPresenter topicHeaderPresenter;

    public TopicHeader(@NonNull Activity activity, @NonNull ListView listView) {
        this.activity = activity;
        LayoutInflater inflater = LayoutInflater.from(activity);
        View headerView = inflater.inflate(R.layout.header_topic, listView, false);
        ButterKnife.bind(this, headerView);
        listView.addHeaderView(headerView, null, false);
        this.topicHeaderPresenter = new TopicHeaderPresenter(activity, this);
    }

    @OnClick(R.id.img_avatar)
    protected void onBtnAvatarClick() {
        UserDetailActivity.startWithTransitionAnimation(activity, topic.getAuthor(), imgAvatar, topic.getAvatar());
    }

    @OnClick(R.id.btn_favorite)
    protected void onBtnFavoriteClick() {
        if (topic != null) {
            if (LoginActivity.startForResultWithLoginCheck(activity)) {
                if (isCollect) {
                    topicHeaderPresenter.decollectTopicAsyncTask(topic.getId());
                } else {
                    topicHeaderPresenter.collectTopicAsyncTask(topic.getId());
                }
            }
        }
    }

    public void updateViews(@Nullable Topic topic, boolean isCollect, int replyCount) {
        this.topic = topic;
        this.isCollect = isCollect;
        if (topic != null) {
            layoutContent.setVisibility(View.VISIBLE);
            iconGood.setVisibility(topic.isGood() ? View.VISIBLE : View.GONE);

            tvTitle.setText(topic.getTitle());
            Glide.with(activity).load(topic.getAvatar()).placeholder(R.drawable.image_placeholder).dontAnimate().into(imgAvatar);
            ctvTab.setText(topic.isTop() ? R.string.tab_top : topic.getTab().getNameId());
            ctvTab.setChecked(topic.isTop());
            tvLoginName.setText(topic.getAuthor());
            tvCreateTime.setText(FormatUtils.getRelativeTimeSpanString(topic.getInTime()) + "??????");
            tvVisitCount.setText(topic.getView() + "?????????");
            btnFavorite.setImageResource(isCollect ? R.drawable.ic_favorite_theme_24dp : R.drawable.ic_favorite_outline_grey600_24dp);

            // ??????????????????WebView??????????????????
            webContent.loadRenderedContent(topic.getContentHtml());

            updateReplyCount(replyCount);
        } else {
            layoutContent.setVisibility(View.GONE);
            iconGood.setVisibility(View.GONE);
        }
    }

    public void updateViews(@NonNull TopicWithReply topic) {
        updateViews(topic.getTopic(), topic.getCollect(), topic.getReplies().size());
    }

    public void updateReplyCount(int replyCount) {
        layoutNoReply.setVisibility(replyCount > 0 ? View.GONE : View.VISIBLE);
        layoutReplyCount.setVisibility(replyCount > 0 ? View.VISIBLE : View.GONE);
        tvReplyCount.setText(replyCount + "?????????");
    }

    @Override
    public void onCollectTopicOk() {
        isCollect = true;
        btnFavorite.setImageResource(R.drawable.ic_favorite_theme_24dp);
    }

    @Override
    public void onDecollectTopicOk() {
        isCollect = false;
        btnFavorite.setImageResource(R.drawable.ic_favorite_outline_grey600_24dp);
    }

}
