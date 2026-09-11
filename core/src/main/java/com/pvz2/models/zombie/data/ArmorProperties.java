package com.pvz2.models.zombie.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ArmorProperties {
    @JsonProperty("aliases")
    private List<String> aliases;

    @JsonProperty("objdata")
    private ArmorData objdata;

    public List<String> getAliases() {
        return aliases;
    }

    public ArmorData getObjdata() {
        return objdata;
    }
}
