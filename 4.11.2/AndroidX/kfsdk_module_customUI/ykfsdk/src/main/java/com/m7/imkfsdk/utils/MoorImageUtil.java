package com.m7.imkfsdk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Log;

import com.m7.imkfsdk.chat.model.MoorImageInfoBean;
import com.m7.imkfsdk.chat.model.MoorImageType;
import com.moor.imkf.lib.utils.MoorDensityUtil;

import java.io.IOException;


/**
 * <pre>
 *     @author : Trial
 *     @time   : 2/4/21
 *     @desc   :
 *     @version: 1.0
 * </pre>
 */
public class MoorImageUtil {

    private static final String TAG = "ImageUtil";
    private static final int MOOR_IMAGEVIEW_MAX = DensityUtil.dp2px(160);//会话图片消息默认宽高
    private static final int MOOR_IMAGEVIEW_MIN = DensityUtil.dp2px(80);//会话图片消息最小宽高


    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation =
                    exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    degree = 0;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    public static int getOrientation(String imagePath) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(imagePath);
            int orientation =
                    exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int[] getWidthHeight(String imagePath) {
        if (imagePath.isEmpty()) {
            return new int[]{0, 0};
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            Bitmap originBitmap = BitmapFactory.decodeFile(imagePath, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 使用第一种方式获取原始图片的宽高
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

        // 使用第二种方式获取原始图片的宽高
        if (srcHeight == -1 || srcWidth == -1) {
            try {
                ExifInterface exifInterface = new ExifInterface(imagePath);
                srcHeight =
                        exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.ORIENTATION_NORMAL);
                srcWidth =
                        exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.ORIENTATION_NORMAL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 使用第三种方式获取原始图片的宽高
        if (srcWidth <= 0 || srcHeight <= 0) {
            Bitmap bitmap2 = BitmapFactory.decodeFile(imagePath);
            if (bitmap2 != null) {
                srcWidth = bitmap2.getWidth();
                srcHeight = bitmap2.getHeight();
                try {
                    if (!bitmap2.isRecycled()) {
                        bitmap2.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        int orient = getOrientation(imagePath);
        if (orient == 90 || orient == 270) {
            return new int[]{srcHeight, srcWidth};
        }
        return new int[]{srcWidth, srcHeight};
    }

    public static boolean isLongImage(Context context, String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        float w = wh[0];
        float h = wh[1];
        float imageRatio = (h / w);
        float phoneRatio = MoorDensityUtil.getPhoneRatio(context.getApplicationContext()) + 0.1F;
//        boolean isLongImage = (w > 0 && h > 0) && (h > w) && (imageRatio >= phoneRatio);
        boolean isLongImage = (w > 0 && h > 0) && (h > w) && (imageRatio >= 2);
        return isLongImage;
    }

    public static boolean isWideImage(Context context, String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        float w = wh[0];
        float h = wh[1];
        float imageRatio = (w / h);
        //float phoneRatio = MoorPhoneUtil.getPhoneRatio(context.getApplicationContext()) + 0.1F;
        boolean isWideImage = (w > 0 && h > 0) && (w > h) && (imageRatio >= 2);
        return isWideImage;
    }

    public static boolean isSmallImage(Context context, String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        boolean isSmallImage = wh[0] < MoorDensityUtil.getScreenWidth(context.getApplicationContext());
        return isSmallImage;
    }

    public static float getLongImageMinScale(Context context, String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        float imageWid = wh[0];
        float phoneWid = MoorDensityUtil.getScreenWidth(context.getApplicationContext());
        return phoneWid / imageWid;
    }

    public static float getLongImageMaxScale(Context context, String imagePath) {
        return getLongImageMinScale(context, imagePath) * 2;
    }
    public static float getLongImageDoubleScale(Context context, String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        float imageWid = wh[0];
        float phoneWid = MoorDensityUtil.getScreenWidth(context.getApplicationContext());
        return phoneWid / imageWid;
    }
    public static float getWideImageDoubleScale(Context context, String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        float imageHei = wh[1];
        float phoneHei = MoorDensityUtil.getScreenHeight(context.getApplicationContext());
        return phoneHei / imageHei;
    }

    public static float getSmallImageMinScale(Context context, String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        float imageWid = wh[0];
        float phoneWid = MoorDensityUtil.getScreenWidth(context.getApplicationContext());
        return phoneWid / imageWid;
    }

    public static float getSmallImageMaxScale(Context context, String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        float imageWid = wh[0];
        float phoneWid = MoorDensityUtil.getScreenWidth(context.getApplicationContext());
        return phoneWid * 2 / imageWid;
    }

    public static Bitmap getImageBitmap(String srcPath, int degree) {
        boolean isOOM = false;
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        float be = 1;
        newOpts.inSampleSize = (int) be;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        newOpts.inDither = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        try {
            bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        } catch (OutOfMemoryError e) {
            isOOM = true;
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            isOOM = true;
            Runtime.getRuntime().gc();
        }
        if (isOOM) {
            try {
                bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
            } catch (Exception e) {
                newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
                bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
            }
        }
        if (bitmap != null) {
            if (degree == 90) {
                degree += 180;
            }
            bitmap = rotateBitmapByDegree(bitmap, degree);
            int ttHeight = (1080 * bitmap.getHeight() / bitmap.getWidth());
            if (bitmap.getWidth() >= 1080) {
                bitmap = zoomBitmap(bitmap, 1080, ttHeight);
            }
        }
        return bitmap;
    }

    private static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    public static String getImageTypeWithMime(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        String type = options.outMimeType;
        Log.d(TAG, "getImageTypeWithMime: type1 = " + type);
        // ”image/png”、”image/jpeg”、”image/gif”
        if (TextUtils.isEmpty(type)) {
            type = "";
        } else {
            type = type.substring(6);
        }
        Log.d(TAG, "getImageTypeWithMime: type2 = " + type);
        return type;
    }

    public static boolean isPngImageWithMime(String url, String path) {
        return "png".equalsIgnoreCase(getImageTypeWithMime(path)) || url.toLowerCase().endsWith("png");
    }

    public static boolean isJpegImageWithMime(String url, String path) {
        return "jpeg".equalsIgnoreCase(getImageTypeWithMime(path)) || "jpg".equalsIgnoreCase(getImageTypeWithMime(path))
                || url.toLowerCase().endsWith("jpeg") || url.toLowerCase().endsWith("jpg");
    }

    public static boolean isBmpImageWithMime(String url, String path) {
        return "bmp".equalsIgnoreCase(getImageTypeWithMime(path)) || url.toLowerCase().endsWith("bmp");
    }

    public static boolean isGifImageWithMime(String url, String path) {
        return "gif".equalsIgnoreCase(getImageTypeWithMime(path)) || url.toLowerCase().endsWith("gif");
    }

    public static boolean isNullImageWithMime(String url, String path) {
        return "".equalsIgnoreCase(getImageTypeWithMime(path)) || url.toLowerCase().endsWith("");
    }

    public static boolean isStandardImage(String url, String path) {
        return isJpegImageWithMime(url, path) || isPngImageWithMime(url, path) || isBmpImageWithMime(url, path) || isNullImageWithMime(url, path);
    }

    /**
     * 获取图片宽高
     *
     * @param path
     * @return
     */
    public static int[] getImageWidthHeight(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        int outWidth = options.outWidth;
        int outHeight = options.outHeight;

        ExifInterface exif = null;
        int degree = 0; // 图片旋转角度
        try {
            exif = new ExifInterface(path);
            if (exif != null) {
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

                if (orientation != -1) {
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            degree = 90;
                            outWidth = options.outHeight;
                            outHeight = options.outWidth;
                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_180:
//                            degree = 180;
//                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            degree = 270;
                            outWidth = options.outHeight;
                            outHeight = options.outWidth;
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return new int[]{outWidth, outHeight};
    }

    /**
     * 根据图片宽高比例，返回应该设置的ImageView布局大小及真正的比例值
     * 图片的宽高以3:1的为准，大于则设置另一边为最小值
     *
     * @param bitmapW 图片的宽
     * @param bitmapH 图片的高
     * @return
     */

    public static MoorImageInfoBean resizeImageView(float bitmapW, float bitmapH) {
        MoorImageInfoBean imageInfoBean = new MoorImageInfoBean();
        imageInfoBean.setLayoutW(0);
        imageInfoBean.setLayoutH(0);
        imageInfoBean.setRealSize(0);
        imageInfoBean.setType(MoorImageType.COMMON);

        //方图
        if (Math.abs(bitmapW - bitmapH) == 0) {
            imageInfoBean.setLayoutW(MOOR_IMAGEVIEW_MAX);
            imageInfoBean.setLayoutH(MOOR_IMAGEVIEW_MAX);
            imageInfoBean.setType(MoorImageType.COMMON);
            return imageInfoBean;
        }

        //横图
        if (bitmapW > bitmapH) {
            imageInfoBean.setLayoutW(MOOR_IMAGEVIEW_MAX);
            float scale = bitmapW / bitmapH;
            if (scale >= 3.0) {
                float differ = scale - 2;
                int differDp = (int) (differ * 10);
                if (differDp > 40) {
                    differDp = 40;
                }
                imageInfoBean.setLayoutW(MOOR_IMAGEVIEW_MAX + DensityUtil.dp2px(differDp));
                imageInfoBean.setLayoutH(MOOR_IMAGEVIEW_MIN);
                imageInfoBean.setRealSize((int) (imageInfoBean.getLayoutH() * scale));
            } else {
                imageInfoBean.setLayoutH((int) (imageInfoBean.getLayoutW() / scale));
                imageInfoBean.setRealSize(imageInfoBean.getLayoutW());
            }
            imageInfoBean.setType(MoorImageType.HORIZONTAL);
            return imageInfoBean;
        }

        //竖图
        if (bitmapW < bitmapH) {
            imageInfoBean.setLayoutH(MOOR_IMAGEVIEW_MAX);
            float scale = bitmapH / bitmapW;
            if (scale >= 3.0) {
                imageInfoBean.setLayoutW(MOOR_IMAGEVIEW_MIN);
                imageInfoBean.setRealSize((int) (imageInfoBean.getLayoutW() * scale));
            } else {
                imageInfoBean.setLayoutW((int) (imageInfoBean.getLayoutH() / scale));
                imageInfoBean.setRealSize(imageInfoBean.getLayoutH());
            }
            imageInfoBean.setType(MoorImageType.VERTICAL);
            return imageInfoBean;
        }

        return imageInfoBean;
    }

}