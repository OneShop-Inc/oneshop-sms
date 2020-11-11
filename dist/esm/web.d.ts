import { WebPlugin } from '@capacitor/core';
import { OneShopSmsPlugin, OpenMessengerOptions } from './definitions';
export declare class OneShopSmsWeb extends WebPlugin implements OneShopSmsPlugin {
    constructor();
    openMessenger(options: OpenMessengerOptions): Promise<void>;
}
declare const OneShopSms: OneShopSmsWeb;
export { OneShopSms };
