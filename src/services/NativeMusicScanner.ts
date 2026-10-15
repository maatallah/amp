import { NativeModules } from 'react-native';

interface MusicScannerInterface {
    scanDirectories(paths: string[]): Promise<string>;
}

const { MusicScannerModule } = NativeModules;

export const NativeMusicScanner: MusicScannerInterface = {
    scanDirectories: async (paths: string[]): Promise<string> => {
        if (!MusicScannerModule) {
            throw new Error('MusicScannerModule native module is not linked or available.');
        }
        return await MusicScannerModule.scanDirectories(paths);
    }
};
