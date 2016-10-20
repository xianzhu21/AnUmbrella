package io.github.xianzhuliu.coolweather.util;

import android.text.TextUtils;

import io.github.xianzhuliu.coolweather.db.CoolWeatherDB;
import io.github.xianzhuliu.coolweather.model.Location;

/**
 * Created by LiuXianzhu on 19/10/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public class Utility {
    public synchronized static boolean handleLocationsResponse(CoolWeatherDB coolWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] locations = response.split(";");
            if (locations != null && locations.length > 0) {
                for (String s : locations) {
                    if (!s.isEmpty()) {
                        String[] array = s.split(",");
                        Location location = new Location();
                        location.setLocationCode(array[0]);
                        location.setCountyName(array[1]);
                        location.setCityName(array[2]);
                        location.setProvinceName(array[3]);
                        coolWeatherDB.saveLocation(location);
                    }
                }
                return true;
            }
        }
        return false;
    }
//    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response) throws
// JSONException {
//        if (!TextUtils.isEmpty(response)) {
//            String[] allProvinces = response.split(",");
//            if (allProvinces != null && allProvinces.length > 0) {
//                for (String p :
//                        allProvinces) {
//                    String[] array = p.split("\\|");
//                    Province province = new Province();
//                    province.setProvinceCode(array[0]);
//                    province.setProvinceName(array[1]);
//                    coolWeatherDB.saveProvince(province);
//                }
//                return true;
//            }
//        }
//        return false;
//    }

//    public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, Context context)
// throws IOException {
//        if (!TextUtils.isEmpty(response)) {
//            String[] allCities = response.split(",");
//            if (allCities != null && allCities.length > 0) {
//                for (String c :
//                        allCities) {
//                    String[] array = c.split("\\|");
//                    Location location = new Location();
//                    location.setLocationCode(array[0]);
//                    location.setCityName(array[1]);
//                    location.setProvinceId(provinceId);
//                    coolWeatherDB.saveLocation(location);
//                }
//                return true;
//            }
//        }
//        return false;
//    }

//    public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, String response, int cityId) {
//        if (!TextUtils.isEmpty(response)) {
//            String[] allProvinces = response.split(",");
//            if (allProvinces != null && allProvinces.length > 0) {
//                for (String c :
//                        allProvinces) {
//                    String[] array = c.split("\\|");
//                    County county = new County();
//                    county.setCountyCode(array[0]);
//                    county.setCountyName(array[1]);
//                    county.setCityId(cityId);
//                    coolWeatherDB.saveCounty(county);
//                }
//                return true;
//            }
//        }
//        return false;
//    }
}
