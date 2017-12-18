package io.github.alessandrojean.mangachecklists.parser.detail;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.alessandrojean.mangachecklists.domain.Detail;
import io.github.alessandrojean.mangachecklists.domain.DetailGroup;
import io.github.alessandrojean.mangachecklists.domain.Manga;

/**
 * Created by Desktop on 18/12/2017.
 */

public class PaniniDetailParser extends DetailParser {

    public PaniniDetailParser(Manga manga) {
        super(manga);
    }

    @Override
    protected Manga getManga(Document html) {
        Element synopsis = html.selectFirst("div.details-description p");
        Elements details = html.select("div.description ul li");

        if (synopsis != null)
            manga.setSynopsis(synopsis.text());

        List<DetailGroup> detailGroupList = new ArrayList<>();

        DetailGroup productDetails = new DetailGroup();
        productDetails.setName("Detalhes do produto");

        List<Detail> detailList = new ArrayList<>();

        for (Element li : details) {
            Detail detail = new Detail();

            Element detailName = li.selectFirst("strong");
            detail.setName(detailName.text().replace(":", ""));

            Element a = li.selectFirst("a");
            if (a != null)
                detail.setDetail(a.text());
            else {
                Node node = detailName.nextSibling();
                detail.setDetail(node.toString().replace(":", "").trim());
            }

            detailList.add(detail);
        }

        manga.setDate(detailList.get(detailList.size() - 1).getDetail());

        detailList.add(new Detail("Preço", formatCurrency(manga.getPrice())));

        productDetails.setDetails(detailList);
        detailGroupList.add(productDetails);

        manga.setDetailGroups(detailGroupList);

        return manga;
    }
}
