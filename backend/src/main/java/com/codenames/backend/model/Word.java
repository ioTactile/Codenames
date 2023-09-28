package com.codenames.backend.model;

import java.util.List;

import jakarta.persistence.Embeddable;

@Embeddable
public class Word {

    private String name;
    private Boolean isSelected;
    private List<Player> selectedBy;
    private Boolean isClicked;
    private WordColor wordColor;

    public Word() {

    }

    public Word(
            String name,
            Boolean isSelected,
            List<Player> selectedBy,
            Boolean isCliked,
            WordColor wordColor) {
        this.name = name;
        this.isSelected = isSelected;
        this.selectedBy = selectedBy;
        this.isClicked = isCliked;
        this.wordColor = wordColor;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public Boolean getIsSelected() {
        return isSelected;
    }
    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public List<Player> getSelectedBy() {
        return selectedBy;
    }
    public void setSelectedBy(List<Player> seletedby) {
        this.selectedBy = seletedby;
    }

    public Boolean getIsClicked() {
        return isClicked;
    }
    public void setIsClicked(Boolean isClicked) {
        this.isClicked = isClicked;
    }

    public WordColor getWordColor() {
        return wordColor;
    }
    public void setWordColor(WordColor wordColor) {
        this.wordColor = wordColor;
    }
}
