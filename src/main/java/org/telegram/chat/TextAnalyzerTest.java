package org.telegram.chat;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by Dennis Suchomsky on 16.03.17.
 */
class TextAnalyzerTest {

    void isQuestion() {
        TextAnalyzer TextAnalyzerTest  = new TextAnalyzer("Bin ich eine Frage?");
        assertTrue(TextAnalyzerTest.isQuestion());
    }

}