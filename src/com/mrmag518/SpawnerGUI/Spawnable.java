package com.mrmag518.spawnergui;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public enum Spawnable {
    CREEPER(EntityType.CREEPER, "Creeper", (byte)50),
    SKELETON(EntityType.SKELETON, "Skeleton", (byte)51),
    SPIDER(EntityType.SPIDER, "Spider", (byte)52),
    GIANT(EntityType.GIANT, "Giant", (byte)53),
    ZOMBIE(EntityType.ZOMBIE, "Zombie", (byte)54),
    SLIME(EntityType.SLIME, "Slime", (byte)55),
    GHAST(EntityType.GHAST, "Ghast", (byte)56),
    PIG_ZOMBIE(EntityType.PIG_ZOMBIE, "PigZombie", (byte)57),
    ENDERMAN(EntityType.ENDERMAN, "Enderman", (byte)58),
    CAVE_SPIDER(EntityType.CAVE_SPIDER, "CaveSpider", (byte)59),
    SILVERFISH(EntityType.SILVERFISH, "Silverfish", (byte)60),
    BLAZE(EntityType.BLAZE, "Blaze", (byte)61),
    MAGMA_CUBE(EntityType.MAGMA_CUBE, "MagmaCube", (byte)62),
    ENDER_DRAGON(EntityType.ENDER_DRAGON, "EnderDragon", (byte)63),
    WITHER(EntityType.WITHER, "Wither", (byte)64),
    BAT(EntityType.BAT, "Bat", (byte)65),
    WITCH(EntityType.WITCH, "Witch", (byte)66),
    PIG(EntityType.PIG, "Pig", (byte)90),
    SHEEP(EntityType.SHEEP, "Sheep", (byte)91),
    COW(EntityType.COW, "Cow", (byte)92),
    CHICKEN(EntityType.CHICKEN, "Chicken", (byte)93),
    SQUID(EntityType.SQUID, "Squid", (byte)94),
    WOLF(EntityType.WOLF, "Wolf", (byte)95),
    MUSHROOM_COW(EntityType.MUSHROOM_COW, "Mooshroom", (byte)96),
    SNOWMAN(EntityType.SNOWMAN, "SnowGolem", (byte)97),
    OCELOT(EntityType.OCELOT, "Ocelot", (byte)98),
    IRON_GOLEM(EntityType.IRON_GOLEM, "IronGolem", (byte)99),
    HORSE(EntityType.HORSE, "Horse", (byte)100),
    VILLAGER(EntityType.VILLAGER, "Villager", (byte)120);

    private final EntityType type;
    private final String name;
    private final byte data;

    private Spawnable(EntityType type, String name, byte data) {
        this.type = type;
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public byte getData() {
        return data;
    }

    public EntityType getType() {
        return type;
    }

    public ItemStack getSpawnEgg() {
        return new ItemStack(Material.MONSTER_EGG, 1, data);
    }

    public static Spawnable from(EntityType type) {
        for(Spawnable s : values()) {
            if(s.getType() == type) {
                return s;
            }
        }
        return null;
    }
}
