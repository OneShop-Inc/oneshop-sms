import { WebPlugin } from '@capacitor/core';
import { CanShareResult, OneShopSmsPlugin, OpenMessengerOptions } from './definitions';
export declare class OneShopSmsWeb extends WebPlugin implements OneShopSmsPlugin {
    openMessenger(options: OpenMessengerOptions): Promise<void>;
    share(): Promise<void>;
    canShare(): Promise<CanShareResult>;
}
