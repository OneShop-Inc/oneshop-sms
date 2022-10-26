import { WebPlugin } from '@capacitor/core';
export class OneShopSmsWeb extends WebPlugin {
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
//# sourceMappingURL=web.js.map