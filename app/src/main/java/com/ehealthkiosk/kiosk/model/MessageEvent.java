package com.ehealthkiosk.kiosk.model;

/**
 * Created by pankaj on 10/7/15.
 */
public class MessageEvent {


    public final int type;
    public static final int EVENT_CHANGE_TEST = 0;
    public static final int EVENT_DONE_TEST = 1;
    public Object data = null;

    public MessageEvent(int type) {
        this.type = type;
    }

    public MessageEvent(int type, Object data) {
        this.type = type;
        this.data = data;
    }
}
