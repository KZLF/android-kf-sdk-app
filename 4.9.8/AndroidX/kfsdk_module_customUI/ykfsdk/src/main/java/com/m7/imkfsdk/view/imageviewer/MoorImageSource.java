package com.m7.imkfsdk.view.imageviewer;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.moor.imkf.lib.utils.MoorStringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * <pre>
 *     @author : Trial
 *     @time   : 2/23/21
 *     @desc   :
 *     @version: 1.0
 * </pre>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class MoorImageSource {

    public static final String FILE_SCHEME = "file:///";
    public  static final String ASSET_SCHEME = "file:///android_asset/";

    private final Uri uri;
    private final Bitmap bitmap;
    private final Integer resource;
    private boolean tile;
    private int sWidth;
    private int sHeight;
    private Rect sRegion;
    private boolean cached;

    private MoorImageSource(Bitmap bitmap, boolean cached) {
        this.bitmap = bitmap;
        this.uri = null;
        this.resource = null;
        this.tile = false;
        this.sWidth = bitmap.getWidth();
        this.sHeight = bitmap.getHeight();
        this.cached = cached;
    }

    private MoorImageSource(@NonNull Uri uri) {
        // #114 If file doesn't exist, attempt to url decode the URI and try again
        String uriString = uri.toString();
        if (uriString.startsWith(FILE_SCHEME)) {
            File uriFile = new File(uriString.substring(FILE_SCHEME.length() - 1));
            if (!uriFile.exists()) {
                try {
                    uri = Uri.parse(URLDecoder.decode(MoorStringUtils.replaceURLDecoder(uriString), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // Fallback to encoded URI. This exception is not expected.
                }
            }
        }
        this.bitmap = null;
        this.uri = uri;
        this.resource = null;
        this.tile = true;
    }

    private MoorImageSource(int resource) {
        this.bitmap = null;
        this.uri = null;
        this.resource = resource;
        this.tile = true;
    }

    /**
     * Create an instance from a resource. The correct resource for the device screen resolution will be used.
     * @param resId resource ID.
     * @return an {@link MoorImageSource} instance.
     */
    @NonNull
    public static MoorImageSource resource(int resId) {
        return new MoorImageSource(resId);
    }

    /**
     * Create an instance from an asset name.
     * @param assetName asset name.
     * @return an {@link MoorImageSource} instance.
     */
    @NonNull
    public static MoorImageSource asset(@NonNull String assetName) {
        //noinspection ConstantConditions
        if (assetName == null) {
            throw new NullPointerException("Asset name must not be null");
        }
        return uri(ASSET_SCHEME + assetName);
    }

    /**
     * Create an instance from a URI. If the URI does not start with a scheme, it's assumed to be the URI
     * of a file.
     * @param uri image URI.
     * @return an {@link MoorImageSource} instance.
     */
    @NonNull
    public static MoorImageSource uri(@NonNull String uri) {
        //noinspection ConstantConditions
        if (uri == null) {
            throw new NullPointerException("Uri must not be null");
        }
        if (!uri.contains("://")) {
            if (uri.startsWith("/")) {
                uri = uri.substring(1);
            }
            uri = FILE_SCHEME + uri;
        }
        return new MoorImageSource(Uri.parse(uri));
    }

    /**
     * Create an instance from a URI.
     * @param uri image URI.
     * @return an {@link MoorImageSource} instance.
     */
    @NonNull
    public static MoorImageSource uri(@NonNull Uri uri) {
        //noinspection ConstantConditions
        if (uri == null) {
            throw new NullPointerException("Uri must not be null");
        }
        return new MoorImageSource(uri);
    }

    /**
     * Provide a loaded bitmap for display.
     * @param bitmap bitmap to be displayed.
     * @return an {@link MoorImageSource} instance.
     */
    @NonNull
    public static MoorImageSource bitmap(@NonNull Bitmap bitmap) {
        //noinspection ConstantConditions
        if (bitmap == null) {
            throw new NullPointerException("Bitmap must not be null");
        }
        return new MoorImageSource(bitmap, false);
    }

    /**
     * Provide a loaded and cached bitmap for display. This bitmap will not be recycled when it is no
     * longer needed. Use this method if you loaded the bitmap with an image loader such as Picasso
     * or Volley.
     * @param bitmap bitmap to be displayed.
     * @return an {@link MoorImageSource} instance.
     */
    @NonNull
    public static MoorImageSource cachedBitmap(@NonNull Bitmap bitmap) {
        //noinspection ConstantConditions
        if (bitmap == null) {
            throw new NullPointerException("Bitmap must not be null");
        }
        return new MoorImageSource(bitmap, true);
    }

    /**
     * Enable tiling of the image. This does not apply to preview images which are always loaded as a single bitmap.,
     * and tiling cannot be disabled when displaying a region of the source image.
     * @return this instance for chaining.
     */
    @NonNull
    public MoorImageSource tilingEnabled() {
        return tiling(true);
    }

    /**
     * Disable tiling of the image. This does not apply to preview images which are always loaded as a single bitmap,
     * and tiling cannot be disabled when displaying a region of the source image.
     * @return this instance for chaining.
     */
    @NonNull
    public MoorImageSource tilingDisabled() {
        return tiling(false);
    }

    /**
     * Enable or disable tiling of the image. This does not apply to preview images which are always loaded as a single bitmap,
     * and tiling cannot be disabled when displaying a region of the source image.
     * @param tile whether tiling should be enabled.
     * @return this instance for chaining.
     */
    @NonNull
    public MoorImageSource tiling(boolean tile) {
        this.tile = tile;
        return this;
    }

    /**
     * Use a region of the source image. Region must be set independently for the full size image and the preview if
     * you are using one.
     * @param sRegion the region of the source image to be displayed.
     * @return this instance for chaining.
     */
    @NonNull
    public MoorImageSource region(Rect sRegion) {
        this.sRegion = sRegion;
        setInvariants();
        return this;
    }

    /**
     * Declare the dimensions of the image. This is only required for a full size image, when you are specifying a URI
     * and also a preview image. When displaying a bitmap object, or not using a preview, you do not need to declare
     * the image dimensions. Note if the declared dimensions are found to be incorrect, the view will reset.
     * @param sWidth width of the source image.
     * @param sHeight height of the source image.
     * @return this instance for chaining.
     */
    @NonNull
    public MoorImageSource dimensions(int sWidth, int sHeight) {
        if (bitmap == null) {
            this.sWidth = sWidth;
            this.sHeight = sHeight;
        }
        setInvariants();
        return this;
    }

    private void setInvariants() {
        if (this.sRegion != null) {
            this.tile = true;
            this.sWidth = this.sRegion.width();
            this.sHeight = this.sRegion.height();
        }
    }

    public final Uri getUri() {
        return uri;
    }

    public final Bitmap getBitmap() {
        return bitmap;
    }

    public final Integer getResource() {
        return resource;
    }

    public final boolean getTile() {
        return tile;
    }

    public final int getSWidth() {
        return sWidth;
    }

    public final int getSHeight() {
        return sHeight;
    }

    public final Rect getSRegion() {
        return sRegion;
    }

    public final boolean isCached() {
        return cached;
    }
}
