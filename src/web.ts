import { WebPlugin } from '@capacitor/core';
import { OneShopSmsPlugin } from './definitions';

export class OneShopSmsWeb extends WebPlugin implements OneShopSmsPlugin {
  constructor() {
    super({
      name: 'OneShopSms',
      platforms: ['web'],
    });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  async openMessanger(
    phoneNumber: string,
    body?: string,
    photos?: string[],
  ): Promise<void> {
    console.log('web openMessanger 3');
    window.open(`sms:${phoneNumber}&body=${encodeURI(body || '')}`, '_self');
    // `sms:${phoneNumber}&body=${encodeURI(state.message || '')}`
    // window.open("https://www.w3schools.com");
  }
}

const OneShopSms = new OneShopSmsWeb();

export { OneShopSms };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(OneShopSms);
