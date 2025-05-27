// TestDTO.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu.singlesteptests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestDTO {

    @JsonProperty("idx")
    public int index;

    public String name;

    @JsonProperty("bytes")
    public Byte[] opcodes;

    @JsonProperty("initial")
    public SingleStepTestSet before;

    @JsonProperty("final")
    public SingleStepTestSet after;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SingleStepTestSet {

        public static class SingleStepTestSetRegs {

            @JsonProperty("ax")
            public Integer AX;

            @JsonProperty("bx")
            public Integer BX;

            @JsonProperty("cx")
            public Integer CX;

            @JsonProperty("dx")
            public Integer DX;

            @JsonProperty("cs")
            public Integer CS;

            @JsonProperty("ss")
            public Integer SS;

            @JsonProperty("ds")
            public Integer DS;

            @JsonProperty("es")
            public Integer ES;

            @JsonProperty("sp")
            public Integer SP;

            @JsonProperty("bp")
            public Integer BP;

            @JsonProperty("si")
            public Integer SI;

            @JsonProperty("di")
            public Integer DI;

            @JsonProperty("ip")
            public Integer IP;

            public Integer flags;
        }

        @JsonProperty("regs")
        public SingleStepTestSetRegs regs;

        @JsonProperty("ram")
        public List<List<Integer>> memory;
    }
}
