import { registerPlugin } from '@capacitor/core';
import type { OneShopSmsPlugin } from './definitions';

const OneShopSms = registerPlugin<OneShopSmsPlugin>('OneShopSms', {
  web: () => import('./web').then(m => new m.OneShopSmsWeb()),
});

export * from './definitions';
export { OneShopSms };
