import { AppRegistry, I18nManager } from 'react-native';
import App from './src/App';

I18nManager.allowRTL(true);

// This string must explicitly match the return value of getMainComponentName() in MainActivity.kt
AppRegistry.registerComponent('AutoMusicPlayer', () => App);
