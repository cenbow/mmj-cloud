package com.mmj.active.homeManagement.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class WebShowcaseEx extends WebShowcase implements Cloneable{

    private List<WebShowcaseFile> webShowcaseFile;

    private List<ShowcaseGood> showcaseGood;

    private Integer showId;

    private Integer showcaseShow;
    public WebShowcaseEx clone() {
        try {
            return (WebShowcaseEx) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return new WebShowcaseEx();
        }
    }

}
