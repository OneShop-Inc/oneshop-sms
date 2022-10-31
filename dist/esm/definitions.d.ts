export interface OneShopSmsPlugin {
    openMessenger(options: OpenMessengerOptions): Promise<void>;
    share(options: ShareOptions): Promise<void>;
    canShare(): Promise<CanShareResult>;
}
export interface OpenMessengerOptions {
    number: string;
    body?: string;
    attachments?: string[];
}
export interface ShareOptions {
    image: string;
    appId?: string;
    topColor?: string;
    bottomColor?: string;
    shareToStories?: boolean;
}
export interface CanShareResult {
    value: boolean;
}
