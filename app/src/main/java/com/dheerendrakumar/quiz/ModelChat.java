package com.dheerendrakumar.quiz;

public class ModelChat {

    String sender;
    String messsage;
    String type;
    String timestamp;
    boolean dilihat;

    public ModelChat(String message, String receiver, String sender, String type, String timestamp, boolean dilihat) {
        this.messsage = message;
        this.receiver = receiver;
        this.sender = sender;
        this.type = type;
        this.timestamp = timestamp;
        this.dilihat = dilihat;
    }



    public String getMesssage() {
        return messsage;
    }

    public void setMesssage(String message) {
        this.messsage = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isDilihat() {
        return dilihat;
    }

    public void setDilihat(boolean dilihat) {
        this.dilihat = dilihat;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String receiver;

    public ModelChat() {
    }

}
