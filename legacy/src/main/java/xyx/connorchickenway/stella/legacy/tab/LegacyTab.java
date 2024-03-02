package xyx.connorchickenway.stella.legacy.tab;

import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyx.connorchickenway.stella.legacy.wrapper.PacketPlayerInfoWrapper;
import xyx.connorchickenway.stella.legacy.wrapper.PacketScoreboardTeamWrapper;
import xyz.connorchickenway.stella.tab.PlayerTab;
import xyz.connorchickenway.stella.tab.entry.TabEntry;
import xyz.connorchickenway.stella.tab.modifier.TabModifier;
import xyz.connorchickenway.stella.tab.skin.Skin;
import xyz.connorchickenway.stella.util.ServerVersion;

import static xyx.connorchickenway.stella.legacy.util.LegacyUtils.is1_7;

public class LegacyTab extends PlayerTab {


    public LegacyTab(Player player) {
        super(player, new TabEntry[is1_7(player) ? WIDTH - 1 : WIDTH][HEIGHT]);
    }

    @Override
    public void init(TabModifier tabModifier) {
        final boolean isLegacy = is1_7(player);
        final int divisor = isLegacy ? 3 : 20;
        for(int index = 0; index <
                (isLegacy ? 80 : 60); index++) {
            int x = index / divisor;
            int y = index % divisor;
            TabEntry tabEntry = entries[x][y] = buildEntry(x, y, tabModifier).initName();
            GameProfile gameProfile = createProfile(tabEntry);
            String text = tabEntry.getText();
            PacketPlayerInfoWrapper info = new PacketPlayerInfoWrapper(PacketPlayerInfoWrapper.
                    PlayerInfoAction.ADD_PLAYER);
            //if player has 1.7 version then send PacketPlayOutScoreboardTeam
            if (isLegacy) {
                String name = NAMES[x][y];
                PacketScoreboardTeamWrapper team = new PacketScoreboardTeamWrapper(true, name);
                team.addEntry(name);
                if (!text.isEmpty()) {
                    String[] split = text.split(":;:");
                    if (split.length > 1) {
                        team.setSuffix(split[1]);
                    }
                    team.setPrefix(split[0]);
                }
                team.send(player);
                info.setText(name);
            } else {
                info.setText(text);
            }
            Skin skin = tabEntry.getSkin();
            gameProfile.getProperties().put("textures",
                    new Property("textures", skin.getValue(), skin.getSignature()));
            info.setPing(tabEntry.getPing());
            info.setProfile(gameProfile);
            info.send(player);
        }
        this.creating.set(false);
    }

    public GameProfile createProfile(TabEntry tabEntry) {
        return new GameProfile(tabEntry.getId(), tabEntry.getEntryName());
    }

    private static String[][] NAMES;

    static {
        if (ServerVersion.isLegacy()) {
            NAMES = new String[3][20];
            ChatColor[] colors = ChatColor.values();
            StringBuilder sb = new StringBuilder("    " + ChatColor.RESET);
            for (int i = 0; i < 60; i++) {
                int x = i % 3;
                int y = i / 3;
                sb.setCharAt(0, ChatColor.COLOR_CHAR);
                sb.setCharAt(1, colors[x].getChar());
                sb.setCharAt(2, ChatColor.COLOR_CHAR);
                sb.setCharAt(3, colors[y].getChar());
                NAMES[x][y] = sb.toString();
            }
        }
    }

}