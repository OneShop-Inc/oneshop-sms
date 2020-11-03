import { WebPlugin } from '@capacitor/core';
import { OneShopSmsPlugin, OpenMessangerOptions } from './definitions';

export class OneShopSmsWeb extends WebPlugin implements OneShopSmsPlugin {
  constructor() {
    super({
      name: 'OneShopSms',
      platforms: ['web'],
    });
  }

  async openMessanger(options: OpenMessangerOptions): Promise<void> {
    window.open(
      `sms:${options.number}&body=${encodeURI(options.body || '')}`,
      '_self',
    );
  }
}

const OneShopSms = new OneShopSmsWeb();

export { OneShopSms };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(OneShopSms);
