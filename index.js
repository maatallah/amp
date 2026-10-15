import { AppRegistry } from 'react-native';
import App from './src/App';

// This string must explicitly match the return value of getMainComponentName() in MainActivity.kt
AppRegistry.registerComponent('AutoMusicPlayer', () => App);
