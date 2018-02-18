package io.keinix.timesync.reddit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.R;

public class ReplyPopUp {

    @BindView(R.id.replyBodyTextView) TextView BodyTextView;
    @BindView(R.id.replyAuthorTextView) TextView authorTextView;
    @BindView(R.id.replyEditText) EditText replyEditText;

    private Context mContext;
    private String mBody;
    private String mAuthor;
    private View mView;

    public ReplyPopUp(Context context, String author, String body) {
        mAuthor = author;
        mBody = body;
        mContext = context;
        mView = LayoutInflater.from(mContext).inflate(R.layout.pop_up_reply, null);
        ButterKnife.bind(mContext, mView);
    }

    public void bindView() {

    }

    public void show() {
        PopupWindow popupWindow = new PopupWindow(mView,
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(mView, 30, 15);
    }

}
