package com.chinesepod.decks;

/**
 * AIDL for the TranslateService (generates ITranslate.java).
 *
 * @author Daniel Rall,  Yusuf Saib
 */
interface ITranslate {
    String translate(in String text, in String from, in String to);
    String findImage(in String text);
    int getVersion();
}
