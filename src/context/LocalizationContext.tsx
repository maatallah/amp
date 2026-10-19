import React, { createContext, useContext, useState, ReactNode } from 'react';
import en from '../localization/en.json';
import es from '../localization/es.json';
import ar from '../localization/ar.json';

type Locale = 'en' | 'es' | 'ar';
type Dictionary = typeof en;

interface LocalizationContextProps {
    locale: Locale;
    setLocale: (locale: Locale) => void;
    t: (key: keyof Dictionary) => string;
}

const dictionaries: Record<Locale, Dictionary> = { en, es, ar };

const LocalizationContext = createContext<LocalizationContextProps | undefined>(undefined);

export const LocalizationProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [locale, setLocale] = useState<Locale>('en');

    const t = (key: keyof Dictionary): string => {
        const dictionary = dictionaries[locale] || dictionaries.en;
        return dictionary[key] || en[key] || key;
    };

    return (
        <LocalizationContext.Provider value={{ locale, setLocale, t }}>
            {children}
        </LocalizationContext.Provider>
    );
};

export const useTranslation = (): LocalizationContextProps => {
    const context = useContext(LocalizationContext);
    if (!context) {
        throw new Error('useTranslation must be used within a LocalizationProvider');
    }
    return context;
};
