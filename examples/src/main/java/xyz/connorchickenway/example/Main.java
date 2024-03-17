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

package xyz.connorchickenway.example;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import xyz.connorchickenway.stella.Stella;
import xyz.connorchickenway.stella.tab.Tab;
import xyz.connorchickenway.stella.tab.entry.TabEntry;
import xyz.connorchickenway.stella.tab.modifier.TabModifier;
import xyz.connorchickenway.stella.tab.options.Options;
import xyz.connorchickenway.stella.tab.skin.Skin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        Stella.builder()
                .plugin(this)
                .tabModifier(TabModifier.builder()
                        .init(() -> {
                            List<TabEntry.TabEntryBuilder> entries = new ArrayList<>();
                            entries.add(TabEntry.builder()
                                    .x(Tab.LEFT)
                                    .y(1)
                                    .text("&eLocation:         "));
                            return entries;
                        })
                        .update((player) -> {
                            List<TabEntry> entries = new ArrayList<>();
                            Location location = player.getLocation();
                            entries.add(TabEntry.builder()
                                            .x(Tab.LEFT)
                                            .y(2)
                                            .text((int)location.getX() + ";" + (int)location.getY() + ";" +
                                                    (int)location.getZ())
                                            .build());
                            entries.add(TabEntry.builder()
                                            .x(Tab.LEFT)
                                            .y(10)
                                            .skin(SKINS.get(RANDOM.nextInt(SKINS.size())))
                                            .build());
                            entries.add(TabEntry.builder()
                                    .x(Tab.LEFT)
                                    .y(11)
                                    .skin(SKINS.get(RANDOM.nextInt(SKINS.size())))
                                    .build());
                            entries.add(TabEntry.builder()
                                    .x(Tab.LEFT)
                                    .y(12)
                                    .skin(SKINS.get(RANDOM.nextInt(SKINS.size())))
                                    .build());
                            entries.add(TabEntry.builder()
                                    .x(Tab.RIGHT)
                                    .y(19)
                                    .skin(SKINS.get(RANDOM.nextInt(SKINS.size())))
                                    .build());
                            return entries;
                        })
                        .build()
                )
                .options(Options.builder()
                        .async(true)
                        .delay(20)
                        .addListener(true)
                        .build()
                )
                .build();
    }


    private static final List<Skin> SKINS = new ArrayList<>();
    private static final Random RANDOM = new Random();

    static {
        JSONParser jsonParser = new JSONParser();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getClassLoader().getResourceAsStream("skins.json"))))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray skins = (JSONArray) obj;
            skins.forEach((skin) -> {
                JSONObject jsonObject = (JSONObject) ((JSONObject) skin).get("texture");
                SKINS.add(Skin.of((String)jsonObject.get("value"), (String)jsonObject.get("signature")));
            });


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
