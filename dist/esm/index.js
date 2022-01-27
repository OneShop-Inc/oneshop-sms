import { registerPlugin } from '@capacitor/core';
const OneShopSms = registerPlugin('OneShopSms', {
    web: () => import('./web').then(m => new m.OneShopSmsWeb()),
});
export * from './definitions';
export { OneShopSms };
//# sourceMappingURL=index.js.map