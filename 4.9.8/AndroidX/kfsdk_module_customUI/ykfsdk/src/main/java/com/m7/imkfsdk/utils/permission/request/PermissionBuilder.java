/*
 * Copyright (C)  guolin, PermissionX Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.m7.imkfsdk.utils.permission.request;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import android.text.TextUtils;

import com.m7.imkfsdk.utils.permission.callback.ExplainReasonCallback;
import com.m7.imkfsdk.utils.permission.callback.ExplainReasonCallbackWithBeforeParam;
import com.m7.imkfsdk.utils.permission.callback.ForwardToSettingsCallback;
import com.m7.imkfsdk.utils.permission.callback.RequestCallback;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * More APIs for developers to control PermissionX functions.
 *
 * @Author: chenbo
 * @Date: 2020/6/17
 */
public class PermissionBuilder {

    /**
     * TAG of InvisibleFragment to find and create.
     */
    private static final String FRAGMENT_TAG = "InvisibleFragment";

    /**
     * Instance of activity for everything.
     */
    FragmentActivity activity;

    /**
     * Normal runtime permissions that app want to request.
     */
    Set<String> normalPermissions;

    /**
     * Some permissions shouldn't request will be stored here. And notify back to user when request finished.
     */
    Set<String> permissionsWontRequest;

    /**
     * Indicate if we should require background location permission alone.
     * If app run on Android R and targetSdkVersion is R, when request ACCESS_BACKGROUND_LOCATION, this should be true.
     */
    boolean requireBackgroundLocationPermission;

    /**
     * Indicates should PermissionX explain request reason before request.
     */
    boolean explainReasonBeforeRequest = false;

    /**
     * Indicates {@link ExplainScope#showRequestReasonDialog(List, String, String)} or {@link ForwardScope#showForwardToSettingsDialog(List, String, String)} is called in {@link #onExplainRequestReason(ExplainReasonCallback)} or {@link #onForwardToSettings(ForwardToSettingsCallback)} callback.
     * If not called, requestCallback will be called by PermissionX automatically.
     */
    boolean showDialogCalled = false;

    /**
     * Holds permissions that have already granted in the requested permissions.
     */
    Set<String> grantedPermissions = new HashSet<>();

    /**
     * Holds permissions that have been denied in the requested permissions.
     */
    Set<String> deniedPermissions = new HashSet<>();

    /**
     * Holds permissions that have been permanently denied in the requested permissions. (Deny and never ask again)
     */
    Set<String> permanentDeniedPermissions = new HashSet<>();

    /**
     * Holds permissions which should forward to Settings to allow them.
     * Not all permanently denied permissions should forward to Settings. Only the ones developer think they are necessary should.
     */
    Set<String> forwardPermissions = new HashSet<>();

    /**
     * The callback for {@link #request(RequestCallback)} method. Can not be null.
     */
    RequestCallback requestCallback;

    /**
     * The callback for {@link #onExplainRequestReason(ExplainReasonCallback)} method. Maybe null.
     */
    ExplainReasonCallback explainReasonCallback;

    /**
     * The callback for {@link #onExplainRequestReason(ExplainReasonCallbackWithBeforeParam)} method, but with beforeRequest param. Maybe null.
     */
    ExplainReasonCallbackWithBeforeParam explainReasonCallbackWithBeforeParam;

    /**
     * The callback for {@link #onForwardToSettings(ForwardToSettingsCallback)} method. Maybe null.
     */
    ForwardToSettingsCallback forwardToSettingsCallback;

    public PermissionBuilder(FragmentActivity activity, Set<String> normalPermissions, boolean requireBackgroundLocationPermission, Set<String> permissionsWontRequest) {
        this.activity = activity;
        this.normalPermissions = normalPermissions;
        this.requireBackgroundLocationPermission = requireBackgroundLocationPermission;
        this.permissionsWontRequest = permissionsWontRequest;
    }

    /**
     * Called when permissions need to explain request reason.
     * Typically every time user denies your request would call this method.
     * If you chained {@link #explainReasonBeforeRequest()}, this method might run before permission request.
     *
     * @param callback Callback with permissions denied by user.
     */
    public PermissionBuilder onExplainRequestReason(ExplainReasonCallback callback) {
        explainReasonCallback = callback;
        return this;
    }

    /**
     * Called when permissions need to explain request reason.
     * Typically every time user denies your request would call this method.
     * If you chained {@link #explainReasonBeforeRequest()}, this method might run before permission request.
     * beforeRequest param would tell you this method is currently before or after permission request.
     *
     * @param callback Callback with permissions denied by user.
     */
    public PermissionBuilder onExplainRequestReason(ExplainReasonCallbackWithBeforeParam callback) {
        explainReasonCallbackWithBeforeParam = callback;
        return this;
    }

    /**
     * Called when permissions need to forward to Settings for allowing.
     * Typically user denies your request and checked never ask again would call this method.
     * Remember {@link #onExplainRequestReason(ExplainReasonCallback)} is always prior to this method.
     * If {@link #onExplainRequestReason(ExplainReasonCallback)} is called, this method will not be called in the same request time.
     *
     * @param callback Callback with permissions denied and checked never ask again by user.
     */
    public PermissionBuilder onForwardToSettings(ForwardToSettingsCallback callback) {
        forwardToSettingsCallback = callback;
        return this;
    }

    /**
     * If you need to show request permission rationale, chain this method in your request syntax.
     * {@link #onExplainRequestReason(ExplainReasonCallback)} will be called before permission request.
     */
    public PermissionBuilder explainReasonBeforeRequest() {
        explainReasonBeforeRequest = true;
        return this;
    }

    /**
     * Request permissions at once, and handle request result in the callback.
     *
     * @param callback Callback with 3 params. allGranted, grantedList, deniedList.
     */
    public void request(RequestCallback callback) {
        requestCallback = callback;
        // Build the request chain.
        // RequestNormalPermissions runs first.
        // Then RequestBackgroundLocationPermission runs.
        RequestChain requestChain = new RequestChain();
        requestChain.addTaskToChain(new RequestNormalPermissions(this));
        requestChain.addTaskToChain(new RequestBackgroundLocationPermission(this));
        requestChain.runTask();
    }

    /**
     * This method is internal, and should not be called by developer.
     * <p>
     * Show a dialog to user and  explain why these permissions are necessary.
     *
     * @param chainTask Instance of current task.
     * @param showReasonOrGoSettings Indicates should show explain reason or forward to Settings.
     * @param permissions            Permissions to request again.
     * @param message                Message that explain to user why these permissions are necessary.
     * @param positiveText           Positive text on the positive button to request again.
     * @param negativeText           Negative text on the negative button. Maybe null if this dialog should not be canceled.
     */
    void showHandlePermissionDialog(final ChainTask chainTask, final boolean showReasonOrGoSettings, final List<String> permissions, String message, String positiveText, String negativeText) {
        showDialogCalled = true;
        if (permissions == null || permissions.isEmpty()) {
            chainTask.finish();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setCancelable(!TextUtils.isEmpty(negativeText));
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (showReasonOrGoSettings) {
                    chainTask.requestAgain(permissions);
                } else {
                    forwardToSettings(permissions);
                }
            }
        });
        if (!TextUtils.isEmpty(negativeText)) {
            builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    chainTask.finish();
                }
            });
        }
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * Request permissions at once in the fragment.
     *
     * @param permissions Permissions that you want to request.
     * @param chainTask Instance of current task.
     */
    void requestNow(Set<String> permissions, ChainTask chainTask) {
        getInvisibleFragment().requestNow(this, permissions, chainTask);
    }

    /**
     * Request ACCESS_BACKGROUND_LOCATION at once in the fragment.
     * @param chainTask Instance of current task.
     */
    void requestAccessBackgroundLocationNow(ChainTask chainTask) {
        getInvisibleFragment().requestAccessBackgroundLocationNow(this, chainTask);
    }

    /**
     * Get the invisible fragment in activity for request permissions.
     * If there is no invisible fragment, add one into activity.
     * Don't worry. This is very lightweight.
     */
    private InvisibleFragment getInvisibleFragment() {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment existedFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (existedFragment != null) {
            return (InvisibleFragment) existedFragment;
        } else {
            InvisibleFragment invisibleFragment = new InvisibleFragment();
            fragmentManager.beginTransaction().add(invisibleFragment, FRAGMENT_TAG).commitNow();
            return invisibleFragment;
        }
    }

    /**
     * Go to your app's Settings page to let user turn on the necessary permissions.
     *
     * @param permissions Permissions which are necessary.
     */
    private void forwardToSettings(List<String> permissions) {
        forwardPermissions.clear();
        forwardPermissions.addAll(permissions);
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        getInvisibleFragment().startActivityForResult(intent, InvisibleFragment.FORWARD_TO_SETTINGS);
    }

}