package io.github.xianzhuliu.anumbrella.model;

import java.util.List;

/**
 * Created by LiuXianzhu on 24/10/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public class Weather {
    public Aqi aqi;
    public Basic basic;
    public List<DailyForecast> daily_forecast;
    public List<HourlyForecast> hourly_forecast;
    public Now now;

    public static class Aqi {
        public City city;

        public static class City {
            public String aqi;
            public String co;
            public String no2;
            public String o3;
            public String pm10;
            public String pm25;
            public String qlty;
            public String so2;
        }
    }

    public static class Basic {
        public String city;
        public String cnty;
        public String id;
        public String lat;
        public String lon;
        public Update update;

        public static class Update {
            public String loc;
            public String utc;
        }
    }

    public static class DailyForecast {
        public Astro astro;
        public Cond cond;
        public String date;
        public String hum;
        public String pop;
        public String pres;
        public Tmp tmp;
        public String vis;
        public Wind wind;

        public static class Astro {
            public String sr;
            public String ss;
        }

        public static class Cond {
            public String code_d;
            public String code_n;
            public String txt_d;
            public String txt_n;
        }

        public static class Tmp {
            public String max;
            public String min;
        }


    }

    public static class HourlyForecast {
        public String date;
        public String hum;
        public String pop;
        public String pres;
        public String tmp;
        public Wind wind;
    }

    public static class Now {
        public Cond cond;
        public String fl;
        public String hum;
        public String pcpn;
        public String pres;
        public String tmp;
        public String vis;
        public Wind wind;
        public String status;
        public Suggestion suggestion;

        public static class Cond {
            public String code;
            public String txt;
        }

        public static class Suggestion {
            public Description comf;
            public Description cw;
            public Description drsg;
            public Description flu;
            public Description sport;
            public Description trav;
            public Description uv;

            public static class Description {
                public String brf;
                public String txt;
            }
        }
    }

    public static class Wind {
        public String deg;
        public String dir;
        public String sc;
        public String spd;
    }
}
