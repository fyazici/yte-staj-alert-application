import i18n from 'i18next';
import I18NextXhrBackend from "i18next-xhr-backend";
import I18nextBrowserLanguageDetector from "i18next-browser-languagedetector";
import { initReactI18next } from "react-i18next";

const fallbackLng = ['en'];
const availableLanguages = ['en', 'tr'];

i18n
    .use(I18NextXhrBackend)
    .use(I18nextBrowserLanguageDetector)
    .use(initReactI18next)
    .init({
        fallbackLng: fallbackLng,
        debug: true,
        whitelist: availableLanguages,

        interpolation: {
            escapeValue: false
        }
    });

export default i18n;