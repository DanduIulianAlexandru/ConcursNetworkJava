package concurs.model;

import antlr.StringUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public enum Distanta {
    @JsonProperty("m50")
    M50,
    @JsonProperty("m200")
    M200,
    @JsonProperty("m800")
    M800,
    @JsonProperty("m1500")
    M1500
}
