declare module '@capacitor/core' {
  interface PluginRegistry {
    OneShopSms: OneShopSmsPlugin;
  }
}

export interface OneShopSmsPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  openMessanger(
    phoneNumber: string,
    body?: string,
    photos?: string[],
  ): Promise<void>;
}
