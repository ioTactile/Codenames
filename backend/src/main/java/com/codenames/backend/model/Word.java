package com.codenames.backend.model;

import java.util.List;

import jakarta.persistence.Embeddable;

@Embeddable
public class Word {

    private String wordName;
    private List<String> selectedBy;
    private WordState wordState;
    private WordColor wordColor;

    public Word() {

    }

    public Word(
            String wordName,
            List<String> selectedBy,
            WordState wordState,
            WordColor wordColor) {
        this.wordName = wordName;
        this.selectedBy = selectedBy;
        this.wordState = wordState;
        this.wordColor = wordColor;
    }

    public String getWordName() {
        return wordName;
    }

    public void setWordName(String wordName) {
        this.wordName = wordName;
    }

    public List<String> getSelectedBy() {
        return selectedBy;
    }

    public void setSelectedBy(List<String> selectedBy) {
        this.selectedBy = selectedBy;
    }

    public WordState getWordState() {
        return wordState;
    }

    public void setWordState(WordState wordState) {
        this.wordState = wordState;
    }

    public WordColor getWordColor() {
        return wordColor;
    }

    public void setWordColor(WordColor wordColor) {
        this.wordColor = wordColor;
    }
}
