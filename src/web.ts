import { WebPlugin } from '@capacitor/core';
import { OneShopSmsPlugin, OpenMessengerOptions } from './definitions';

export class OneShopSmsWeb extends WebPlugin implements OneShopSmsPlugin {
  async openMessenger(options: OpenMessengerOptions): Promise<void> {
    window.open(
      `sms:${options.number}&body=${encodeURI(options.body || '')}`,
      '_self',
    );
  }
}
