package com.example.qr;

public class HelperCalss2 {
    String Tid,sender_p,receiver_p,items,amount,price,tax,done;

    public HelperCalss2(String tid, String sender_p, String receiver_p, String items, String amount, String price, String tax, String done) {
        Tid = tid;
        this.sender_p = sender_p;
        this.receiver_p = receiver_p;
        this.items = items;
        this.amount = amount;
        this.price = price;
        this.tax = tax;
        this.done = done;
    }

    public String getTid() {
        return Tid;
    }

    public String getSender_p() {
        return sender_p;
    }

    public String getReceiver_p() {
        return receiver_p;
    }

    public String getItems() {
        return items;
    }

    public String getAmount() {
        return amount;
    }

    public String getPrice() {
        return price;
    }

    public String getTax() {
        return tax;
    }

    public String getDone() {
        return done;
    }
}
