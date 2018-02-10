package com.dchip.door.smartdoorsdk.event;

/**
 * Created by jelly on 2017/8/3.
 */

public class ReadCardEven {
    String cardId;
    public ReadCardEven(String cardId){
        this.cardId = cardId;
    }

    public String getCardId() {
        return cardId;
    }
}
