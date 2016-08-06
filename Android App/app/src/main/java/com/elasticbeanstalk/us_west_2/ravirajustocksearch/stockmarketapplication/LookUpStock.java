package com.elasticbeanstalk.us_west_2.ravirajustocksearch.stockmarketapplication;

public class LookUpStock {
    private String symbol;
    private String companyName;
    private String exchange;

    public LookUpStock(String symbol, String companyName, String exchange) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.exchange = exchange;
    }

    public String getSymbol(){
        return symbol;
    }

    public String getCompanyName(){
        return companyName;
    }

    public String getExchange(){
        return exchange;
    }
}
