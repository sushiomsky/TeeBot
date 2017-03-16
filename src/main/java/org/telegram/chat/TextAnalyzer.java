package org.telegram.chat;

import java.util.regex.Pattern;

/**
 * Created by Dennis Suchomsky on 16.03.17.
 */
public class TextAnalyzer {
    private String text;

    TextAnalyzer(String text){
        this.text = text;
    }

    public boolean isQuestion(){
        return Pattern.matches(".\\?", text);
    }
}
