package com.xqlh.handscoordination.entity;

/**
 * Created by Administrator on 2017/8/30.
 */

public class Entity {
    private String id;
    private String name;
    private String SearchTime;
    private String DetailTime;

    public Entity() {
    }

    public Entity(String id, String name, String searchTime, String detailTime) {
        this.id = id;
        this.name = name;
        SearchTime = searchTime;
        DetailTime = detailTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSearchTime() {
        return SearchTime;
    }

    public void setSearchTime(String searchTime) {
        SearchTime = searchTime;
    }

    public String getDetailTime() {
        return DetailTime;
    }

    public void setDetailTime(String detailTime) {
        DetailTime = detailTime;
    }
}
