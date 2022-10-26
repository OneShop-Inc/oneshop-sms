'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var core = require('@capacitor/core');

const OneShopSms = core.registerPlugin('OneShopSms', {
    web: () => Promise.resolve().then(function () { return web; }).then(m => new m.OneShopSmsWeb()),
});

class OneShopSmsWeb extends core.WebPlugin {
    async openMessenger(options) {
        window.open(`sms:${options.number}&body=${encodeURI(options.body || '')}`, '_self');
    }
    async share() {
        throw this.unavailable('share is not available in this browser');
    }
    async canShare() {
        return Promise.resolve({ value: false });
    }
}

var web = /*#__PURE__*/Object.freeze({
    __proto__: null,
    OneShopSmsWeb: OneShopSmsWeb
});

exports.OneShopSms = OneShopSms;
//# sourceMappingURL=plugin.cjs.js.map
