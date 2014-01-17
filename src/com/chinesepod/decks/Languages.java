package com.chinesepod.decks;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Button;

import java.util.Locale;
import java.util.Map;

/**
 * Language information for the Google Translate API.
 */
public final class Languages {
    
    /**
     * Reference at http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2
     */
    public static enum Language {
        
//        AFRIKAANS("af", "Afrikaans", R.drawable.af),
//        ALBANIAN("sq", "Albanian"),
//        AMHARIC("am", "Amharic", R.drawable.am),
//        ARABIC("ar", "Arabic", R.drawable.ar),
//        ARMENIAN("hy", "Armenian"),
//        AZERBAIJANI("az", "Azerbaijani", R.drawable.az),
//        BASQUE("eu", "Basque"),
//        BELARUSIAN("be", "Belarusian", R.drawable.be),
//        BENGALI("bn", "Bengali", R.drawable.bn),
//        BIHARI("bh", "Bihari", R.drawable.bh),
        
        BULGARIAN("bg", "Bulgarian", R.drawable.bg),

//        BURMESE("my", "Burmese", R.drawable.my),
        CATALAN("ca", "Catalan"),
        
        CHINESE("zh", "Chinese", R.drawable.cn),
        CHINESE_SIMPLIFIED("zh-CN", "Chinese simplified", R.drawable.cn),
        CHINESE_TRADITIONAL("zh-TW", "Chinese traditional", R.drawable.tw),
        CROATIAN("hr", "Croatian", R.drawable.hr),
        CZECH("cs", "Czech", R.drawable.cs),
        
        DANISH("da", "Danish", R.drawable.dk),
//        DHIVEHI("dv", "Dhivehi"),
        
        DUTCH("nl", "Dutch", R.drawable.nl),
        ENGLISH("en", "English", R.drawable.us),
        
//        ESPERANTO("eo", "Esperanto"),
//        ESTONIAN("et", "Estonian", R.drawable.et),
        FILIPINO("tl", "Filipino", R.drawable.ph),

        FINNISH("fi", "Finnish", R.drawable.fi),
        FRENCH("fr", "French", R.drawable.fr),
        
//        GALICIAN("gl", "Galician", R.drawable.gl),
//        GEORGIAN("ka", "Georgian"),
        
        GERMAN("de", "German", R.drawable.de),
        
        GREEK("el", "Greek", R.drawable.gr),
//        GUARANI("gn", "Guarani", R.drawable.gn),
//        GUJARATI("gu", "Gujarati", R.drawable.gu),
//        HEBREW("iw", "Hebrew", R.drawable.il),
//        HINDI("hi", "Hindi"),
//        HUNGARIAN("hu", "Hungarian", R.drawable.hu),
//        ICELANDIC("is", "Icelandic", R.drawable.is),
        INDONESIAN("id", "Indonesian", R.drawable.id),
//        INUKTITUT("iu", "Inuktitut"),
        
        ITALIAN("it", "Italian", R.drawable.it),
        JAPANESE("ja", "Japanese", R.drawable.jp),
        
//        KANNADA("kn", "Kannada", R.drawable.kn),
//        KAZAKH("kk", "Kazakh"),
//        KHMER("km", "Khmer", R.drawable.km),
        
        KOREAN("ko", "Korean", R.drawable.kr),
        
//        KURDISH("ky", "Kurdish", R.drawable.ky),
//        LAOTHIAN("lo", "Laothian"),
//        LATVIAN("la", "Latvian", R.drawable.la),
        LITHUANIAN("lt", "Lithuanian", R.drawable.lt),
//        MACEDONIAN("mk", "Macedonian", R.drawable.mk),
//        MALAY("ms", "Malay", R.drawable.ms),
//        MALAYALAM("ml", "Malayalam", R.drawable.ml),
//        MALTESE("mt", "Maltese", R.drawable.mt),
//        MARATHI("mr", "Marathi", R.drawable.mr),
//        MONGOLIAN("mn", "Mongolian", R.drawable.mn),
//        NEPALI("ne", "Nepali", R.drawable.ne),
        
        NORWEGIAN("no", "Norwegian", R.drawable.no),
        
//        ORIYA("or", "Oriya"),
//        PASHTO("ps", "Pashto", R.drawable.ps),
//        PERSIAN("fa", "Persian"),
        
        POLISH("pl", "Polish", R.drawable.pl),
        PORTUGUESE("pt", "Portuguese", R.drawable.pt),
        
//        PUNJABI("pa", "Punjabi", R.drawable.pa),
        
        ROMANIAN("ro", "Romanian", R.drawable.ro),
        RUSSIAN("ru", "Russian", R.drawable.ru),
        
//        SANSKRIT("sa", "Sanskrit", R.drawable.sa),
        SERBIAN("sr", "Serbian", R.drawable.sr),
//        SINDHI("sd", "Sindhi", R.drawable.sd),
//        SINHALESE("si", "Sinhalese", R.drawable.si),
        SLOVAK("sk", "Slovak", R.drawable.sk),
        SLOVENIAN("sl", "Slovenian", R.drawable.sl),
        
        SPANISH("es", "Spanish", R.drawable.es),
        
//        SWAHILI("sw", "Swahili"),
        
        SWEDISH("sv", "Swedish", R.drawable.sv),
        
//        TAJIK("tg", "Tajik", R.drawable.tg),
//        TAMIL("ta", "Tamil"),
        
        TAGALOG("tl", "Tagalog", R.drawable.ph),
//        TELUGU("te", "Telugu"),
//        THAI("th", "Thai", R.drawable.th),
//        TIBETAN("bo", "Tibetan", R.drawable.bo),
//        TURKISH("tr", "Turkish", R.drawable.tr),
        UKRAINIAN("uk", "Ukrainian", R.drawable.ua),
//        URDU("ur", "Urdu"),
//        UZBEK("uz", "Uzbek", R.drawable.uz),
//        UIGHUR("ug", "Uighur", R.drawable.ug),

        ;
        
        private String mShortName;
        private String mLongName;
        private int mFlag;
        
        private static Map<String, String> mLongNameToShortName = Maps.newHashMap();
        private static Map<String, Language> mShortNameToLanguage = Maps.newHashMap();
        
        static {
            for (Language language : values()) {
                mLongNameToShortName.put(language.getLongName(), language.getShortName());
                mShortNameToLanguage.put(language.getShortName(), language);
            }
        }
        
        private Language(String shortName, String longName, int flag) {
            init(shortName, longName, flag);
        }
        
        private Language(String shortName, String longName) {
            init(shortName, longName, -1);
        }

        private void init(String shortName, String longName, int flag) {
            mShortName = shortName;
            mLongName = longName;
            mFlag = flag;
            
        }

        public String getShortName() {
            return mShortName;
        }

        public String getLongName() {
            return mLongName;
        }
        
        public int getFlag() {
            return mFlag;
        }

        @Override
        public String toString() {
            return mLongName;
        }
        
        public static Language findLanguageByShortName(String shortName) {
            return mShortNameToLanguage.get(shortName);
        }
        
        public void configureButton(Activity activity, Button button) {
            button.setTag(this);
            button.setText(getLongName());
            int f = getFlag();
            if (f != -1) {
                Drawable flag = activity.getResources().getDrawable(f);
                button.setCompoundDrawablesWithIntrinsicBounds(flag, null, null, null);
                button.setCompoundDrawablePadding(5);
            }
        }
    }

    public static String getShortName(String longName) {
        return Language.mLongNameToShortName.get(longName);
    }

    @SuppressWarnings("unused")
	private static void log(String s) {
        Log.d("Languages", s);
    }

	public static Locale toLocale(Language language) {
		switch (language) {
			case ENGLISH:
				return Locale.ENGLISH;
			case FRENCH:
				return Locale.FRENCH;
			case GERMAN:
				return Locale.GERMAN;
			case ITALIAN:
				return Locale.ITALIAN;
			case SPANISH:
				return new Locale("es_ES");
			case CHINESE:
				return Locale.CHINESE;
			case CHINESE_SIMPLIFIED:
				return Locale.CHINA;
			case CHINESE_TRADITIONAL:
				return Locale.TAIWAN;
			default:
				return new Locale(language.getShortName());
		}
	}

}

