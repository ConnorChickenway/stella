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

package xyz.connorchickenway.stella.util;

import org.bukkit.Bukkit;

public enum NMSVersion {

    v1_7_R4,
    v1_8_R1,
    v1_8_R2,
    v1_8_R3,
    v1_9_R1,
    v1_9_R2,
    v1_10_R1,
    v1_11_R1,
    v1_12_R1,
    v1_13_R1,
    v1_13_R2,
    v1_14_R1,
    v1_15_R1,
    v1_16_R1,
    v1_16_R2,
    v1_16_R3,
    v1_17_R1,
    v1_18_R1,
    v1_18_R2,
    v1_19_R1,
    v1_19_R2,
    v1_19_R3,
    v1_20_R1,
    v1_20_R2,
    v1_20_R3;

    public boolean isEqual(NMSVersion compare) {
        return ordinal() == compare.ordinal();
    }

    public static NMSVersion SERVER_VERSION;

    static {
        String version = null;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException ignore) {

        }
        if (version != null) {
            for (NMSVersion tmp : NMSVersion.class.getEnumConstants())
                if (tmp.name().compareToIgnoreCase(version) == 0)
                {
                    SERVER_VERSION = tmp;
                    break;
                }
        }
    }

    //They change packets in this version.
    public static boolean isMajor() {
        return compare(NMSVersion.v1_19_R2);
    }

    public static boolean isRemappedVersion() {
        return compare(v1_17_R1);
    }

    public static boolean isLegacy() {
        return compare(NMSVersion.v1_8_R1);
    }

    public static boolean compare(NMSVersion nmsVersion) {
        return SERVER_VERSION.ordinal() >= nmsVersion.ordinal();
    }

    public static boolean compareIsBelow(NMSVersion nmsVersion) {
        return SERVER_VERSION.ordinal() <= nmsVersion.ordinal();
    }

}
