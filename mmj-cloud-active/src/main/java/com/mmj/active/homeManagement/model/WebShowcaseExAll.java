package com.mmj.active.homeManagement.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WebShowcaseExAll implements Cloneable{

    private List<WebShowcaseEx> list;

    private WebShow webShow;

}
