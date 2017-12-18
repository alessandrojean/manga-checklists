package io.github.alessandrojean.mangachecklists.parser.detail;

import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.github.alessandrojean.mangachecklists.domain.Detail;
import io.github.alessandrojean.mangachecklists.domain.DetailGroup;
import io.github.alessandrojean.mangachecklists.domain.Manga;

/**
 * Created by Desktop on 18/12/2017.
 */

public class JBCDetailParser extends DetailParser {

    public JBCDetailParser(Manga manga) {
        super(manga);
    }

    @Override
    protected Manga getManga(Document html) {
        if (html.selectFirst("div.extra-info-container") != null)
            manga = parseMangaNew(html);
        else
            manga = parseMangaOld(html);

        // If manga is from a plan.
        //if (manga.getThumbnailUrl() == null) {
            manga.setThumbnailUrl(fixUrl(html.select("img.center-block.mb20").attr("src")));
        //}

        return manga;
    }

    private Manga parseMangaOld(Document html) {
        Element subtitle = html.selectFirst("em.text-center.excerpt");
        Element synopsis = html.selectFirst("div.mb30[itemprop=\"description\"] p");
        Element headerImage = html.selectFirst("img.colectionHeader.mb10");
        Elements detailsGroup = html.select("div.mb30[itemprop=\"description\"] p:has(strong)");

        manga.setSubtitle(subtitle.text());
        manga.setSynopsis(synopsis.text());

        if (headerImage != null)
            manga.setHeaderUrl(headerImage.attr("src"));

        List<DetailGroup> detailGroupList = new ArrayList<>();

        for (Element p : detailsGroup) {
            Element detailGroupName = p.previousElementSibling();

            DetailGroup detailGroup = new DetailGroup();

            if (detailGroupName.tagName() == "h2" || detailGroupName.tagName() == "h3")
                detailGroup.setName(detailGroupName.text());

            List<Detail> detailList = new ArrayList<>();

            Elements details = p.select("strong");

            for (Element e : details) {
                Detail detail = new Detail();
                detail.setName(e.text().replace(":", ""));

                Node node = e.nextSibling();
                detail.setDetail(node.toString().trim().replace("&nbsp;", ""));

                Log.d("detail", detail.getName() + detail.getDetail());

                detailList.add(detail);
            }

            detailGroup.setDetails(detailList);

            detailGroupList.add(detailGroup);
        }

        manga.setDetailGroups(detailGroupList);

        return manga;
    }

    private Manga parseMangaNew(Document html) {
        Element subtitle = html.selectFirst("em.text-center.excerpt");
        Element synopsis = html.selectFirst("div.mb30[itemprop=\"description\"] p");
        Element headerImage = html.selectFirst("img.colectionHeader.mb10");
        Elements detailsGroup = html.select("div.extra-info-col-content");

        manga.setSubtitle(subtitle.text());
        manga.setSynopsis(synopsis.text());

        if (headerImage != null)
            manga.setHeaderUrl(headerImage.attr("src"));

        List<DetailGroup> detailGroupList = new ArrayList<>();

        for (Element e : detailsGroup) {
            DetailGroup detailGroup = new DetailGroup();

            Element name = e.selectFirst("h2") == null
                    ? e.selectFirst("h3")
                    : e.selectFirst("h2");

            detailGroup.setName(name.text());

            List<Detail> detailsList = new ArrayList<>();

            Elements info = e.select("ul.extra-info li");

            for (Element f : info) {
                Detail detail = new Detail();
                detail.setName(f.select("strong").text().replace(":", ""));
                detail.setDetail(f.select("span").text().replace("&nbsp;", ""));

                Log.d("detail", detail.getName() + detail.getDetail());

                detailsList.add(detail);
            }

            detailGroup.setDetails(detailsList);

            detailGroupList.add(detailGroup);
        }

        manga.setDetailGroups(detailGroupList);

        return manga;
    }
}
