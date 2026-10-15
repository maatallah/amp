import React, { useState } from 'react';
import { LocalizationProvider, useTranslation } from './context/LocalizationContext';
import { ThemeProvider, useTheme } from './context/ThemeContext';
import { NativeMusicScanner } from './services/NativeMusicScanner';

const MainScreen: React.FC = () => {
    const { t, locale, setLocale } = useTranslation();
    const { themeMode, setThemeMode, tokens } = useTheme();
    const [scanResult, setScanResult] = useState<string>('');
    const [loading, setLoading] = useState<boolean>(false);

    const handleScan = async () => {
        try {
            setLoading(true);
            const result = await NativeMusicScanner.scanDirectories(['/storage/emulated/0/Music']);
            setScanResult(result);
        } catch (e: any) {
            setScanResult(`Error: ${e.message}`);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ backgroundColor: tokens.background, color: tokens.text, padding: 20, flex: 1, minHeight: '100vh' }}>
            <h1>{t('app_name')}</h1>
            
            <div style={{ margin: '20px 0', padding: 15, backgroundColor: tokens.card, border: `1px solid ${tokens.border}`, borderRadius: 8 }}>
                <h3>Localization Settings</h3>
                <p>Current Locale: {locale}</p>
                <button onClick={() => setLocale(locale === 'en' ? 'es' : 'en')}>
                    Switch Language (EN / ES)
                </button>
            </div>

            <div style={{ margin: '20px 0', padding: 15, backgroundColor: tokens.card, border: `1px solid ${tokens.border}`, borderRadius: 8 }}>
                <h3>Theme Settings</h3>
                <p>Current Theme: {themeMode}</p>
                <button onClick={() => setThemeMode(themeMode === 'light' ? 'dark' : 'light')}>
                    Toggle Light / Dark
                </button>
            </div>

            <div style={{ margin: '20px 0', padding: 15, backgroundColor: tokens.card, border: `1px solid ${tokens.border}`, borderRadius: 8 }}>
                <h3>Library Scanner Bridge</h3>
                <button onClick={handleScan} disabled={loading}>
                    {loading ? 'Scanning...' : 'Trigger Background Scan'}
                </button>
                {scanResult ? <p style={{ marginTop: 10 }}>{scanResult}</p> : null}
            </div>
        </div>
    );
};

const App: React.FC = () => {
    return (
        <ThemeProvider>
            <LocalizationProvider>
                <MainScreen />
            </LocalizationProvider>
        </ThemeProvider>
    );
};

export default App;
