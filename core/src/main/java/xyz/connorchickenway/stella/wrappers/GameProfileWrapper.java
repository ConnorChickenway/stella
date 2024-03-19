/*
 *     Stella - A tablist API
 *     Copyright (C) 2024  ConnorChickenway
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package xyz.connorchickenway.stella.wrappers;

import xyz.connorchickenway.stella.tab.entry.TabEntry;
import xyz.connorchickenway.stella.tab.skin.Skin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

import static xyz.connorchickenway.stella.util.NMSVersion.*;
import static xyz.connorchickenway.stella.util.ReflectionHelper.*;

public class GameProfileWrapper {

    private final UUID uuid;
    private final String name;
    private final Object gameProfile;

    public GameProfileWrapper(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.gameProfile = invokeConstructor(GAME_PROFILE_CONSTRUCTOR, uuid, name);
    }

    public void setSkin(Skin skin) {
        setSkin(skin.getValue(), skin.getValue());
    }

    public void setSkin(String value, String signature) {
        invokeMethod(PUT_PROPERTY,
                invokeMethod(GET_PROPERTIES, gameProfile),
                "textures",
                invokeConstructor(PROPERY_CONSTRUCTOR, "textures", value, signature));
    }

    public UUID getId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Object getGameProfile() {
        return gameProfile;
    }

    public static GameProfileWrapper getGameProfile(TabEntry tabEntry) {
        return getGameProfile(tabEntry.getId(), tabEntry.getEntryName(), tabEntry.getSkin());
    }

    public static GameProfileWrapper getGameProfileWithoutSkin(TabEntry tabEntry) {
        return getGameProfile(tabEntry.getId(), tabEntry.getEntryName(), null);
    }

    public static GameProfileWrapper getGameProfile(UUID uuid, String name, Skin skin) {
        GameProfileWrapper gameProfileWrapper = new GameProfileWrapper(uuid, name);
        if (skin != null)
            gameProfileWrapper.setSkin(skin.getValue(), skin.getSignature());
        return gameProfileWrapper;
    }

    private static final Constructor<?> GAME_PROFILE_CONSTRUCTOR, PROPERY_CONSTRUCTOR;
    private static final Method GET_PROPERTIES, PUT_PROPERTY;

    static {
        try {
            final String _package_ = is1_7() ? "net.minecraft.util." : "";
            Class<?> gameProfileClass = Class.forName(_package_ + "com.mojang.authlib.GameProfile");
            GAME_PROFILE_CONSTRUCTOR = gameProfileClass.getConstructor(UUID.class, String.class);
            GET_PROPERTIES =  gameProfileClass.getMethod("getProperties");
            Class<?> propertyClass = Class.forName(_package_ + "com.mojang.authlib.properties.Property");
            PROPERY_CONSTRUCTOR = propertyClass.getConstructor(String.class, String.class, String.class);
            Class<?> propertyMapClass = GET_PROPERTIES.getReturnType();
            PUT_PROPERTY = Arrays.stream(propertyMapClass.getMethods()).filter(method -> method.getName().equals("put"))
                    .findFirst().orElseThrow(RuntimeException::new);
        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
