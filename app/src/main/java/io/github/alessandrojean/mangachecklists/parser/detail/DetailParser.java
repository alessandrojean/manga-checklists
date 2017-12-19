package io.github.alessandrojean.mangachecklists.parser.detail;

import android.net.Uri;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import io.github.alessandrojean.mangachecklists.domain.Manga;

/**
 * Created by Desktop on 18/12/2017.
 */

public abstract class DetailParser {
    protected static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36";

    protected Manga manga;

    public DetailParser(Manga manga) {
        this.manga = manga;
    }

    public Manga getDetails() {
        if (manga.getUrl() == null)
            return manga;

        Document html;
        Log.i("details", "Getting details: " + manga.getUrl());

        try {
            html = Jsoup
                    .connect(manga.getUrl())
                    .userAgent(USER_AGENT)
                    .get();

            manga = getManga(html);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return manga;
    }

    protected Manga getManga(Document html) {
        return null;
    }

    protected String fixUrl(String urlStr) {
        return Uri.encode(urlStr, "@#&=*+-_.,:!?()/~'%");
    }

    protected String formatCurrency(double currency) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String currencySymbol = currencyFormatter.format(0.00).replace("0.00", "");
        return currencyFormatter.format(currency).replace(currencySymbol, currencySymbol + " ");
    }
}
