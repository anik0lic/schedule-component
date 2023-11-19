package raf.sk.projekat1.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Places {
    protected String name;
    protected Map<String,String> additional;

    public Places() {
        additional = new HashMap<>();
    }

    public Places(String name) {
        this.name = name;
        additional = new HashMap<>();
    }

}
