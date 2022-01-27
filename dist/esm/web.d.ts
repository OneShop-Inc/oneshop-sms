import { WebPlugin } from '@capacitor/core';
import { OneShopSmsPlugin, OpenMessengerOptions } from './definitions';
export declare class OneShopSmsWeb extends WebPlugin implements OneShopSmsPlugin {
    openMessenger(options: OpenMessengerOptions): Promise<void>;
}
