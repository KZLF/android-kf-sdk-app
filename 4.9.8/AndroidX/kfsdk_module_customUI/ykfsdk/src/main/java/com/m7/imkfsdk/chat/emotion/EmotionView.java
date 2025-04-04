package com.m7.imkfsdk.chat.emotion;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 编码参考 http://www.oicqzone.com/tool/emoji/
 * Created by yummyLau on 18-7-11
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class EmotionView extends GridView {

    private static int sNumColumns = 0;
    private static int sNumRows = 0;
    private static int sPadding = 0;
    private static int sEmotionSize = 0;
    private final EditText mEditText;

    public static int calSizeForContainEmotion(Context context, int width, int height) {
        sPadding =  DensityUtil.dp2px(5f);
        sEmotionSize = DensityUtil.dp2px(50f);
        sNumColumns = width / sEmotionSize;
        sNumRows = height / sEmotionSize;
        return sNumColumns * sNumRows;
    }

    public EmotionView(Context context, EditText editText) {
        super(context);
        this.mEditText = editText;
    }

    public void buildEmotions(final List<Emotion> data) {
        setVerticalScrollBarEnabled(false);
        setNumColumns(sNumColumns);
        setPadding(sPadding, sPadding, sPadding, sPadding+DensityUtil.dp2px(50f));
        setClipToPadding(false);
        setAdapter(new EmotionAdapter(getContext(), data));
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Emotion emotion = data.get(position);
                int start = mEditText.getSelectionStart();
                Editable editable = mEditText.getEditableText();
                Spannable emotionSpannable = EmojiSpanBuilder.buildEmotionSpannable(getContext(), emotion.text);
                editable.insert(start, emotionSpannable);
            }
        });
    }

    public static class EmotionAdapter extends BaseAdapter {

        public List<Emotion> mEmotions;
        private final Context mContext;

        public EmotionAdapter(Context context, List<Emotion> emotions) {
            if (emotions == null) {
                emotions = new ArrayList<>();
            }
            mEmotions = emotions;
            mContext = context;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.ykfsdk_vh_emotion_item_layout, parent, false);
            }
            ImageView imageView = view.findViewById(R.id.image);
            int resID = mContext.getResources().getIdentifier(((Emotion)getItem(position)).drawableRes,
                    "drawable", mContext.getPackageName());
            imageView.setImageResource(resID);
            return view;
        }

        @Override
        public int getCount() {
            return mEmotions.size();
        }

        @Override
        public Object getItem(int position) {
            return mEmotions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

    }
}
