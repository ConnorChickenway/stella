package xyz.connorchickenway.stella.util;

import org.bukkit.Bukkit;

public enum ServerVersion {

    V1_7_R4,
    V1_8_R1,
    V1_8_R2,
    V1_8_R3,
    V1_9_R1,
    V1_9_R2,
    V1_10_R1,
    V1_11_R1,
    V1_12_R1,
    V1_13_R1,
    V1_13_R2,
    V1_14_R1,
    V1_15_R1,
    V1_16_R1,
    V1_16_R2,
    V1_16_R3,
    V1_17_R1,
    V1_18_R1,
    V1_18_R2,
    V1_19_R1,
    V1_19_R2,
    V1_19_R3,
    V1_20_R1,
    V1_20_R2,
    V1_20_R3;

    public boolean isAboveOrEqual(ServerVersion compare) {
        return ordinal() >= compare.ordinal();
    }

    public boolean isEqual(ServerVersion compare) {
        return ordinal() == compare.ordinal();
    }

    public static ServerVersion serverVersion;

    static {
        String version = null;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException ignore) {

        }
        if (version != null) {
            for (ServerVersion tmp : ServerVersion.class.getEnumConstants())
                if (tmp.name().compareToIgnoreCase(version) == 0)
                {
                    serverVersion = tmp;
                    break;
                }
        }
    }

    public static boolean isLegacy() {
        return serverVersion != null && serverVersion.isEqual(ServerVersion.V1_7_R4);
    }

}
