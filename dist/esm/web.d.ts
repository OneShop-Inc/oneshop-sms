import { WebPlugin } from '@capacitor/core';
import { OneshopSmsPlugin, OpenMessengerOptions } from './definitions';
export declare class OneshopSmsWeb extends WebPlugin implements OneshopSmsPlugin {
    constructor();
    openMessenger(options: OpenMessengerOptions): Promise<void>;
}
declare const OneshopSms: OneshopSmsWeb;
export { OneshopSms };
