package xyx.connorchickenway.stella.legacy.wrapper;

import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import xyx.connorchickenway.stella.legacy.util.ReflectionUtil;

import java.util.Collection;

public class PacketScoreboardTeamWrapper implements PacketWrapper<PacketPlayOutScoreboardTeam> {

    private final PacketPlayOutScoreboardTeam team;

    public PacketScoreboardTeamWrapper(boolean create, String teamName) {
        team = new PacketPlayOutScoreboardTeam();
        ReflectionUtil.set(team, "a", teamName);
        ReflectionUtil.set(team, "f", create ? 0 : 2);
    }


    public void addEntry(String entry) {
        Collection collection = (Collection) ReflectionUtil.get(team, "e");
        collection.add(entry);
    }

    public void setPrefix(String prefix) {
        ReflectionUtil.set(team, "c", prefix);
    }

    public void setSuffix(String suffix) {
        ReflectionUtil.set(team, "d", suffix);
    }


    @Override
    public PacketPlayOutScoreboardTeam get() {
        return team;
    }

    @Override
    public void send(Player player) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(team);
    }

}
