package xyx.connorchickenway.stella.legacy.util;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class LegacyUtils {

    private LegacyUtils() {
    }

    public static boolean is1_7(Player player) {
        return ((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() < 47;
    }

    public static String prefixAndSuffix(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        if (text.length() <= 16) {
            stringBuilder.append(text);
        } else {
            int i = text.charAt(15) == ChatColor.COLOR_CHAR ? 15 : 16;
            String prefix = text.substring(0, i);
            String suffix = (i == 16 ? ChatColor.getLastColors(prefix) : "") + text.substring(i);
            suffix = suffix.length() > 16 ? suffix.substring(0, 16) : suffix;
            stringBuilder.append(prefix)
                    .append(":;:")
                    .append(suffix);
        }
        return stringBuilder.toString();
    }

}
