import { WebPlugin } from '@capacitor/core';
import { OneshopSmsPlugin, OpenMessengerOptions } from './definitions';

export class OneshopSmsWeb extends WebPlugin implements OneshopSmsPlugin {
  constructor() {
    super({
      name: 'OneshopSms',
      platforms: ['web'],
    });
  }

  async openMessenger(options: OpenMessengerOptions): Promise<void> {
    window.open(
      `sms:${options.number}&body=${encodeURI(options.body || '')}`,
      '_self',
    );
  }
}

const OneshopSms = new OneshopSmsWeb();

export { OneshopSms };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(OneshopSms);
