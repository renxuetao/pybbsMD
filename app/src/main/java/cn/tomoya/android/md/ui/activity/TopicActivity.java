package cn.tomoya.android.md.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.tomoya.android.md.R;
import cn.tomoya.android.md.model.api.ApiDefine;
import cn.tomoya.android.md.model.entity.Reply;
import cn.tomoya.android.md.model.entity.Topic;
import cn.tomoya.android.md.model.entity.TopicWithReply;
import cn.tomoya.android.md.model.util.EntityUtils;
import cn.tomoya.android.md.presenter.contract.ITopicPresenter;
import cn.tomoya.android.md.presenter.implement.TopicPresenter;
import cn.tomoya.android.md.ui.adapter.ReplyListAdapter;
import cn.tomoya.android.md.ui.base.StatusBarActivity;
import cn.tomoya.android.md.ui.dialog.CreateReplyDialog;
import cn.tomoya.android.md.ui.listener.DoubleClickBackToContentTopListener;
import cn.tomoya.android.md.ui.listener.NavigationFinishClickListener;
import cn.tomoya.android.md.ui.util.Navigator;
import cn.tomoya.android.md.ui.util.RefreshUtils;
import cn.tomoya.android.md.ui.util.ThemeUtils;
import cn.tomoya.android.md.ui.view.IBackToContentTopView;
import cn.tomoya.android.md.ui.view.ICreateReplyView;
import cn.tomoya.android.md.ui.view.ITopicView;
import cn.tomoya.android.md.ui.viewholder.TopicHeader;

public class TopicActivity extends StatusBarActivity implements ITopicView, IBackToContentTopView, SwipeRefreshLayout.OnRefreshListener, Toolbar.OnMenuItemClickListener {

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.refresh_layout)
    protected SwipeRefreshLayout refreshLayout;

    @BindView(R.id.list_view)
    protected ListView listView;

    @BindView(R.id.icon_no_data)
    protected View iconNoData;

    @BindView(R.id.fab_reply)
    protected FloatingActionButton fabReply;

    private String topicId;
    private Topic topic;

    private ICreateReplyView createReplyView;
    private TopicHeader header;
    private ReplyListAdapter adapter;

    private ITopicPresenter topicPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.configThemeBeforeOnCreate(this, R.style.AppThemeLight, R.style.AppThemeDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        ButterKnife.bind(this);

//        if (SettingShared.isShowTopicRenderCompatTip(this)) {
//            SettingShared.markShowTopicRenderCompatTip(this);
//            AlertDialogUtils.createBuilderWithAutoTheme(this)
//                    .setMessage(R.string.topic_render_compat_tip)
//                    .setPositiveButton(R.string.ok, null)
//                    .show();
//        }

        topicId = getIntent().getStringExtra(Navigator.TopicWithAutoCompat.EXTRA_TOPIC_ID);

        if (!TextUtils.isEmpty(getIntent().getStringExtra(Navigator.TopicWithAutoCompat.EXTRA_TOPIC))) {
            topic = EntityUtils.gson.fromJson(getIntent().getStringExtra(Navigator.TopicWithAutoCompat.EXTRA_TOPIC), Topic.class);
        }

        toolbar.setNavigationOnClickListener(new NavigationFinishClickListener(this));
        toolbar.inflateMenu(R.menu.topic);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setOnClickListener(new DoubleClickBackToContentTopListener(this));

        createReplyView = CreateReplyDialog.createWithAutoTheme(this, topicId, this);
        header = new TopicHeader(this, listView);
        header.updateViews(topic, false, 0);
        adapter = new ReplyListAdapter(this, createReplyView);
        listView.setAdapter(adapter);

        iconNoData.setVisibility(topic == null ? View.VISIBLE : View.GONE);

        fabReply.attachToListView(listView);

        topicPresenter = new TopicPresenter(this, this);

        RefreshUtils.init(refreshLayout, this);
        RefreshUtils.refresh(refreshLayout, this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                if (topic != null) {
                    Navigator.openShare(this, "???" + topic.getTitle() + "???\n" + ApiDefine.TOPIC_LINK_URL_PREFIX + topicId + "\n?????? ??????????????????");
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onRefresh() {
        topicPresenter.getTopicAsyncTask(topicId);
    }

    @OnClick(R.id.fab_reply)
    protected void onBtnReplyClick() {
        if (topic != null && LoginActivity.startForResultWithLoginCheck(this)) {
            createReplyView.showWindow();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginActivity.REQUEST_LOGIN && resultCode == RESULT_OK) {
            refreshLayout.setRefreshing(true);
            onRefresh();
        }
    }

    @Override
    public void onGetTopicOk(@NonNull TopicWithReply topic) {
        this.topic = topic.getTopic();
        header.updateViews(topic);
        adapter.setReplyList(topic.getReplies());
        adapter.notifyDataSetChanged();
        iconNoData.setVisibility(View.GONE);
    }

    @Override
    public void onGetTopicFinish() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void appendReplyAndUpdateViews(@NonNull Reply reply) {
        adapter.addReply(reply);
        adapter.notifyDataSetChanged();
        header.updateReplyCount(adapter.getReplyList().size());
        listView.smoothScrollToPosition(adapter.getReplyList().size());
    }

    @Override
    public void backToContentTop() {
        listView.setSelection(0);
    }

}
