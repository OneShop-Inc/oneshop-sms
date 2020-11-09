declare global {
    interface PluginRegistry {
        OneshopSms?: OneshopSmsPlugin;
    }
}
export interface OneshopSmsPlugin {
    openMessenger(options: OpenMessengerOptions): Promise<void>;
}
export interface OpenMessengerOptions {
    number: string;
    body: string;
    attachments: string[];
}
