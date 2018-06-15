package com.example.spirit.androiddemo.modle;

import java.util.List;

public class WeatherBean {

    /**
     * date : 20180613
     * message : Success !
     * status : 200
     * city : 北京
     * count : 1557
     * data : {"shidu":"72%","pm25":68,"pm10":66,"quality":"良","wendu":"23",
     * "ganmao":"极少数敏感人群应减少户外活动","yesterday":{"date":"12日星期二","sunrise":"04:45","high":"高温
     * 32.0℃","low":"低温 19.0℃","sunset":"19:43","aqi":120,"fx":"东南风","fl":"3-4级","type":"雷阵雨",
     * "notice":"带好雨具，别在树下躲雨"},"forecast":[{"date":"13日星期三","sunrise":"04:45","high":"高温 28.0℃",
     * "low":"低温 19.0℃","sunset":"19:43","aqi":92,"fx":"东北风","fl":"<3级","type":"雷阵雨",
     * "notice":"带好雨具，别在树下躲雨"},{"date":"14日星期四","sunrise":"04:45","high":"高温 31.0℃","low":"低温
     * 20.0℃","sunset":"19:44","aqi":71,"fx":"东南风","fl":"<3级","type":"多云",
     * "notice":"阴晴之间，谨防紫外线侵扰"},{"date":"15日星期五","sunrise":"04:45","high":"高温 33.0℃","low":"低温
     * 22.0℃","sunset":"19:44","aqi":81,"fx":"南风","fl":"<3级","type":"晴","notice":"愿你拥有比阳光明媚的心情"},
     * {"date":"16日星期六","sunrise":"04:45","high":"高温 34.0℃","low":"低温 21.0℃","sunset":"19:44",
     * "aqi":81,"fx":"南风","fl":"<3级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"17日星期日",
     * "sunrise":"04:45","high":"高温 30.0℃","low":"低温 20.0℃","sunset":"19:45","aqi":88,"fx":"东北风",
     * "fl":"<3级","type":"雷阵雨","notice":"带好雨具，别在树下躲雨"}]}
     */

    private String date;
    private String message;
    private int status;
    private String city;
    private int count;
    private DataBean data;

    public String getDate() { return date;}

    public void setDate(String date) { this.date = date;}

    public String getMessage() { return message;}

    public void setMessage(String message) { this.message = message;}

    public int getStatus() { return status;}

    public void setStatus(int status) { this.status = status;}

    public String getCity() { return city;}

    public void setCity(String city) { this.city = city;}

    public int getCount() { return count;}

    public void setCount(int count) { this.count = count;}

    public DataBean getData() { return data;}

    public void setData(DataBean data) { this.data = data;}

    public static class DataBean {
        /**
         * shidu : 72%
         * pm25 : 68.0
         * pm10 : 66.0
         * quality : 良
         * wendu : 23
         * ganmao : 极少数敏感人群应减少户外活动
         * yesterday : {"date":"12日星期二","sunrise":"04:45","high":"高温 32.0℃","low":"低温 19.0℃",
         * "sunset":"19:43","aqi":120,"fx":"东南风","fl":"3-4级","type":"雷阵雨","notice":"带好雨具，别在树下躲雨"}
         * forecast : [{"date":"13日星期三","sunrise":"04:45","high":"高温 28.0℃","low":"低温 19.0℃",
         * "sunset":"19:43","aqi":92,"fx":"东北风","fl":"<3级","type":"雷阵雨","notice":"带好雨具，别在树下躲雨"},
         * {"date":"14日星期四","sunrise":"04:45","high":"高温 31.0℃","low":"低温 20.0℃",
         * "sunset":"19:44","aqi":71,"fx":"东南风","fl":"<3级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},
         * {"date":"15日星期五","sunrise":"04:45","high":"高温 33.0℃","low":"低温 22.0℃",
         * "sunset":"19:44","aqi":81,"fx":"南风","fl":"<3级","type":"晴","notice":"愿你拥有比阳光明媚的心情"},
         * {"date":"16日星期六","sunrise":"04:45","high":"高温 34.0℃","low":"低温 21.0℃",
         * "sunset":"19:44","aqi":81,"fx":"南风","fl":"<3级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},
         * {"date":"17日星期日","sunrise":"04:45","high":"高温 30.0℃","low":"低温 20.0℃",
         * "sunset":"19:45","aqi":88,"fx":"东北风","fl":"<3级","type":"雷阵雨","notice":"带好雨具，别在树下躲雨"}]
         */

        private String shidu;
        private double pm25;
        private double pm10;
        private String quality;
        private String wendu;
        private String ganmao;
        private YesterdayBean yesterday;
        private List<ForecastBean> forecast;

        public String getShidu() { return shidu;}

        public void setShidu(String shidu) { this.shidu = shidu;}

        public double getPm25() { return pm25;}

        public void setPm25(double pm25) { this.pm25 = pm25;}

        public double getPm10() { return pm10;}

        public void setPm10(double pm10) { this.pm10 = pm10;}

        public String getQuality() { return quality;}

        public void setQuality(String quality) { this.quality = quality;}

        public String getWendu() { return wendu;}

        public void setWendu(String wendu) { this.wendu = wendu;}

        public String getGanmao() { return ganmao;}

        public void setGanmao(String ganmao) { this.ganmao = ganmao;}

        public YesterdayBean getYesterday() { return yesterday;}

        public void setYesterday(YesterdayBean yesterday) { this.yesterday = yesterday;}

        public List<ForecastBean> getForecast() { return forecast;}

        public void setForecast(List<ForecastBean> forecast) { this.forecast = forecast;}

        public static class YesterdayBean {
            /**
             * date : 12日星期二
             * sunrise : 04:45
             * high : 高温 32.0℃
             * low : 低温 19.0℃
             * sunset : 19:43
             * aqi : 120.0
             * fx : 东南风
             * fl : 3-4级
             * type : 雷阵雨
             * notice : 带好雨具，别在树下躲雨
             */

            private String date;
            private String sunrise;
            private String high;
            private String low;
            private String sunset;
            private double aqi;
            private String fx;
            private String fl;
            private String type;
            private String notice;

            public String getDate() { return date;}

            public void setDate(String date) { this.date = date;}

            public String getSunrise() { return sunrise;}

            public void setSunrise(String sunrise) { this.sunrise = sunrise;}

            public String getHigh() { return high;}

            public void setHigh(String high) { this.high = high;}

            public String getLow() { return low;}

            public void setLow(String low) { this.low = low;}

            public String getSunset() { return sunset;}

            public void setSunset(String sunset) { this.sunset = sunset;}

            public double getAqi() { return aqi;}

            public void setAqi(double aqi) { this.aqi = aqi;}

            public String getFx() { return fx;}

            public void setFx(String fx) { this.fx = fx;}

            public String getFl() { return fl;}

            public void setFl(String fl) { this.fl = fl;}

            public String getType() { return type;}

            public void setType(String type) { this.type = type;}

            public String getNotice() { return notice;}

            public void setNotice(String notice) { this.notice = notice;}
        }

        public static class ForecastBean {
            /**
             * date : 13日星期三
             * sunrise : 04:45
             * high : 高温 28.0℃
             * low : 低温 19.0℃
             * sunset : 19:43
             * aqi : 92.0
             * fx : 东北风
             * fl : <3级
             * type : 雷阵雨
             * notice : 带好雨具，别在树下躲雨
             */

            private String date;
            private String sunrise;
            private String high;
            private String low;
            private String sunset;
            private double aqi;
            private String fx;
            private String fl;
            private String type;
            private String notice;

            public String getDate() { return date;}

            public void setDate(String date) { this.date = date;}

            public String getSunrise() { return sunrise;}

            public void setSunrise(String sunrise) { this.sunrise = sunrise;}

            public String getHigh() { return high;}

            public void setHigh(String high) { this.high = high;}

            public String getLow() { return low;}

            public void setLow(String low) { this.low = low;}

            public String getSunset() { return sunset;}

            public void setSunset(String sunset) { this.sunset = sunset;}

            public double getAqi() { return aqi;}

            public void setAqi(double aqi) { this.aqi = aqi;}

            public String getFx() { return fx;}

            public void setFx(String fx) { this.fx = fx;}

            public String getFl() { return fl;}

            public void setFl(String fl) { this.fl = fl;}

            public String getType() { return type;}

            public void setType(String type) { this.type = type;}

            public String getNotice() { return notice;}

            public void setNotice(String notice) { this.notice = notice;}
        }
    }
}
