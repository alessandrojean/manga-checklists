package io.github.alessandrojean.mangachecklists.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import io.github.alessandrojean.mangachecklists.domain.Manga;

/**
 * Created by Desktop on 14/12/2017.
 */

public class JBCMock {
    public static ArrayList<Manga> gerarMangas() {
        ArrayList<Manga> mangas = new ArrayList<>();

        Manga m1 = new Manga(
                "Fullmetal Alchemist ESP.",
                13,
                getDate(8,9,2017),
                "https://jbchost.com.br/mangasjbc/wp-content/uploads/2017/08/FMA-Esp_13_p-130x198.jpg",
                "https://mangasjbc.com.br/fullmetal-alchemist-esp-13/"
        );

        Manga m2 = new Manga(
                "Fort of Apocalypse",
                7,
                getDate(20,9,2017),
                "https://jbchost.com.br/mangasjbc/wp-content/uploads/2017/08/Fort-of-Apocalypse-07-Capa_p-130x198.jpg",
                "https://mangasjbc.com.br/fort-of-apocalypse-07/"
        );

        Manga m3 = new Manga(
                "Samurai 7",
                1,
                getDate(20,9,2017),
                "https://jbchost.com.br/mangasjbc/wp-content/uploads/2017/08/Samurai7-01-Capa_p-130x198.jpg",
                "https://mangasjbc.com.br/samurai-7-01/"
        );

        Manga m4 = new Manga(
                "Blood Blockade Battlefront",
                10,
                getDate(20,9,2017),
                "https://jbchost.com.br/mangasjbc/wp-content/uploads/2017/08/Kekkai-Sensen-10-Capa_p-130x198.jpg",
                "https://mangasjbc.com.br/blood-blockade-battlefront-10/"
        );

        mangas.addAll(Arrays.asList(m1, m2, m3, m4));

        return mangas;
    }

    private static long getDate(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);

        return calendar.getTimeInMillis();
    }
}
