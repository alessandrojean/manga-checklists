package io.github.alessandrojean.mangachecklists.parser.detail;

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
 * Created by Desktop on 20/12/2017.
 */

public class NewPOPDetailParser extends DetailParser {

    public NewPOPDetailParser(Manga manga) {
        super(manga);
    }

    @Override
    protected Manga getManga(Document html) {
        return manga.getUrl().indexOf("lojanewpop.com.br") != -1
                ? parseMangaShop(html)
                : parseMangaSite(html);
    }

    private Manga parseMangaSite(Document html) {
        Element synopsis = html.select("div.col-md-7 p").first();
        Elements details = html.select("div.meta div.meta-item b");

        manga.setSynopsis(synopsis.text());

        List<DetailGroup> detailGroupList = new ArrayList<>();

        DetailGroup productDetails = new DetailGroup();
        productDetails.setName("Detalhes do produto");

        List<Detail> detailList = new ArrayList<>();

        for (Element e : details) {
            Detail detail = new Detail();
            detail.setName(e.text().replace(":", ""));

            Node node = e.nextSibling();
            detail.setDetail(node.toString().trim());

            detailList.add(detail);
        }

        detailGroupList.add(productDetails);

        productDetails.setDetails(detailList);

        manga.setDetailGroups(detailGroupList);

        return manga;
    }

    private Manga parseMangaShop(Document html) {
        Element synopsis = html.select("div.tab-pane.active p").first();
        Elements details = html.select("div.tab-pane.active ul li");

        manga.setSynopsis(synopsis.text());

        List<DetailGroup> detailGroupList = new ArrayList<>();

        DetailGroup productDetails = new DetailGroup();
        productDetails.setName("Detalhes do produto");

        List<Detail> detailList = new ArrayList<>();

        for (Element e : details) {
            // Parse only important details.
            if (e.text().indexOf(":") != -1) {
                Detail detail = new Detail();

                String[] splitted = e.text().split(":");
                detail.setName(splitted[0]);
                detail.setDetail(splitted[1]);

                detailList.add(detail);
            }
        }

        detailGroupList.add(productDetails);

        productDetails.setDetails(detailList);

        manga.setDetailGroups(detailGroupList);

        return manga;
    }
}
