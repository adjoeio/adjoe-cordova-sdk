function PlaytimePlugin() {}

PlaytimePlugin.prototype.ERR_PAYOUT_UNKNOWN = 0;
PlaytimePlugin.prototype.ERR_PAYOUT_NOT_ENOUGH = 400;
PlaytimePlugin.prototype.ERR_PAYOUT_TOS = 1;

PlaytimePlugin.prototype.EVENT_TEASER_SHOWN = 14;

PlaytimePlugin.prototype.initialize = function (sdkHash, options, success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'init', [sdkHash, options, null, null]);
};

/**
 * @deprecated Please use initialize method
 */
PlaytimePlugin.prototype.initializeWithSubIDs = function (sdkHash, options, subId1, subId2, success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'init', [sdkHash, options, subId1, subId2]);
}

PlaytimePlugin.prototype.showCatalog = function (success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'showCatalog', [null, null]);
};

/**
 * @deprecated Please use methods that is *WithUaParams
 */
PlaytimePlugin.prototype.showCatalogWithSubIDs = function (subId1, subId2, success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'showCatalog', [subId1, subId2]);
};

PlaytimePlugin.prototype.showCatalogWithUaParams = function (params, success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'showCatalog', [params]);
};


PlaytimePlugin.prototype.getRewards = function (success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'getRewards', [null, null]);
};

/**
 * @deprecated Please use methods that is *WithUaParams
 */
PlaytimePlugin.prototype.getRewardsWithSubIDs = function (subId1, subId2, success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'getRewards', [subId1, subId2]);
}

PlaytimePlugin.prototype.getRewardsWithUaParams = function (params, success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'getRewards', [params]);
}

PlaytimePlugin.prototype.doPayout = function (success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'doPayout', [null, null]);
};

PlaytimePlugin.prototype.doPayoutWithSubIDs = function (subId1, subId2, success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'doPayout', [subId1, subId2]);
};

PlaytimePlugin.prototype.doPayoutWithUaParams = function (params, success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'doPayout', [params]);
};

PlaytimePlugin.prototype.setProfile = function (source, gender, birthday, success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'setProfile', [source, gender, birthday, null, null]);
}

/**
 * @deprecated Please use methods that is *WithUaParams
 */
PlaytimePlugin.prototype.setProfileWithSubIDs = function (source, gender, birthday, subId1, subId2, success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'setProfile', [source, gender, birthday, subId1, subId2]);
}

PlaytimePlugin.prototype.setProfileWithUaParams = function (source, gender, birthday, params, success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'setProfile', [source, gender, birthday, params]);
}

PlaytimePlugin.prototype.setUAParams = function (params, success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'setUAParams', [params]);
}

PlaytimePlugin.prototype.sendUserEvent = function (eventId, success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'sendUserEvent', [eventId, null, null]);
}

/**
 * @deprecated Please use methods that is *WithUaParams
 */
PlaytimePlugin.prototype.sendUserEventWithSubIDs = function (eventId, subId1, subId2, success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'sendUserEvent', [eventId, subId1, subId2]);
}

PlaytimePlugin.prototype.sendUserEventWithUaParams = function (eventId, params, success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'sendUserEvent', [eventId, params]);
}

PlaytimePlugin.prototype.getVersion = function (success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'getVersion', []);
}

PlaytimePlugin.prototype.getVersionName = function (success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'getVersionName', []);
}

PlaytimePlugin.prototype.isInitialized = function (success, error){
	cordova.exec(success, error, 'PlaytimePlugin', 'isInitialized', []);
}

PlaytimePlugin.prototype.hasAcceptedTOS = function (success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'hasAcceptedTOS', []);
}

PlaytimePlugin.prototype.hasAcceptedUsagePermission = function (success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'hasAcceptedUsagePermission', []);
}

PlaytimePlugin.prototype.canUseCatalogFeatures = function (success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'canUseCatalogFeatures', []);
}

PlaytimePlugin.prototype.getUserId = function (success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'getUserId', []);
}

PlaytimePlugin.prototype._a = function (a, success, error) {
	cordova.exec(success, error, 'PlaytimePlugin', 'a', [a]);
}

module.exports = new PlaytimePlugin();
