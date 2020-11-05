declare global {
  interface PluginRegistry {
    OneShopSms?: OneShopSmsPlugin;
  }
}

export interface OneShopSmsPlugin {
  openMessanger(options: OpenMessangerOptions): Promise<void>;
}

export interface OpenMessangerOptions {
  number: string;
  body: string;
  attachments: string[];
}
