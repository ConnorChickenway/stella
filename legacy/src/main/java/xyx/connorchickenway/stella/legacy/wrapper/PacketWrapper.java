package xyx.connorchickenway.stella.legacy.wrapper;

import org.bukkit.entity.Player;

public interface PacketWrapper<T> {


    T get();
    void send(Player player);

}
