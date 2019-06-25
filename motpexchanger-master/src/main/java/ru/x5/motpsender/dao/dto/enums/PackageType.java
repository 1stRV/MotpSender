package ru.x5.motpsender.dao.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum PackageType {
    BOX ("box"),
    BLOCK ("block"),
    PACK ("pack");

    private String packageType;

    PackageType(String packageType) {
        this.packageType = packageType;
    }

    private static final Map<String, PackageType> map;

    static {
        map = new HashMap<>();
        for (PackageType packageType : PackageType.values()) {
            map.put(packageType.packageType, packageType);
        }
    }

    @JsonCreator
    public static PackageType findByDescription(String value) {
        return map.get(value.toLowerCase());
    }
}
