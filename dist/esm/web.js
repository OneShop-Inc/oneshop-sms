import { WebPlugin } from '@capacitor/core';
export class OneShopSmsWeb extends WebPlugin {
    async openMessenger(options) {
        window.open(`sms:${options.number}&body=${encodeURI(options.body || '')}`, '_self');
    }
}
//# sourceMappingURL=web.js.map