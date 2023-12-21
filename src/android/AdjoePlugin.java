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

import io.adjoe.sdk.Adjoe;
import io.adjoe.sdk.AdjoeException;
import io.adjoe.sdk.AdjoeExtensions;
import io.adjoe.sdk.AdjoeGender;
import io.adjoe.sdk.AdjoeInitialisationListener;
import io.adjoe.sdk.AdjoeNotInitializedException;
import io.adjoe.sdk.AdjoeParams;
import io.adjoe.sdk.AdjoePayoutError;
import io.adjoe.sdk.AdjoePayoutListener;
import io.adjoe.sdk.AdjoeRewardListener;
import io.adjoe.sdk.AdjoeRewardResponse;
import io.adjoe.sdk.AdjoeRewardResponseError;
import io.adjoe.sdk.AdjoeUserProfile;

public class AdjoePlugin extends CordovaPlugin {

    private static final String LOGTAG = "[AdjoePlugin]";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.i(LOGTAG, "execute " + action + " with args " + args);
        PluginResult result = null;
        if (action == null) {
            return false;
        }

        if (action.equals("init")) {
            Log.i(LOGTAG, "adjoe SDK initialize");
            String sdkHash = args.getString(0);
            JSONObject options = args.getJSONObject(1);
            String subId1 = args.optString(2, null);
            String subId2 = args.optString(3, null);

            if (sdkHash.isEmpty()) {
                callbackContext.error("called init without sdkHash");
                return false;
            }

            executeInitialize(callbackContext, sdkHash, options, subId1, subId2);

        } else if (action.equals("showOfferwall")) {
            Log.i(LOGTAG, "adjoe SDK show offerwall");
            executeShowOfferwall(callbackContext, args);

        } else if (action.equals("canShowOfferwall")) {
            Log.i(LOGTAG, "adjoe SDK can show offerwall");
            result = executeCanShowOfferwall();

        } else if (action.equals("getRewards")) {
            Log.i(LOGTAG, "adjoe SDK rewards");
            executeGetRewards(callbackContext, args);

        } else if (action.equals("doPayout")) {
            Log.i(LOGTAG, "adjoe SDK payout");
            executeDoPayout(callbackContext, args);

        } else if (action.equals("setProfile")) {
            Log.i(LOGTAG, "adjoe SDK set profile");
            executeSetProfile(callbackContext, args.getString(0), args.getString(1), args.getString(2), args);

        } else if (action.equals("setUAParams")) {
            Log.i(LOGTAG, "adjoe sdk set UA Params");
            executeSetUAParams(callbackContext, args);

        } else if (action.equals("sendUserEvent")) {
            Log.i(LOGTAG, "adjoe SDK sendUserEvent");
            executeSendUserEvent(callbackContext, args.getInt(0), args);

        } else if (action.equals("getVersion")) {
            Log.i(LOGTAG, "adjoe SDK get version");
            result = executeGetVersion();

        } else if (action.equals("getVersionName")) {
            Log.i(LOGTAG, "adjoe SDK get version name");
            result = executeGetVersionName();

        } else if (action.equals("isInitialized")) {
            Log.i(LOGTAG, "adjoe SDK is initialized");
            result = executeIsInitialized();

        } else if (action.equals("hasAcceptedTOS")) {
            Log.i(LOGTAG, "adjoe SDK has accepted TOS");
            result = executeHasAcceptedTOS();

        } else if (action.equals("hasAcceptedUsagePermission")) {
            Log.i(LOGTAG, "adjoe SDK has accepted usage permission");
            result = executeHasAcceptedUsagePermission();

        } else if (action.equals("canUseOfferwallFeatures")) {
            Log.i(LOGTAG, "adjoe SDK can use offerwall features");
            result = executeCanUseOfferwallFeatures();

        } else if (action.equals("getUserId")) {
            Log.i(LOGTAG, "adjoe SDK get user Id");
            result = executeGetUserId();

        } else if (action.equals("a")) {
            Log.i(LOGTAG, "adjoe a");
            a(callbackContext, args.getBoolean(0));
        }

        if (result != null) {
            callbackContext.sendPluginResult(result);
        }
        return true;
    }

    private void executeInitialize(final CallbackContext callbackContext, final String sdkHash, final JSONObject options, final String subId1, final String subId2) {
        cordova.getThreadPool().execute(new Runnable() {

            public void run() {
                Adjoe.Options adjoeOptions = new Adjoe.Options();
                if (options != null) {
                    Log.i(LOGTAG, "options exist");
                    adjoeOptions.setUserId(options.optString("user_id", null))
                            .setParams(provideAdjoeParamsFromOptions(options))
                            .setUserProfile(provideUserProfileFromOptions(options))
                            .setExtensions(provideAdjoeExtensionFromOptions(options));
                }
                adjoeOptions.w("cordova");
                Adjoe.init(cordova.getActivity(), sdkHash, adjoeOptions, subId1, subId2, new AdjoeInitialisationListener() {

                    @Override
                    public void onInitialisationFinished() {
                        Log.i(LOGTAG, "adjoe SDK initialized");
                        callbackContext.success();
                    }

                    @Override
                    public void onInitialisationError(Exception e) {
                        Log.e(LOGTAG, "adjoe SDK not initialized");
                        if (e != null) {
                            e.printStackTrace();
                            callbackContext.error(e.getMessage());
                        } else {
                            callbackContext.error("adjoe SDK could not be initialized");
                        }
                    }
                });
            }
        });
    }

    private void executeShowOfferwall(final CallbackContext callbackContext, JSONArray args) {
        this.cordova.getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    AdjoeParams adjoeParams = getAdjoeUaParams(args, 0, 1);
                    Intent adjoeOfferwallIntent = Adjoe.getOfferwallIntent(cordova.getActivity(), adjoeParams);
                    cordova.getActivity().startActivity(adjoeOfferwallIntent);

                    callbackContext.success();

                } catch (AdjoeNotInitializedException notInitializedException) {
                    Log.e(LOGTAG, "adjoe SDK not initialized", notInitializedException);
                    callbackContext.error(notInitializedException.getMessage());

                } catch (AdjoeException exception) {
                    Log.e(LOGTAG, "adjoe SDK threw an exception", exception);
                    callbackContext.error(exception.getMessage());
                }
            }
        });
    }

    private PluginResult executeCanShowOfferwall() throws JSONException {
        JSONObject message = new JSONObject();
        message.put("can_show", Adjoe.canShowOfferwall(cordova.getActivity()));
        return new PluginResult(PluginResult.Status.OK, message);
    }

    private void executeGetRewards(final CallbackContext callbackContext, JSONArray args) {
        cordova.getThreadPool().execute(new Runnable() {

            public void run() {
                try {
                    AdjoeParams adjoeParams = getAdjoeUaParams(args, 0, 1);

                    Adjoe.requestRewards(cordova.getActivity(), adjoeParams, new AdjoeRewardListener() {

                        @Override
                        public void onUserReceivesReward(AdjoeRewardResponse adjoeRewardResponse) {
                            // successfully requested the rewards
                            // get the total amount of coins which the user has collected
                            int reward = adjoeRewardResponse.getReward();
                            // get the amount of coins which are available for payout
                            int availableForPayout = adjoeRewardResponse.getAvailablePayoutCoins();
                            // get the amount of coins which the user has already spent
                            int alreadySpentCoins = adjoeRewardResponse.getAlreadySpentCoins();


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
                        public void onUserReceivesRewardError(AdjoeRewardResponseError adjoeRewardResponseError) {
                            // an error occurred while requesting the rewards
                            // you can try to get additional information about the error for debugging
                            Log.e(LOGTAG, "adjoe  onUserReceivesRewardError" + adjoeRewardResponseError.toString());

                            if (adjoeRewardResponseError.getException() != null) {
                                adjoeRewardResponseError.getException().printStackTrace();
                                callbackContext.error(adjoeRewardResponseError.getException().getMessage());
                            } else {
                                callbackContext.error("adjoe SDK could not fetch rewards");
                            }
                        }
                    });
                } catch (AdjoeNotInitializedException e) {
                    Log.e(LOGTAG, "adjoe SDK not initialized", e);
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }

    private void executeDoPayout(final CallbackContext callbackContext, JSONArray args) {
        cordova.getThreadPool().execute(new Runnable() {

            public void run() {

                try {
                    AdjoeParams adjoeParams = getAdjoeUaParams(args, 0, 1);
                    Adjoe.doPayout(cordova.getActivity(), adjoeParams, new AdjoePayoutListener() {

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
                        public void onPayoutError(AdjoePayoutError adjoePayoutError) {

                            // an error occurred while paying out the coins

                            // get information about why it failed
                            // 'reason' is one of
                            // - AdjoePayoutError.UNKNOWN
                            // - AdjoePayoutError.NOT_ENOUGH_COINS
                            // - AdjoePayoutError.TOS_NOT_ACCEPTED
                            int reason = adjoePayoutError.getReason();

                            JSONObject message = new JSONObject();
                            try {
                                message.put("reason", reason);
                            } catch (JSONException e) {}

                            // if available, get more information about the error

                            if (adjoePayoutError.getException() != null) {
                                adjoePayoutError.getException().printStackTrace();
                                try {
                                    message.put("exception", adjoePayoutError.getException().getMessage());
                                } catch (JSONException e) {}
                            }
                            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, message));
                        }
                    });
                } catch (AdjoeNotInitializedException e) {
                    // make sure to initialize the adjoe SDK first
                    Log.e(LOGTAG, "adjoe SDK not initialized", e);
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }

    private void executeSetProfile(final CallbackContext callbackContext, final String source, final String genderString, final String birthdayString, JSONArray args) {
        cordova.getThreadPool().execute(new Runnable() {

            @Override
            public void run() {

                AdjoeParams adjoeParams = getAdjoeUaParams(args, 3, 4);
                AdjoeGender gender;
                if (genderString.equalsIgnoreCase("male")) {
                    gender = AdjoeGender.MALE;
                } else if (genderString.equalsIgnoreCase("female")) {
                    gender = AdjoeGender.FEMALE;
                } else {
                    gender = AdjoeGender.UNKNOWN;
                }

                Date birthday = null;
                try {
                    birthday = new SimpleDateFormat("yyyy-MM-dd").parse(birthdayString);
                } catch (ParseException e) {
                    Log.e(LOGTAG, "adjoe SDK birthday is in wrong format: " + birthdayString + "; expected yyyy-MM-dd", e);
                    callbackContext.error(e.getMessage());
                    return;
                }

                try {
                    AdjoeUserProfile userProfile = new AdjoeUserProfile(gender, birthday);
                    Adjoe.setProfile(cordova.getActivity(), source, userProfile, adjoeParams);

                    callbackContext.success();
                } catch (AdjoeNotInitializedException notInitializedException) {
                    Log.e(LOGTAG, "adjoe SDK not initialized", notInitializedException);
                    callbackContext.error(notInitializedException.getMessage());
                }
            }
        });
    }

    private void executeSetUAParams(final CallbackContext callbackContext, JSONArray args) {
        Log.i(LOGTAG, "executeSetUAParams");
        cordova.getThreadPool().execute(() -> {
            try {
                AdjoeParams params = getAdjoeUaParams(args, 0, 0);
                Adjoe.setUAParams(cordova.getActivity(), params);
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
                Adjoe.a(cordova.getActivity(), a);
                callbackContext.success();
            }
        });
    }

    private void executeSendUserEvent(final CallbackContext callbackContext, final int eventId, JSONArray args) {
        cordova.getThreadPool().execute(new Runnable() {

            @Override
            public void run() {
                try {
                    AdjoeParams adjoeParams = getAdjoeUaParams(args, 1, 2);
                    Adjoe.sendUserEvent(cordova.getActivity(), eventId, null, adjoeParams);

                    callbackContext.success();
                } catch (AdjoeNotInitializedException e) {
                    Log.e(LOGTAG, "adjoe SDK not initialized", e);
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }

    private PluginResult executeGetVersion() throws JSONException {
        JSONObject message = new JSONObject();
        message.put("version", Adjoe.getVersion());
        return new PluginResult(PluginResult.Status.OK, message);
    }

    private PluginResult executeGetVersionName() throws JSONException {
        JSONObject message = new JSONObject();
        message.put("version_name", Adjoe.getVersionName());
        return new PluginResult(PluginResult.Status.OK, message);
    }

    private PluginResult executeIsInitialized() throws JSONException {
        JSONObject message = new JSONObject();
        message.put("initialized", Adjoe.isInitialized());
        return new PluginResult(PluginResult.Status.OK, message);
    }

    private PluginResult executeHasAcceptedTOS() throws JSONException {
        JSONObject message = new JSONObject();
        message.put("accepted", Adjoe.hasAcceptedTOS(cordova.getActivity()));
        return new PluginResult(PluginResult.Status.OK, message);
    }

    private PluginResult executeHasAcceptedUsagePermission() throws JSONException {
        JSONObject message = new JSONObject();
        message.put("accepted", Adjoe.hasAcceptedUsagePermission(cordova.getActivity()));
        return new PluginResult(PluginResult.Status.OK, message);
    }

    private PluginResult executeCanUseOfferwallFeatures() throws JSONException {
        JSONObject message = new JSONObject();
        message.put("can_use", Adjoe.canUseOfferwallFeatures(cordova.getActivity()));
        return new PluginResult(PluginResult.Status.OK, message);
    }

    private PluginResult executeGetUserId() throws JSONException {
        JSONObject message = new JSONObject();
        message.put("user_id", Adjoe.getUserId(cordova.getActivity()));
        return new PluginResult(PluginResult.Status.OK, message);
    }

    // region Helper Methods
    private AdjoeUserProfile provideUserProfileFromOptions(JSONObject options) {
        JSONObject userProfile = options.optJSONObject("user_profile");
        return constructAdjoeUserProfile(userProfile);
    }

    private AdjoeUserProfile constructAdjoeUserProfile(JSONObject userProfile) {
        if (userProfile == null) return null;
        String gender = userProfile.optString("gender");
        AdjoeGender adjoeGender;
        switch (gender) {
            case "male":
                adjoeGender = AdjoeGender.MALE;
                break;
            case "female":
                adjoeGender = AdjoeGender.FEMALE;
                break;
            default:
                adjoeGender = AdjoeGender.MALE;
        }
        String date = userProfile.optString("birthdate");
        if (TextUtils.isEmpty(gender) && TextUtils.isEmpty(date)) return null;

        Date birthdate = null;
        try {
            birthdate = DateFormat.getDateInstance().parse(date);
        } catch (ParseException ignore) {
            // nothing
        }
        return new AdjoeUserProfile(adjoeGender, birthdate);
    }

    private AdjoeParams provideAdjoeParamsFromOptions(JSONObject options) {
        JSONObject params = options.optJSONObject("adjoe_params");
        return constructAdjoeParams(params);
    }

    private AdjoeParams constructAdjoeParams(JSONObject params) {
        AdjoeParams.Builder builder = new AdjoeParams.Builder();
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

    private AdjoeParams constructAdjoeParams(String subId1, String subId2) {
        AdjoeParams.Builder builder = new AdjoeParams.Builder();
        if (TextUtils.isEmpty(subId1) && TextUtils.isEmpty(subId2)) return builder.build();
        return builder.setUaNetwork(subId1)
                .setUaChannel(subId2)
                .build();
    }

    private AdjoeExtensions provideAdjoeExtensionFromOptions(JSONObject options) {
        JSONObject extension = options.optJSONObject("adjoe_extension");
        return constructAdjoeExtensions(extension);
    }

    private AdjoeExtensions constructAdjoeExtensions(JSONObject extension) {
        AdjoeExtensions.Builder builder = new AdjoeExtensions.Builder();
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

    private JSONObject getAdjoeUaParamsFromArgs(JSONArray args) {
        Object obj = args.opt(0);
        if (obj == null) return null;
        if (obj instanceof JSONObject) {
            return (JSONObject) obj;
        }
        return null;
    }

    private AdjoeParams getAdjoeUaParams(JSONArray args, int subId1Index, int subId2Index) {
        JSONObject adjoeParmasObj = getAdjoeUaParamsFromArgs(args);
        AdjoeParams adjoeParams;
        if (adjoeParmasObj != null) {
            adjoeParams = constructAdjoeParams(adjoeParmasObj);
        } else {
            adjoeParams = constructAdjoeParams(args.optString(subId1Index, null), args.optString(subId2Index, null));
        }
        return adjoeParams;
    }
    // endregion

}
