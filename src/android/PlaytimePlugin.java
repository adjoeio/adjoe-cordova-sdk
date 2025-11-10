package io.adjoe.sdk.cordova;

import java.text.DateFormat;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;


import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.adjoe.sdk.Playtime;
import io.adjoe.sdk.custom.PlaytimeCustom;
import io.adjoe.sdk.PlaytimeException;
import io.adjoe.sdk.PlaytimeOptions;
import io.adjoe.sdk.PlaytimeExtensions;
import io.adjoe.sdk.PlaytimeGender;
import io.adjoe.sdk.PlaytimeInitialisationListener;
import io.adjoe.sdk.PlaytimeNotInitializedException;
import io.adjoe.sdk.PlaytimeParams;
import io.adjoe.sdk.custom.PlaytimePayoutError;
import io.adjoe.sdk.custom.PlaytimePayoutListener;
import io.adjoe.sdk.custom.PlaytimeRewardListener;
import io.adjoe.sdk.custom.PlaytimeRewardResponse;
import io.adjoe.sdk.custom.PlaytimeRewardResponseError;
import io.adjoe.sdk.PlaytimeUserProfile;

public class PlaytimePlugin extends CordovaPlugin {

    private static final String LOGTAG = "[PlaytimePlugin]";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.i(LOGTAG, "execute " + action + " with args " + args);
        PluginResult result = null;
        if (action == null) {
            return false;
        }

        if (action.equals("init")) {
            Log.i(LOGTAG, "Playtime SDK initialize");
            String sdkHash = args.getString(0);
            JSONObject options = args.getJSONObject(1);
            String subId1 = args.optString(2, null);
            String subId2 = args.optString(3, null);

            if (sdkHash.isEmpty()) {
                callbackContext.error("called init without sdkHash");
                return false;
            }

            executeInitialize(callbackContext, sdkHash, options);

        } else if (action.equals("showCatalog")) {
            Log.i(LOGTAG, "Playtime SDK show Catalog");
            executeShowCatalog(callbackContext, args);

        } else if (action.equals("getRewards")) {
            Log.i(LOGTAG, "Playtime SDK rewards");
            executeGetRewards(callbackContext, args);

        } else if (action.equals("doPayout")) {
            Log.i(LOGTAG, "Playtime SDK payout");
            executeDoPayout(callbackContext, args);

        }  else if (action.equals("setUAParams")) {
            Log.i(LOGTAG, "Playtime sdk set UA Params");
            executeSetUAParams(callbackContext, args);

        } else if (action.equals("sendUserEvent")) {
            Log.i(LOGTAG, "Playtime SDK sendUserEvent");
            executeSendUserEvent(callbackContext, args.getInt(0), args);

        } else if (action.equals("getVersion")) {
            Log.i(LOGTAG, "Playtime SDK get version");
            result = executeGetVersion();

        } else if (action.equals("getVersionName")) {
            Log.i(LOGTAG, "Playtime SDK get version name");
            result = executeGetVersionName();

        } else if (action.equals("isInitialized")) {
            Log.i(LOGTAG, "Playtime SDK is initialized");
            result = executeIsInitialized();

        } else if (action.equals("hasAcceptedTOS")) {
            Log.i(LOGTAG, "Playtime SDK has accepted TOS");
            result = executeHasAcceptedTOS();

        } else if (action.equals("hasAcceptedUsagePermission")) {
            Log.i(LOGTAG, "Playtime SDK has accepted usage permission");
            result = executeHasAcceptedUsagePermission();

        } else if (action.equals("getUserId")) {
            Log.i(LOGTAG, "Playtime SDK get user Id");
            result = executeGetUserId();

        } else if (action.equals("a")) {
            Log.i(LOGTAG, "Playtime a");
            a(callbackContext, args.getBoolean(0));
        }

        if (result != null) {
            callbackContext.sendPluginResult(result);
        }
        return true;
    }

    private void executeInitialize(final CallbackContext callbackContext, final String sdkHash, final JSONObject options) {
        cordova.getThreadPool().execute(new Runnable() {

            public void run() {
                PlaytimeOptions playtimeOptions = new PlaytimeOptions();
                if (options != null) {
                    Log.i(LOGTAG, "options exist");
                    playtimeOptions.setUserId(options.optString("user_id", null))
                            .setParams(providePlaytimeParamsFromOptions(options))
                            .setUserProfile(provideUserProfileFromOptions(options))
                            .setExtensions(providePlaytimeExtensionFromOptions(options));
                }
                playtimeOptions.w("cordova");
                Playtime.init(cordova.getActivity(), sdkHash, playtimeOptions, new PlaytimeInitialisationListener() {

                    @Override
                    public void onInitialisationFinished() {
                        Log.i(LOGTAG, "Playtime SDK initialized");
                        callbackContext.success();
                    }

                    @Override
                    public void onInitialisationError(Exception e) {
                        Log.e(LOGTAG, "Playtime SDK not initialized");
                        if (e != null) {
                            e.printStackTrace();
                            callbackContext.error(e.getMessage());
                        } else {
                            callbackContext.error("Playtime SDK could not be initialized");
                        }
                    }
                });
            }
        });
    }

    private void executeShowCatalog(final CallbackContext callbackContext, JSONArray args) {
        this.cordova.getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    PlaytimeParams playtimeParams = getPlaytimeUaParams(args, 0, 1);
                    Intent playtimeCatalogIntent = Playtime.getCatalogIntent(cordova.getActivity(), playtimeParams);
                    cordova.getActivity().startActivity(playtimeCatalogIntent);

                    callbackContext.success();

                } catch (PlaytimeException exception) {
                    Log.e(LOGTAG, "Playtime SDK threw an exception", exception);
                    callbackContext.error(exception.getMessage());
                }
            }
        });
    }

    private void executeGetRewards(final CallbackContext callbackContext, JSONArray args) {
        cordova.getThreadPool().execute(new Runnable() {

            public void run() {
                PlaytimeParams playtimeParams = getPlaytimeUaParams(args, 0, 1);

                PlaytimeCustom.requestRewards(cordova.getActivity(), playtimeParams, new PlaytimeRewardListener() {

                    @Override
                    public void onUserReceivesReward(PlaytimeRewardResponse playtimeRewardResponse) {
                        // successfully requested the rewards
                        // get the total amount of coins which the user has collected
                        int reward = playtimeRewardResponse.reward;
                        // get the amount of coins which are available for payout
                        int availableForPayout = playtimeRewardResponse.availablePayoutCoins;
                        // get the amount of coins which the user has already spent
                        int alreadySpentCoins = playtimeRewardResponse.alreadySpentCoins;


                        JSONObject json = new JSONObject();
                        try {
                            json.put("reward", reward);
                            json.put("available_for_payout", availableForPayout);
                            json.put("already_spent_coins", alreadySpentCoins);
                        } catch (JSONException e) {}

                        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, json));
                    }

                    // todo: corvin, when executed.
                    @Override
                    public void onUserReceivesRewardError(PlaytimeRewardResponseError playtimeRewardResponseError) {
                        // an error occurred while requesting the rewards
                        // you can try to get additional information about the error for debugging
                        Log.e(LOGTAG, "Playtime  onUserReceivesRewardError" + playtimeRewardResponseError.toString());

                        if (playtimeRewardResponseError.exception != null) {
                            playtimeRewardResponseError.exception.printStackTrace();
                            callbackContext.error(playtimeRewardResponseError.exception.getMessage());
                        } else {
                            callbackContext.error("Playtime SDK could not fetch rewards");
                        }
                    }
                });
            }
        });
    }

    private void executeDoPayout(final CallbackContext callbackContext, JSONArray args) {
        cordova.getThreadPool().execute(new Runnable() {

            public void run() {

                PlaytimeParams playtimeParams = getPlaytimeUaParams(args, 0, 1);
                PlaytimeCustom.doPayout(cordova.getActivity(), playtimeParams, new PlaytimePayoutListener() {

                    @Override
                    public void onPayoutExecuted(int coins) {
                        // successfully paid out 'coins' coins
                        JSONObject json = new JSONObject();
                        try {
                            json.put("payout_coins", coins);
                        } catch (JSONException e) {}

                        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, json));
                    }

                    @Override
                    public void onPayoutError(PlaytimePayoutError playtimePayoutError) {

                        // an error occurred while paying out the coins

                        // get information about why it failed
                        // 'reason' is one of
                        // - PlaytimePayoutError.UNKNOWN
                        // - PlaytimePayoutError.NOT_ENOUGH_COINS
                        // - PlaytimePayoutError.TOS_NOT_ACCEPTED
                        int reason = playtimePayoutError.getReason();

                        JSONObject message = new JSONObject();
                        try {
                            message.put("reason", reason);
                        } catch (JSONException e) {}

                        // if available, get more information about the error

                        if (playtimePayoutError.getException() != null) {
                            playtimePayoutError.getException().printStackTrace();
                            try {
                                message.put("exception", playtimePayoutError.getException().getMessage());
                            } catch (JSONException e) {}
                        }
                        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, message));
                    }
                });
        
            }
        });
    }

    private void executeSetUAParams(final CallbackContext callbackContext, JSONArray args) {
        Log.i(LOGTAG, "executeSetUAParams");
        cordova.getThreadPool().execute(() -> {
            try {
                PlaytimeParams params = getPlaytimeUaParams(args, 0, 0);
                Playtime.setUAParams(cordova.getActivity(), params);
                callbackContext.success();
            } catch (Exception e) {
                callbackContext.error(e.getMessage());
            }
        });
    }

    private void a(final CallbackContext callbackContext, final boolean a) {
        cordova.getThreadPool().execute(new Runnable() {

            @Override
            public void run() {
                Playtime.a(a);
                callbackContext.success();
            }
        });
    }

    private void executeSendUserEvent(final CallbackContext callbackContext, final int eventId, JSONArray args) {
        cordova.getThreadPool().execute(new Runnable() {

            @Override
            public void run() {
                try {
                    PlaytimeParams playtimeParams = getPlaytimeUaParams(args, 1, 2);
                    Playtime.sendUserEvent(cordova.getActivity(), eventId, null, playtimeParams);

                    callbackContext.success();
                } catch (PlaytimeNotInitializedException e) {
                    Log.e(LOGTAG, "Playtime SDK not initialized", e);
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }

    private PluginResult executeGetVersion() throws JSONException {
        JSONObject message = new JSONObject();
        message.put("version", Playtime.getVersion());
        return new PluginResult(PluginResult.Status.OK, message);
    }

    private PluginResult executeGetVersionName() throws JSONException {
        JSONObject message = new JSONObject();
        message.put("version_name", Playtime.getVersionName());
        return new PluginResult(PluginResult.Status.OK, message);
    }

    private PluginResult executeIsInitialized() throws JSONException {
        JSONObject message = new JSONObject();
        message.put("initialized", Playtime.isInitialized());
        return new PluginResult(PluginResult.Status.OK, message);
    }

    private PluginResult executeHasAcceptedTOS() throws JSONException {
        JSONObject message = new JSONObject();
        message.put("accepted", Playtime.hasAcceptedTOS(cordova.getActivity()));
        return new PluginResult(PluginResult.Status.OK, message);
    }

    private PluginResult executeHasAcceptedUsagePermission() throws JSONException {
        JSONObject message = new JSONObject();
        message.put("accepted", Playtime.hasAcceptedUsagePermission(cordova.getActivity()));
        return new PluginResult(PluginResult.Status.OK, message);
    }

    private PluginResult executeGetUserId() throws JSONException {
        JSONObject message = new JSONObject();
        message.put("user_id", Playtime.getUserId(cordova.getActivity()));
        return new PluginResult(PluginResult.Status.OK, message);
    }

    // region Helper Methods
    private PlaytimeUserProfile provideUserProfileFromOptions(JSONObject options) {
        JSONObject userProfile = options.optJSONObject("user_profile");
        return constructPlaytimeUserProfile(userProfile);
    }

    private PlaytimeUserProfile constructPlaytimeUserProfile(JSONObject userProfile) {
        if (userProfile == null) return null;
        String gender = userProfile.optString("gender");
        PlaytimeGender playtimeGender;
        switch (gender) {
            case "male":
                playtimeGender = PlaytimeGender.MALE;
                break;
            case "female":
                playtimeGender = PlaytimeGender.FEMALE;
                break;
            default:
                playtimeGender = PlaytimeGender.MALE;
        }
        String date = userProfile.optString("birthdate");
        if (TextUtils.isEmpty(gender) && TextUtils.isEmpty(date)) return null;

        Date birthdate = null;
        try {
            birthdate = DateFormat.getDateInstance().parse(date);
        } catch (ParseException ignore) {
            // nothing
        }
        return new PlaytimeUserProfile(playtimeGender, birthdate);
    }

    private PlaytimeParams providePlaytimeParamsFromOptions(JSONObject options) {
        JSONObject params = options.optJSONObject("playtime_params");
        return constructPlaytimeParams(params);
    }

    private PlaytimeParams constructPlaytimeParams(JSONObject params) {
        PlaytimeParams.Builder builder = new PlaytimeParams.Builder();
        if (params == null) return builder.build();
        String uaNetwork = params.optString("uaNetwork");
        String uaChannel = params.optString("uaChannel");
        String uaSubPublisherCleartext = params.optString("uaSubPublisherCleartext");
        String uaSubPublisherEncrypted = params.optString("uaSubPublisherEncrypted");
        String placement = params.optString("placement");

        return builder
                .setUaNetwork(uaNetwork)
                .setUaChannel(uaChannel)
                .setUaSubPublisherCleartext(uaSubPublisherCleartext)
                .setUaSubPublisherEncrypted(uaSubPublisherEncrypted)
                .setPlacement(placement)
                .build();
    }

    private PlaytimeParams constructPlaytimeParams(String subId1, String subId2) {
        PlaytimeParams.Builder builder = new PlaytimeParams.Builder();
        if (TextUtils.isEmpty(subId1) && TextUtils.isEmpty(subId2)) return builder.build();
        return builder.setUaNetwork(subId1)
                .setUaChannel(subId2)
                .build();
    }

    private PlaytimeExtensions providePlaytimeExtensionFromOptions(JSONObject options) {
        JSONObject extension = options.optJSONObject("playtime_extension");
        return constructPlaytimeExtensions(extension);
    }

    private PlaytimeExtensions constructPlaytimeExtensions(JSONObject extension) {
        PlaytimeExtensions.Builder builder = new PlaytimeExtensions.Builder();
        if (extension == null) return builder.build();
        String subId1 = extension.optString("subId1");
        String subId2 = extension.optString("subId2");
        String subId3 = extension.optString("subId3");
        String subId4 = extension.optString("subId4");
        String subId5 = extension.optString("subId5");
        return builder
                .setSubId1(subId1)
                .setSubId2(subId2)
                .setSubId3(subId3)
                .setSubId4(subId4)
                .setSubId5(subId5)
                .build();
    }

    private JSONObject getPlaytimeUaParamsFromArgs(JSONArray args) {
        Object obj = args.opt(0);
        if (obj == null) return null;
        if (obj instanceof JSONObject) {
            return (JSONObject) obj;
        }
        return null;
    }

    private PlaytimeParams getPlaytimeUaParams(JSONArray args, int subId1Index, int subId2Index) {
        JSONObject playtimeParmasObj = getPlaytimeUaParamsFromArgs(args);
        PlaytimeParams playtimeParams;
        if (playtimeParmasObj != null) {
            playtimeParams = constructPlaytimeParams(playtimeParmasObj);
        } else {
            playtimeParams = constructPlaytimeParams(args.optString(subId1Index, null), args.optString(subId2Index, null));
        }
        return playtimeParams;
    }
    // endregion

}
