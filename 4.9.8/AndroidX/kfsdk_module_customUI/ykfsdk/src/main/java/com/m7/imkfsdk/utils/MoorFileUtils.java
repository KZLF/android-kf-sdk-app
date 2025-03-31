package com.m7.imkfsdk.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;

import com.moor.imkf.IMChatManager;
import com.moor.imkf.lib.utils.MoorLogUtils;
import com.moor.imkf.lib.utils.MoorSdkVersionUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;

/**
 * <pre>
 *     @author : Trial
 *     @time   : 2/1/21
 *     @desc   : 文件操作
 *     @version: 1.0
 * </pre>
 */
public class MoorFileUtils {

    private MoorFileUtils() {
    }

    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    private static boolean isSpace(final String s) {
        if (s == null) {
            return true;
        }
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isFileExists(final String filePath) {
        return isFileExists(getFileByPath(filePath));
    }

    public static boolean isFileExists(final File file) {
        return file != null && file.exists();
    }


    private static String getFilePathFromUri_Q(Uri contentUri) {
        File rootDataDir = IMChatManager.getInstance().getApplicationAgain().getExternalFilesDir(null);
        String fileName = getFileNameByUri(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(rootDataDir + File.separator + fileName);
            copyFile(contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    private static String getFileNameByUri(Uri uri) {
        String fileName = "";
        Cursor cursor = IMChatManager.getInstance().getApplicationAgain().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));
            cursor.close();
        }
        return fileName;
    }

    public static String getFileName(String filePath) {

        int start = filePath.lastIndexOf("/");
        int end = filePath.length();
        if (start != -1) {
            return filePath.substring(start + 1, end);
        } else {
            return null;
        }

    }

    private static void copyFile(Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = IMChatManager.getInstance().getApplicationAgain().getContentResolver().openInputStream(srcUri);
            if (inputStream == null) {
                return;
            }
            OutputStream outputStream = new FileOutputStream(dstFile);
            copyStream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据文件路径拷贝文件
     *
     * @param resourceFile 源文件
     * @param targetPath   目标路径（包含文件名和文件格式）
     * @return boolean 成功true、失败false
     */
    public static boolean copyFile(File resourceFile, String targetPath, String fileName) {
        boolean result = false;
        if (resourceFile == null || TextUtils.isEmpty(targetPath)) {
            return result;
        }
        File target = new File(targetPath);
        if (target.exists()) {
            target.delete(); // 已存在的话先删除
        } else {
            try {
                target.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File targetFile = new File(targetPath.concat(fileName));
        if (targetFile.exists()) {
            targetFile.delete();
        } else {
            try {
                targetFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileChannel resourceChannel = null;
        FileChannel targetChannel = null;
        try {
            resourceChannel = new FileInputStream(resourceFile).getChannel();
            targetChannel = new FileOutputStream(targetFile).getChannel();
            resourceChannel.transferTo(0, resourceChannel.size(), targetChannel);
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
        try {
            resourceChannel.close();
            targetChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static int copyStream(InputStream input, OutputStream output) throws Exception {
        final int BUFFER_SIZE = 1024 * 2;
        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
            try {
                in.close();
            } catch (IOException e) {
            }
        }
        return count;
    }


    public static String readFileSize(String path) {
        return readableFileSize(new File(path).length());
    }

    private static String readableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private static String name;

    public static String getRealPathFromUri(final Context context, final Uri uri) {
        if (uri == null) {
            return "";
        }
        //Android Q 文件 URI-file单独处理
        if (MoorSdkVersionUtil.over29()) {
            return getFilePathFromUri_Q(uri);
        }
        if (MoorSdkVersionUtil.over19() && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {

                String id = DocumentsContract.getDocumentId(uri);
                if (!TextUtils.isEmpty(id)) {
                    if (id.startsWith("raw:")) {
                        final String path = id.replaceFirst("raw:", "");
                        return path;
                    }

                    try {
                        Uri contentUri = uri;
                        contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
//                        }
                        return getDataColumn(context, contentUri, null, null);
                    } catch (Exception e) {
                        ToastUtils.showShort(context, "暂不支持此类文件");
                    }

                }
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (MoorSdkVersionUtil.over24()) {
                return getFilePathForN(uri, context);
            } else {
                return getDataColumn(context, uri, null, null);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "";
    }

    private static String getFilePathForN(Uri uri, Context context) {
        Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
        try {
            if (returnCursor != null && returnCursor.moveToFirst()) {
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                name = (returnCursor.getString(nameIndex));
            }
        } finally {
            returnCursor.close();
        }

        if (name == null) {
            name = uri.getPath();
            int cut = name.lastIndexOf('/');
            if (cut != -1) {
                name = name.substring(cut + 1);
            }
        }
        File file = new File(context.getFilesDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1024 * 1024;
            int bytesAvailable = inputStream.available();

            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            MoorLogUtils.e("Exception", e.getMessage());
        }
        return file.getPath();
    }


    /**
     * Create a file if it doesn't exist, otherwise delete old file before creating.
     *
     * @param filePath The path of file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean createFileByDeleteOldFile(final String filePath) {
        return createFileByDeleteOldFile(getFileByPath(filePath));
    }

    /**
     * Create a file if it doesn't exist, otherwise delete old file before creating.
     *
     * @param file The file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean createFileByDeleteOldFile(final File file) {
        if (file == null) {
            return false;
        }
        // file exists and unsuccessfully delete then return false
        if (file.exists() && !file.delete()) {
            return false;
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create a directory if it doesn't exist, otherwise do nothing.
     *
     * @param file The file.
     * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
     */
    public static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 递归求取目录文件个数
     *
     * @param file
     * @return
     */
    public static long getFileCount(File file) {
        long size = 0;
        File[] files = file.listFiles();
        size = files.length;
        for (File value : files) {
            if (value.isDirectory()) {
                size = size + getFileCount(value);
                size--;
            }
        }
        return size;
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //加载图片
        BitmapFactory.decodeFile(path, options);
        //计算缩放比
        options.inSampleSize = calculateInSampleSize(options, reqHeight, reqWidth);
        //重新加载图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqHeight, int reqWidth) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            //计算缩放比，是2的指数
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }


        return inSampleSize;
    }

    /**
     * 保存图片到共有目录 Pictures/moor/
     *
     * @param context
     * @param sourceFile
     * @return
     */
    public static boolean saveImage(Context context, File sourceFile,String suffix) {
        if (MoorSdkVersionUtil.over29()) {
            ContentValues values = new ContentValues();
            if ("heic".endsWith(suffix)||"bmp".endsWith(suffix)){
                values.put(MediaStore.Images.Media.DISPLAY_NAME, "moor" + System.currentTimeMillis() + "." + suffix);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + suffix);
            }else {
                values.put(MediaStore.Images.Media.DISPLAY_NAME, "moor" + System.currentTimeMillis() + ".jpg");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            }
            values.put(MediaStore.Images.Media.RELATIVE_PATH, MoorPathUtil.getImageDownLoadPath());
            Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver resolver = context.getContentResolver();

            Uri insertUri = resolver.insert(external, values);

            BufferedInputStream inputStream = null;
            OutputStream os = null;
            boolean result;
            try {
                inputStream = new BufferedInputStream(new FileInputStream(sourceFile));
                if (insertUri != null) {
                    os = resolver.openOutputStream(insertUri);
                }
                if (os != null) {
                    byte[] buffer = new byte[1024 * 4];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                    }
                    os.flush();
                }
                if (sourceFile.exists()) {

                }
                result = true;
            } catch (Exception e) {
                result = false;
            } finally {
                try {
                    os.close();
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return result;
        } else {
            return saveImageToGallery(context, sourceFile);
        }

    }

    private static boolean saveImageToGallery(Context context, File sourceFile) {
        // 首先保存图片
        String storePath = MoorPathUtil.getImageDownLoadPath();
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "moor" + System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        BufferedInputStream inputStream = null;
        boolean result = false;

        try {
            fos = new FileOutputStream(file);
            inputStream = new BufferedInputStream(new FileInputStream(sourceFile));
            if (fos != null) {
                byte[] buffer = new byte[1024 * 4];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
            }
            if (sourceFile.exists()) {

            }
            result = true;
            //把文件插入到系统图库
//            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

            return result;
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
            try {
                fos.flush();
                fos.close();
            } catch (Exception ioException) {
                result = false;
                ioException.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 保存文件到共有目录 Download/moor/
     *
     * @param context
     * @param sourceFile
     * @return
     */
    public static boolean saveFile(Context context, File sourceFile, String fileName) {
        if (MoorSdkVersionUtil.over29()) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, getFormatName(fileName));
            values.put(MediaStore.Downloads.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Downloads.RELATIVE_PATH, MoorPathUtil.getFileDownLoadPath());
            ContentResolver resolver = context.getContentResolver();

            Uri insertUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            BufferedInputStream inputStream = null;
            OutputStream os = null;
            boolean result;

            try {
                inputStream = new BufferedInputStream(new FileInputStream(sourceFile));
                if (insertUri != null) {
                    os = resolver.openOutputStream(insertUri);
                }
                if (os != null) {
                    byte[] buffer = new byte[1024 * 4];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                    }
                    os.flush();
                }

                result = true;
            } catch (Exception e) {
                result = false;
            } finally {
                try {
                    os.close();
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return result;
        } else {
            return saveFileToGallery(sourceFile, fileName);
        }

    }

    private static boolean saveFileToGallery(File sourceFile, String fileName) {
        // 首先保存图片
        String storePath = MoorPathUtil.getFileDownLoadPath();
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        BufferedInputStream inputStream = null;
        boolean result = false;

        try {
            fos = new FileOutputStream(file);
            inputStream = new BufferedInputStream(new FileInputStream(sourceFile));
            if (fos != null) {
                byte[] buffer = new byte[1024 * 4];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
            }
            if (sourceFile.exists()) {

            }
            result = true;
            return result;
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
            try {
                fos.flush();
                fos.close();
            } catch (Exception ioException) {
                result = false;
                ioException.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 获取文件目录File对象
     *
     * @param folderPath 存放的文件夹名称
     * @param childPath  创建子文件夹名称
     * @param fileName   要创建的文件名称
     */
    public static File getDownloadFile(String folderPath, String childPath, String fileName) {
        String fileDir = "";
        if (TextUtils.isEmpty(childPath)) {
            fileDir = folderPath;
        } else {
            fileDir = folderPath + childPath + File.separator;
        }
        File dir = new File(fileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return new File(dir, fileName);
    }

    /**
     * 获取文件格式名
     */
    public static String getFormatName(String fileName) {
        if (fileName == null) {
            return null;
        }
        fileName = fileName.trim();
        int dot = fileName.lastIndexOf(".");
        if (dot >= 0) {
            return fileName.substring(dot);
        } else {
            // No extension.
            return "";
        }
    }

    /**
     * 判断Downloads 文件夹  文件是否存在
     * @param context
     * @param dirName  Downloads下的目录
     * @param fileName 文件名
     * @return
     */
    public static boolean isDownloadsFileExists(Context context, String dirName, String fileName) {
        boolean isStoreFileExists = false;
        if (MoorSdkVersionUtil.over29()) {
            ContentResolver resolver = context.getContentResolver();
            Uri downloadUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
            Cursor resultCursor = resolver.query(downloadUri,
                    null,
                    MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME + "=?",
                    new String[]{dirName},
                    null);
            if (resultCursor != null) {
                while (resultCursor.moveToNext()) {
                    int fileNameIndex = resultCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
                    String fName = resultCursor.getString(fileNameIndex);
                    if (fileName.equals(fName)) {
                        isStoreFileExists = true;
                        resultCursor.close();
                        break;
                    }
                }
            }
        } else {
            String path = Environment.getExternalStorageDirectory().getPath() + File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator +
                    dirName + File.separator+fileName;
            File file=new File(path);
            if(file.exists()){
                isStoreFileExists = true;
            }
        }
        return isStoreFileExists;
    }

}
