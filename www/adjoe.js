function AdjoePlugin() {}

AdjoePlugin.prototype.ERR_PAYOUT_UNKNOWN = 0;
AdjoePlugin.prototype.ERR_PAYOUT_NOT_ENOUGH = 400;
AdjoePlugin.prototype.ERR_PAYOUT_TOS = 1;

AdjoePlugin.prototype.EVENT_TEASER_SHOWN = 14;

AdjoePlugin.prototype.initialize = function (sdkHash, options, success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'init', [sdkHash, options, null, null]);
};

/**
 * @deprecated Please use initialize method
 */
AdjoePlugin.prototype.initializeWithSubIDs = function (sdkHash, options, subId1, subId2, success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'init', [sdkHash, options, subId1, subId2]);
}

AdjoePlugin.prototype.showOfferwall = function (success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'showOfferwall', [null, null]);
};

/**
 * @deprecated Please use methods that is *WithUaParams
 */
AdjoePlugin.prototype.showOfferwallWithSubIDs = function (subId1, subId2, success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'showOfferwall', [subId1, subId2]);
};

AdjoePlugin.prototype.showOfferwallWithUaParams = function (params, success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'showOfferwall', [params]);
};

AdjoePlugin.prototype.canShowOfferwall = function (success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'canShowOfferwall', []);
}

AdjoePlugin.prototype.getRewards = function (success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'getRewards', [null, null]);
};

/**
 * @deprecated Please use methods that is *WithUaParams
 */
AdjoePlugin.prototype.getRewardsWithSubIDs = function (subId1, subId2, success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'getRewards', [subId1, subId2]);
}

AdjoePlugin.prototype.getRewardsWithUaParams = function (params, success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'getRewards', [params]);
}

AdjoePlugin.prototype.doPayout = function (success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'doPayout', [null, null]);
};

AdjoePlugin.prototype.doPayoutWithSubIDs = function (subId1, subId2, success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'doPayout', [subId1, subId2]);
};

AdjoePlugin.prototype.doPayoutWithUaParams = function (params, success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'doPayout', [params]);
};

AdjoePlugin.prototype.setProfile = function (source, gender, birthday, success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'setProfile', [source, gender, birthday, null, null]);
}

/**
 * @deprecated Please use methods that is *WithUaParams
 */
AdjoePlugin.prototype.setProfileWithSubIDs = function (source, gender, birthday, subId1, subId2, success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'setProfile', [source, gender, birthday, subId1, subId2]);
}

AdjoePlugin.prototype.setProfileWithUaParams = function (source, gender, birthday, params, success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'setProfile', [source, gender, birthday, params]);
}

AdjoePlugin.prototype.setUAParams = function (params, success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'setUAParams', [params]);
}

AdjoePlugin.prototype.sendUserEvent = function (eventId, success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'sendUserEvent', [eventId, null, null]);
}

/**
 * @deprecated Please use methods that is *WithUaParams
 */
AdjoePlugin.prototype.sendUserEventWithSubIDs = function (eventId, subId1, subId2, success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'sendUserEvent', [eventId, subId1, subId2]);
}

AdjoePlugin.prototype.sendUserEventWithUaParams = function (eventId, params, success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'sendUserEvent', [eventId, params]);
}

AdjoePlugin.prototype.getVersion = function (success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'getVersion', []);
}

AdjoePlugin.prototype.getVersionName = function (success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'getVersionName', []);
}

AdjoePlugin.prototype.isInitialized = function (success, error){
	cordova.exec(success, error, 'AdjoePlugin', 'isInitialized', []);
}

AdjoePlugin.prototype.hasAcceptedTOS = function (success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'hasAcceptedTOS', []);
}

AdjoePlugin.prototype.hasAcceptedUsagePermission = function (success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'hasAcceptedUsagePermission', []);
}

AdjoePlugin.prototype.canUseOfferwallFeatures = function (success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'canUseOfferwallFeatures', []);
}

AdjoePlugin.prototype.getUserId = function (success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'getUserId', []);
}

AdjoePlugin.prototype._a = function (a, success, error) {
	cordova.exec(success, error, 'AdjoePlugin', 'a', [a]);
}

module.exports = new AdjoePlugin();
