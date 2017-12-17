package io.github.alessandrojean.mangachecklists.constant;

/**
 * Created by Desktop on 15/12/2017.
 */

public class JBC {
    public static final String URL = "https://mangasjbc.com.br/checklist-%s-%d/";
    public static final String CSS_SELECT_BEFORE_MARCH_2016 = "ul.checklist li";
    public static final String CSS_SELECT_AFTER_MARCH_2016 = "ul.checklist li.adjust-font-size a";

    public static final String URL_PLAN = "https://mangasjbc.com.br/assinantes/";
    public static final String CSS_SELECT_PLAN = "div.box.mb-md ul li";

    public static final String PATTERN_INFO = "^(.*?)(?: #(\\d+))?(?: (?:–|-) (\\d+)ª temp.)?(?: (?:–|-) (\\d+)\\/(\\d+))?$";
    public static final String PATTERN_IMAGE = "^(?:.*)(\\d{3})x(\\d+).(?:.*)$";
    public static final String PATTERN_INFO_PLAN = "^(.*?)(?: #(\\d+))?$";
    public static final String PATTERN_PLAN = "^(?:.*?) - enviado em (\\d+)\\/(\\d+)\\/(\\d+)(?: - (.*))?";

    public static final int MINIMUM_MONTH = 11;
    public static final int MINIMUM_YEAR = 2013;

    public static final String MANGA_LIST_KEY = "jbc_manga_list_key_";
    public static final String PLANS_LIST_KEY = "jbc_plans_list_key";
}
