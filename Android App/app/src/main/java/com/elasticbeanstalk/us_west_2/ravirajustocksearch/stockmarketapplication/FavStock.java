package com.elasticbeanstalk.us_west_2.ravirajustocksearch.stockmarketapplication;

public class FavStock {
    private String symbol;
    private String price;
    private double change;
    private String company;
    private String marketCap;

    public FavStock(String symbol, String price, double change, String company, String marketCap) {
        this.symbol = symbol;
        this.price = price;
        this.change = change;
        this.company = company;
        this.marketCap = marketCap;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getPrice() {
        return price;
    }

    public double getChange() {
        return change;
    }

    public String getCompany() {
        return company;
    }

    public String getMarketCap() {
        return marketCap;
    }
}
