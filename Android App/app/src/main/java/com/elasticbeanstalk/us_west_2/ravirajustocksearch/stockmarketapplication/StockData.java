package com.elasticbeanstalk.us_west_2.ravirajustocksearch.stockmarketapplication;

public class StockData {
    private String header;
    private String data;

    public StockData(String header, String data) {
        this.header = header;
        this.data = data;
    }

    public String getHeader() {
        return header;
    }

    public String getData() {
        return data;
    }
}
