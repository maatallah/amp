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

    const cycleLocale = () => {
        if (locale === 'en') setLocale('es');
        else if (locale === 'es') setLocale('ar');
        else setLocale('en');
    };

    return (
        <div style={{ backgroundColor: '#121212', color: '#FFFFFF', paddingInlineStart: 24, paddingInlineEnd: 24, paddingTop: 24, flex: 1, minHeight: '100vh', direction: locale === 'ar' ? 'rtl' : 'ltr' }}>
            <h1 style={{ fontSize: 32, fontWeight: 'bold', marginBottom: 24 }}>{t('app_name')}</h1>
            
            <div style={{ marginBlock: 16, padding: 20, backgroundColor: '#1E1E1E', border: '1px solid #333333', borderRadius: 12 }}>
                <h3 style={{ fontSize: 20, marginBottom: 8 }}>{t('nav_settings')}</h3>
                <p style={{ marginBlockEnd: 12 }}>Current Locale: {locale.toUpperCase()}</p>
                <button 
                    onClick={cycleLocale}
                    style={{ minWidth: 160, minHeight: 80, backgroundColor: '#333333', color: '#FFFFFF', fontSize: 18, fontWeight: 'bold', border: 'none', borderRadius: 8, cursor: 'pointer' }}
                >
                    Switch Language (EN / ES / AR)
                </button>
            </div>

            <div style={{ marginBlock: 16, padding: 20, backgroundColor: '#1E1E1E', border: '1px solid #333333', borderRadius: 12 }}>
                <h3 style={{ fontSize: 20, marginBottom: 8 }}>Theme Settings</h3>
                <p style={{ marginBlockEnd: 12 }}>Current Theme: {themeMode}</p>
                <button 
                    onClick={() => setThemeMode(themeMode === 'light' ? 'dark' : 'light')}
                    style={{ minWidth: 160, minHeight: 80, backgroundColor: '#333333', color: '#FFFFFF', fontSize: 18, fontWeight: 'bold', border: 'none', borderRadius: 8, cursor: 'pointer' }}
                >
                    Toggle Light / Dark
                </button>
            </div>

            <div style={{ marginBlock: 16, padding: 20, backgroundColor: '#1E1E1E', border: '1px solid #333333', borderRadius: 12 }}>
                <h3 style={{ fontSize: 20, marginBottom: 8 }}>Library Scanner Bridge</h3>
                <button 
                    onClick={handleScan} 
                    disabled={loading}
                    style={{ minWidth: 200, minHeight: 80, backgroundColor: '#1DB954', color: '#FFFFFF', fontSize: 18, fontWeight: 'bold', border: 'none', borderRadius: 8, cursor: 'pointer' }}
                >
                    {loading ? 'Scanning...' : 'Trigger Background Scan'}
                </button>
                {scanResult ? <p style={{ marginTop: 12, fontSize: 16 }}>{scanResult}</p> : null}
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
