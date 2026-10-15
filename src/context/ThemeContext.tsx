import React, { createContext, useContext, useState, ReactNode } from 'react';

type ThemeMode = 'light' | 'dark' | 'system';

interface ThemeTokens {
    background: string;
    card: string;
    text: string;
    primary: string;
    border: string;
}

const lightTokens: ThemeTokens = {
    background: '#FFFFFF',
    card: '#F8F9FA',
    text: '#212529',
    primary: '#007BFF',
    border: '#DEE2E6'
};

const darkTokens: ThemeTokens = {
    background: '#121212',
    card: '#1E1E1E',
    text: '#F8F9FA',
    primary: '#0D6EFD',
    border: '#2D2D2D'
};

interface ThemeContextProps {
    themeMode: ThemeMode;
    setThemeMode: (mode: ThemeMode) => void;
    tokens: ThemeTokens;
}

const ThemeContext = createContext<ThemeContextProps | undefined>(undefined);

export const ThemeProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [themeMode, setThemeMode] = useState<ThemeMode>('system');

    const tokens = themeMode === 'dark' ? darkTokens : lightTokens;

    return (
        <ThemeContext.Provider value={{ themeMode, setThemeMode, tokens }}>
            {children}
        </ThemeContext.Provider>
    );
};

export const useTheme = (): ThemeContextProps => {
    const context = useContext(ThemeContext);
    if (!context) {
        throw new Error('useTheme must be used within a ThemeProvider');
    }
    return context;
};
