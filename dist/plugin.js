var capacitorOneShopSms = (function (exports, core) {
    'use strict';

    const OneShopSms = core.registerPlugin('OneShopSms', {
        web: () => Promise.resolve().then(function () { return web; }).then(m => new m.OneShopSmsWeb()),
    });

    class OneShopSmsWeb extends core.WebPlugin {
        async openMessenger(options) {
            window.open(`sms:${options.number}&body=${encodeURI(options.body || '')}`, '_self');
        }
    }

    var web = /*#__PURE__*/Object.freeze({
        __proto__: null,
        OneShopSmsWeb: OneShopSmsWeb
    });

    exports.OneShopSms = OneShopSms;

    Object.defineProperty(exports, '__esModule', { value: true });

    return exports;

})({}, capacitorExports);
//# sourceMappingURL=plugin.js.map
