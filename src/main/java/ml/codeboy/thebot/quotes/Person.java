package ml.codeboy.thebot.quotes;

import java.util.ArrayList;

public class Person {
    private String name;
    private ArrayList<Quote>quotes=new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Quote> getQuotes() {
        return quotes;
    }

    public void setQuotes(ArrayList<Quote> quotes) {
        this.quotes = quotes;
    }

    public void save(){
        QuoteManager.getInstance().save(this);
    }
}
