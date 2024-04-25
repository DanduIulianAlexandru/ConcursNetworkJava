package concurs.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Stil {
    @JsonProperty("liber")
    LIBER,
    @JsonProperty("spate")
    SPATE,
    @JsonProperty("fluture")
    FLUTURE,
    @JsonProperty("mixt")
    MIXT
}
