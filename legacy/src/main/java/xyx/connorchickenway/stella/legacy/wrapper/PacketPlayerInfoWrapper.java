package xyx.connorchickenway.stella.legacy.wrapper;

import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import xyx.connorchickenway.stella.legacy.util.ReflectionUtil;

public class PacketPlayerInfoWrapper implements PacketWrapper<PacketPlayOutPlayerInfo> {

    private final PacketPlayOutPlayerInfo packet;

    public PacketPlayerInfoWrapper(PlayerInfoAction action) {
        this.packet = new PacketPlayOutPlayerInfo();
        this.setAction(action);
    }

    private void setAction(PlayerInfoAction action) {
        ReflectionUtil.set(this.packet, "action", action.ordinal());
    }

    public void setText(String username) {
        ReflectionUtil.set(this.packet, "username", username);
    }

    public void setPing(int ping) {
        ReflectionUtil.set(this.packet, "ping", ping);
    }

    public void setProfile(GameProfile gameProfile) {
        ReflectionUtil.set(this.packet,"player", gameProfile);
    }

    @Override
    public PacketPlayOutPlayerInfo get() {
        return packet;
    }

    @Override
    public void send(Player player) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(this.packet);
    }

    public enum PlayerInfoAction {
        ADD_PLAYER,
        UPDATE_GAME_MODE,
        UPDATE_LATENCY,
        UPDATE_DISPLAY_NAME,
        REMOVE_PLAYER;
    }

}
