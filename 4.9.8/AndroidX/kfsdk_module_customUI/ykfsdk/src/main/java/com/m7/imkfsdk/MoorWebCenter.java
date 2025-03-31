package com.m7.imkfsdk;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.m7.imkfsdk.chat.KFBaseActivity;
import com.m7.imkfsdk.utils.DownloadDataUtil;
import com.m7.imkfsdk.utils.FileUtils2;
import com.m7.imkfsdk.utils.statusbar.StatusBarUtils;
import com.moor.imkf.utils.LogUtils;

import java.io.File;
import java.net.URLDecoder;

public class MoorWebCenter extends KFBaseActivity {
    private TextView titlebar_name;
	private ImageView titlebar_back;
	private WebView mWebView;
	String Now_Url,titleName;
	private ValueCallback mUploadMessage;

	private final static int FILECHOOSER_RESULTCODE = 10000;


	@Override
	@SuppressLint("SetJavaScriptEnabled")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ykfsdk_act_web);
		StatusBarUtils.setColor(this, getResources().getColor(R.color.ykfsdk_all_white));
		Intent intent = getIntent();
		Now_Url = intent.getStringExtra("OpenUrl");
		LogUtils.d("OpenUrl=",Now_Url);
		titleName = intent.getStringExtra("titleName");
		titlebar_name = findViewById(R.id.titlebar_name);
		titlebar_name.setText(titleName);
		titlebar_back = findViewById(R.id.titlebar_back);
		titlebar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mWebView = findViewById(R.id.webView1);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
//		webSettings.setAllowFileAccess(true);
		webSettings.setSavePassword(false);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setBlockNetworkImage(false);
		webSettings.setUseWideViewPort(true);

		if (BuildConfig.DEBUG) {
			WebView.setWebContentsDebuggingEnabled(true);
		}


		mWebView.setWebChromeClient(new WebChromeClient() {

			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				if (mUploadMessage != null) {
					mUploadMessage.onReceiveValue(null);
				}
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
			}
			// For Android 3.0+
			public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
				if (mUploadMessage != null) {
					mUploadMessage.onReceiveValue(null);
				}
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				String type = TextUtils.isEmpty(acceptType) ? "*/*" : acceptType;
				i.setType(type);
				startActivityForResult(Intent.createChooser(i, "File Chooser"),
						FILECHOOSER_RESULTCODE);
			}
			// For Android 4.1
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
				if (mUploadMessage != null) {
					mUploadMessage.onReceiveValue(null);
				}
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				String type = TextUtils.isEmpty(acceptType) ? "*/*" : acceptType;
				i.setType(type);
				startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
			}
			//Android 5.0+
			@Override
			@SuppressLint("NewApi")
			public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
				if (mUploadMessage != null) {
					mUploadMessage.onReceiveValue(null);
				}
				mUploadMessage = filePathCallback;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				if (fileChooserParams != null && fileChooserParams.getAcceptTypes() != null
						&& fileChooserParams.getAcceptTypes().length > 0 && !"".equals(fileChooserParams.getAcceptTypes()[0])) {
					i.setType(fileChooserParams.getAcceptTypes()[0]);
				} else {
					i.setType("*/*");
				}
				startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
				return true;
			}
		});
		mWebView.setWebViewClient(new WebViewClient() {
			final Dialog progressDialog = ProgressDialog.show(MoorWebCenter.this, null, getString(R.string.ykfsdk_reading));

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Now_Url = url;
				view.loadUrl(url);

				return false;
			}

			@Override
			public void onPageStarted(WebView view, String url,
									  Bitmap favicon) {
				super.onPageStarted(view, url, favicon);

				try {
					progressDialog.show();
					progressDialog.setCancelable(true);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				try {
					progressDialog.cancel();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
//			@Override
//			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
// 				//证书信任
//				handler.proceed();
//			}
		});
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebView.loadUrl(Now_Url);

		mWebView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				String fileName = "";
				LogUtils.d("tag", "url=" + url);
				LogUtils.d("tag", "userAgent=" + userAgent);
				LogUtils.d("tag", "contentDisposition=" + contentDisposition);
				String  decoderString = "";
				try {
				   decoderString  =	URLDecoder.decode(url, "UTF-8");
					String[] split = decoderString.split("/");
					fileName = split[split.length-1];
					LogUtils.d("tag", "mimetype=" + mimetype);
					LogUtils.d("tag", "contentLength=" + contentLength);
					Toast.makeText(MoorWebCenter.this, "正在下载", Toast.LENGTH_SHORT).show();
					final DownloadDataUtil upDataUtil = new DownloadDataUtil(MoorWebCenter.this);
//					final String newFile = getFilesDir() +File.separator+fileName;
					if (Build.VERSION.SDK_INT >= 29) {
						final String finalFileName = fileName;
						new Thread(new Runnable() {
							@Override
							public void run() {
								upDataUtil.downurl(url, finalFileName);
							}
						}).start();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		} else {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (null == mUploadMessage) {
				return;
			}
			Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
			if (result == null) {
				mUploadMessage.onReceiveValue(null);
				mUploadMessage = null;
				return;
			}
			String path =  FileUtils2.getPath(this, result);
			if (TextUtils.isEmpty(path)) {
				mUploadMessage.onReceiveValue(null);
				mUploadMessage = null;
				return;
			}
			Uri uri = Uri.fromFile(new File(path));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				mUploadMessage.onReceiveValue(new Uri[]{uri});
			} else {
				mUploadMessage.onReceiveValue(uri);
			}
			mUploadMessage = null;
		}
	}
}
