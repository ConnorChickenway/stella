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

import com.google.common.collect.LinkedHashMultimap;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
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

    public GameProfileWrapper(UUID uuid, String name, Skin skin) {
        this.uuid = uuid;
        this.name = name;
        if(!isNewVersion()) {
            this.gameProfile = invokeConstructor(GAME_PROFILE_CONSTRUCTOR, uuid, name);
            if (skin != null)
                invokeMethod(PUT_PROPERTY,
                        invokeMethod(GET_PROPERTIES, gameProfile),
                        "textures",
                        invokeConstructor(PROPERTY_CONSTRUCTOR, "textures", skin.getValue(), skin.getSignature()));
        }else
        if (skin != null) {
            LinkedHashMultimap<String, Property> map = LinkedHashMultimap.create();
            map.put("textures", (Property) invokeConstructor(PROPERTY_CONSTRUCTOR, "textures", skin.getValue(), skin.getSignature()));
            gameProfile = invokeConstructor(GAME_PROFILE_WITH_MAP_CONSTRUCTOR, uuid, name, invokeConstructor(PROPERTY_MAP_CLASS, map));
        }else
            this.gameProfile = invokeConstructor(GAME_PROFILE_CONSTRUCTOR, uuid, name);

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
        return new GameProfileWrapper(uuid, name, skin);
    }

    private static boolean isNewVersion() {
        return MinecraftVersion.ordinal(MinecraftVersion._1_21_10) || compare(v1_21_R6);
    }

    private static final Constructor<?> GAME_PROFILE_CONSTRUCTOR, PROPERTY_CONSTRUCTOR;
    private static final Method GET_PROPERTIES, PUT_PROPERTY;
    private static final Class<?> PROPERTY_CLASS;
    private static Constructor<?> PROPERTY_MAP_CLASS, GAME_PROFILE_WITH_MAP_CONSTRUCTOR;

    static {
        try {
            final String _package_ = is1_7() ? "net.minecraft.util." : "";
            Class<?> gameProfileClass = Class.forName(_package_ + "com.mojang.authlib.GameProfile");
            if (isNewVersion()) {
                GAME_PROFILE_WITH_MAP_CONSTRUCTOR = gameProfileClass.getConstructor(UUID.class, String.class, PropertyMap.class);
                PROPERTY_MAP_CLASS = Class.forName("com.mojang.authlib.properties.PropertyMap").getConstructors()[0];
            }
            GAME_PROFILE_CONSTRUCTOR = gameProfileClass.getConstructor(UUID.class, String.class);
            GET_PROPERTIES =  gameProfileClass.getMethod(isNewVersion() ? "properties" : "getProperties");
            PROPERTY_CLASS = Class.forName(_package_ + "com.mojang.authlib.properties.Property");
            PROPERTY_CONSTRUCTOR = PROPERTY_CLASS.getConstructor(String.class, String.class, String.class);
            Class<?> propertyMapClass = GET_PROPERTIES.getReturnType();
            PUT_PROPERTY = Arrays.stream(propertyMapClass.getMethods()).filter(method -> method.getName().equals("put"))
                    .findFirst().orElseThrow(RuntimeException::new);
        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
