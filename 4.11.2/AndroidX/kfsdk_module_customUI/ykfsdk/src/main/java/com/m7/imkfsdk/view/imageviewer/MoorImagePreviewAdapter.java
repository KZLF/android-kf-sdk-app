package com.m7.imkfsdk.view.imageviewer;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.model.MoorImageInfoBean;
import com.m7.imkfsdk.utils.MoorImageUtil;
import com.m7.imkfsdk.view.imageviewer.listener.IMoorSimpleOnImageEventListener;
import com.m7.imkfsdk.view.imageviewer.photoview.MoorPhotoView;
import com.m7.imkfsdk.view.imageviewer.subscaleview.MoorSubsamplingScaleImageView;
import com.moor.imkf.IMChatManager;
import com.moor.imkf.lib.utils.MoorLogUtils;
import com.moor.imkf.listener.IMoorImageLoaderListener;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author SherlockHolmes
 */
public class MoorImagePreviewAdapter extends PagerAdapter {

    private static final String TAG = "MoorImagePreviewAdapter";
    private final AppCompatActivity activity;
    private final List<MoorImageInfoBean> imageInfo;
    private HashMap<String, MoorSubsamplingScaleImageView> imageHashMap = new HashMap<>();
    private HashMap<String, MoorPhotoView> imageGifHashMap = new HashMap<>();
    private HashMap<Integer, View> imageViewHashMap = new HashMap<>();

    public MoorImagePreviewAdapter(AppCompatActivity activity, @NonNull List<MoorImageInfoBean> imageInfo) {
        super();
        this.imageInfo = imageInfo;
        this.activity = activity;
    }

    public void closePage() {
        try {
            if (imageHashMap != null && imageHashMap.size() > 0) {
                for (Object o : imageHashMap.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    if (entry != null && entry.getValue() != null) {
                        ((MoorSubsamplingScaleImageView) entry.getValue()).destroyDrawingCache();
                        ((MoorSubsamplingScaleImageView) entry.getValue()).recycle();
                    }
                }
                imageHashMap.clear();
                imageHashMap = null;
            }
            if (imageGifHashMap != null && imageGifHashMap.size() > 0) {
                for (Object o : imageGifHashMap.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    if (entry != null && entry.getValue() != null) {
                        ((MoorPhotoView) entry.getValue()).destroyDrawingCache();
                        ((MoorPhotoView) entry.getValue()).setImageBitmap(null);
                    }
                }
                imageGifHashMap.clear();
                imageGifHashMap = null;
            }
            if (imageViewHashMap != null && imageViewHashMap.size() > 0) {
                imageViewHashMap.clear();
                imageViewHashMap = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return imageInfo.size();
    }

    public HashMap<Integer, View> getImageViewHashMap() {
        return imageViewHashMap;
    }

    @SuppressLint("CheckResult")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        if (activity == null) {
            return container;
        }
        View convertView = View.inflate(activity, R.layout.ykfsdk_item_photoview, null);
        final ProgressBar progressBar = convertView.findViewById(R.id.progress_view);
//        final MoorFingerDragHelper fingerDragHelper = convertView.findViewById(R.id.fingerDragHelper);
        final MoorSubsamplingScaleImageView imageView = convertView.findViewById(R.id.photo_view);
        final MoorPhotoView imageGif = convertView.findViewById(R.id.gif_view);

        final MoorImageInfoBean info = this.imageInfo.get(position);
        String imageUrl = info.getPath();

        imageView.setMinimumScaleType(MoorSubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE);
        imageView.setDoubleTapZoomStyle(MoorSubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        imageView.setDoubleTapZoomDuration(MoorImagePreview.getInstance().getZoomTransitionDuration());
        imageView.setMinScale(MoorImagePreview.getInstance().getMinScale());
        imageView.setMaxScale(MoorImagePreview.getInstance().getMaxScale());
        imageView.setDoubleTapZoomScale(MoorImagePreview.getInstance().getMediumScale());

        imageGif.setZoomTransitionDuration(MoorImagePreview.getInstance().getZoomTransitionDuration());
        imageGif.setMinimumScale(MoorImagePreview.getInstance().getMinScale());
        imageGif.setMaximumScale(MoorImagePreview.getInstance().getMaxScale());
        imageGif.setScaleType(ImageView.ScaleType.FIT_CENTER);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MoorImagePreview.getInstance().isEnableClickClose()) {
                    activity.onBackPressed();
                }
                if (MoorImagePreview.getInstance().getBigImageClickListener() != null) {
                    MoorImagePreview.getInstance().getBigImageClickListener().onClick(activity, v, position);
                }
            }
        });
        imageGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MoorImagePreview.getInstance().isEnableClickClose()) {
                    activity.onBackPressed();
                }
                if (MoorImagePreview.getInstance().getBigImageClickListener() != null) {
                    MoorImagePreview.getInstance().getBigImageClickListener().onClick(activity, v, position);
                }
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (MoorImagePreview.getInstance().getBigImageLongClickListener() != null) {
                    return MoorImagePreview.getInstance().getBigImageLongClickListener().onLongClick(activity, v, position);
                }
                return false;
            }
        });
        imageGif.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (MoorImagePreview.getInstance().getBigImageLongClickListener() != null) {
                    return MoorImagePreview.getInstance().getBigImageLongClickListener().onLongClick(activity, v, position);
                }
                return false;
            }
        });

//        if (activity instanceof MoorImagePreviewActivity) {
//            ((MoorImagePreviewActivity) activity).setAlpha(1);
//        }
        if (!MoorImageUtil.isGifImageWithMime(imageUrl, imageUrl)) {
            imageView.setVisibility(View.VISIBLE);
            imageGif.setVisibility(View.GONE);
            imageViewHashMap.put(position, imageView);
        } else {
            imageView.setVisibility(View.GONE);
            imageGif.setVisibility(View.VISIBLE);
            imageViewHashMap.put(position, imageGif);
        }

        imageGifHashMap.remove(imageUrl);
        imageGifHashMap.put(imageUrl, imageGif);

        imageHashMap.remove(imageUrl);
        imageHashMap.put(imageUrl, imageView);


        imageUrl = imageUrl.trim();
        MoorLogUtils.d(imageUrl);
        // 显示加载圈圈
        progressBar.setVisibility(View.VISIBLE);
//        if (info.getFrom().equals(MoorChatMsgType.MSG_TYPE_RECEIVED)) {
//            loadNetImage(position, imageUrl, progressBar, imageView, imageGif);
//        } else {
//            loadNativeImage(position, imageUrl, progressBar, imageView, imageGif);
//        }
        if (imageUrl.startsWith("http")) {
            loadNetImage(position, imageUrl, progressBar, imageView, imageGif);
        } else {
            loadNativeImage(position, imageUrl, progressBar, imageView, imageGif);
        }

        container.addView(convertView);
        return convertView;
    }

    private void loadNetImage(final int position, final String url, final ProgressBar progressBar, final MoorSubsamplingScaleImageView imageView, MoorPhotoView imageGif) {
        if (!MoorImageUtil.isGifImageWithMime(url, url)) {


            if (IMChatManager.getInstance().getImageLoader() != null) {
                IMChatManager.getInstance().getImageLoader().loadImage(false, true, url,
                        null, 0, 0, 0, null, null, new IMoorImageLoaderListener() {
                            @Override
                            public void onLoadStarted(@Nullable Drawable placeholder) {

                            }

                            @Override
                            public void onLoadComplete(@NonNull Bitmap bitmap) {

                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {

                            }

                            @Override
                            public void onResourceReady(@NonNull final File resource) {
                                try {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            MoorImageSource origin = MoorImageSource.uri(resource.getAbsolutePath());
                                            int widOrigin = MoorImageUtil.getWidthHeight(resource.getAbsolutePath())[0];
                                            int heiOrigin = MoorImageUtil.getWidthHeight(resource.getAbsolutePath())[1];

                                            origin.dimensions(widOrigin, heiOrigin);
                                            setImageSpec(resource.getAbsolutePath(), imageView);
                                            imageView.setOrientation(MoorSubsamplingScaleImageView.ORIENTATION_USE_EXIF);
                                            MoorImageSource imageSource = MoorImageSource.uri(Uri.fromFile(resource));
                                            if (MoorImageUtil.isBmpImageWithMime(resource.getAbsolutePath(), resource.getAbsolutePath())) {
                                                imageSource.tilingDisabled();
                                            }
                                            imageView.setImage(origin);
                                            imageView.setOnImageEventListener(new IMoorSimpleOnImageEventListener() {
                                                @Override
                                                public void onReady() {
                                                    progressBar.setVisibility(View.VISIBLE);
                                                }

                                                @Override
                                                public void onImageLoaded() {
                                                    super.onImageLoaded();
                                                    imageInfo.get(position).setStartScale(imageView.getScale());
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            });
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            } else {
                MoorLogUtils.eTag("ImageLoader", "ImageLoader is null");
            }


        } else {

            if (IMChatManager.getInstance().getImageLoader() != null) {
                IMChatManager.getInstance().getImageLoader().loadImage(true, false, url,
                        imageGif, 0, 0, 0, null, null, null);
            } else {
                MoorLogUtils.eTag("ImageLoader", "ImageLoader is null");
            }


            progressBar.setVisibility(View.GONE);
        }

    }

    private void loadNativeImage(final int position, final String url, final ProgressBar progressBar, final MoorSubsamplingScaleImageView imageView, MoorPhotoView imageGif) {
        if (!MoorImageUtil.isGifImageWithMime(url, url)) {


            if (IMChatManager.getInstance().getImageLoader() != null) {
                IMChatManager.getInstance().getImageLoader().loadImage(false, false, url,
                        null, 0, 0, 0, null, null, new IMoorImageLoaderListener() {
                            @Override
                            public void onLoadStarted(@Nullable Drawable placeholder) {

                            }

                            @Override
                            public void onLoadComplete(@NonNull Bitmap bitmap) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        MoorImageSource origin = MoorImageSource.uri(url);
                                        int widOrigin = MoorImageUtil.getWidthHeight(url)[0];
                                        int heiOrigin = MoorImageUtil.getWidthHeight(url)[1];

                                        origin.dimensions(widOrigin, heiOrigin);
                                        setImageSpec(url, imageView);
                                        imageView.setOrientation(MoorSubsamplingScaleImageView.ORIENTATION_USE_EXIF);
                                        MoorImageSource imageSource = MoorImageSource.uri(Uri.fromFile(new File(url)));
                                        if (MoorImageUtil.isBmpImageWithMime(url, url)) {
                                            imageSource.tilingDisabled();
                                        }
                                        imageView.setImage(origin);

                                        imageView.setOnImageEventListener(new IMoorSimpleOnImageEventListener() {
                                            @Override
                                            public void onReady() {
                                                progressBar.setVisibility(View.VISIBLE);
                                            }

                                            @Override
                                            public void onImageLoaded() {
                                                super.onImageLoaded();
                                                imageInfo.get(position).setStartScale(imageView.getScale());
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                });
                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {

                            }

                            @Override
                            public void onResourceReady(@NonNull File resource) {

                            }
                        });
            } else {
                MoorLogUtils.eTag("ImageLoader", "ImageLoader is null");
            }
        } else {

            if (IMChatManager.getInstance().getImageLoader() != null) {
                IMChatManager.getInstance().getImageLoader().loadImage(true, false, url,
                        imageGif, 0, 0, 0, null, null, null);
            } else {
                MoorLogUtils.eTag("ImageLoader", "ImageLoader is null");
            }

            progressBar.setVisibility(View.GONE);
        }

    }

    private void setImageSpec(final String imagePath, final MoorSubsamplingScaleImageView imageView) {
        boolean isLongImage = MoorImageUtil.isLongImage(activity, imagePath);
        if (isLongImage) {
            imageView.setMinimumScaleType(MoorSubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE);
            imageView.setMinScale(MoorImageUtil.getLongImageMinScale(activity, imagePath));
            imageView.setMaxScale(MoorImageUtil.getLongImageMaxScale(activity, imagePath));
            imageView.setDoubleTapZoomScale(MoorImagePreview.getInstance().getMaxScale());
        } else {
            boolean isWideImage = MoorImageUtil.isWideImage(activity, imagePath);
            boolean isSmallImage = MoorImageUtil.isSmallImage(activity, imagePath);
            if (isWideImage) {
                imageView.setMinimumScaleType(MoorSubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE);
                imageView.setMinScale(MoorImagePreview.getInstance().getMinScale());
                imageView.setMaxScale(MoorImageUtil.getWideImageDoubleScale(activity, imagePath));
                imageView.setDoubleTapZoomScale(MoorImagePreview.getInstance().getMaxScale());
            } else if (isSmallImage) {
                imageView.setMinimumScaleType(MoorSubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
                imageView.setMinScale(MoorImageUtil.getSmallImageMinScale(activity, imagePath));
                imageView.setMaxScale(MoorImageUtil.getSmallImageMaxScale(activity, imagePath));
                imageView.setDoubleTapZoomScale(MoorImageUtil.getSmallImageMaxScale(activity, imagePath));
            } else {
                imageView.setMinimumScaleType(MoorSubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE);
                imageView.setMinScale(MoorImagePreview.getInstance().getMinScale());
                imageView.setMaxScale(MoorImagePreview.getInstance().getMaxScale());
                imageView.setDoubleTapZoomScale(MoorImagePreview.getInstance().getMediumScale());
            }
        }
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull final Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        String originUrl = imageInfo.get(position).getPath();
        try {
            if (imageHashMap != null) {
                MoorSubsamplingScaleImageView imageViewDragClose = imageHashMap.get(originUrl);
                if (imageViewDragClose != null) {
                    imageViewDragClose.resetScaleAndCenter();
                    imageViewDragClose.destroyDrawingCache();
                    imageViewDragClose.recycle();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (imageGifHashMap != null) {
                MoorPhotoView photoView = imageGifHashMap.get(originUrl);
                if (photoView != null) {
                    photoView.destroyDrawingCache();
                    photoView.setImageBitmap(null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (imageViewHashMap != null) {
                View view = imageViewHashMap.get(position);
                if (view != null) {
                    if (view instanceof MoorSubsamplingScaleImageView) {
                        ((MoorSubsamplingScaleImageView) view).resetScaleAndCenter();
                        view.destroyDrawingCache();
                        ((MoorSubsamplingScaleImageView) view).recycle();
                    } else if (view instanceof MoorPhotoView) {
                        view.destroyDrawingCache();
                        ((MoorPhotoView) view).setImageBitmap(null);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}