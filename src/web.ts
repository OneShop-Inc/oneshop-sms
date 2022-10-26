import { WebPlugin } from '@capacitor/core';
import { CanShareResult, OneShopSmsPlugin, OpenMessengerOptions } from './definitions';

export class OneShopSmsWeb extends WebPlugin implements OneShopSmsPlugin {
  async openMessenger(options: OpenMessengerOptions): Promise<void> {
    window.open(`sms:${options.number}&body=${encodeURI(options.body || '')}`, '_self');
  }

  async share(): Promise<void> {
    throw this.unavailable('share is not available in this browser');
  }

  async canShare(): Promise<CanShareResult> {
    return Promise.resolve({ value: false });
  }
}
