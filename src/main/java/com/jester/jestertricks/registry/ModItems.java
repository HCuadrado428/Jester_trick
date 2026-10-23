package com.jester.jestertricks.registry;

import com.jester.jestertricks.JesterTricks;
import com.jester.jestertricks.item.BangPistolItem;
import com.jester.jestertricks.item.BlackBoxItem;
import com.jester.jestertricks.item.FairHoopItem;
import com.jester.jestertricks.item.FateDeckItem;
import com.jester.jestertricks.item.JesterBellsItem;
import com.jester.jestertricks.item.LaughMirrorItem;
import com.jester.jestertricks.item.OriginStarItem;
import com.jester.jestertricks.item.EndlessScarvesItem;
import com.jester.jestertricks.item.JesterScepterItem;
import com.jester.jestertricks.item.JugglerKnivesItem;
import com.jester.jestertricks.item.LoreBlockItem;
import com.jester.jestertricks.item.MagicTopHatItem;
import com.jester.jestertricks.item.MagicWandItem;
import com.jester.jestertricks.item.RubberMalletItem;
import com.jester.jestertricks.item.SharpCardsItem;
import com.jester.jestertricks.item.SmokeBombItem;
import com.jester.jestertricks.item.SquirtingFlowerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, JesterTricks.MOD_ID);

    private static Item.Properties tool() {
        return new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant();
    }

    // --- Armas ---
    public static final RegistryObject<Item> JESTER_SCEPTER = ITEMS.register("jester_scepter", () -> new JesterScepterItem(tool()));
    public static final RegistryObject<Item> SHARP_CARDS = ITEMS.register("sharp_cards", () -> new SharpCardsItem(tool()));
    public static final RegistryObject<Item> JUGGLER_KNIVES = ITEMS.register("juggler_knives", () -> new JugglerKnivesItem(tool()));
    public static final RegistryObject<Item> RUBBER_MALLET = ITEMS.register("rubber_mallet", () -> new RubberMalletItem(tool()));
    public static final RegistryObject<Item> BANG_PISTOL = ITEMS.register("bang_pistol", () -> new BangPistolItem(tool()));

    // --- Trucos de mago ---
    public static final RegistryObject<Item> MAGIC_TOP_HAT = ITEMS.register("magic_top_hat", () -> new MagicTopHatItem(tool()));
    public static final RegistryObject<Item> MAGIC_WAND = ITEMS.register("magic_wand", () -> new MagicWandItem(tool()));
    public static final RegistryObject<Item> SMOKE_BOMB = ITEMS.register("smoke_bomb", () -> new SmokeBombItem(tool()));
    public static final RegistryObject<Item> ENDLESS_SCARVES = ITEMS.register("endless_scarves", () -> new EndlessScarvesItem(tool()));

    // --- Bromas y trampas ---
    public static final RegistryObject<Item> SQUIRTING_FLOWER = ITEMS.register("squirting_flower", () -> new SquirtingFlowerItem(tool()));
    public static final RegistryObject<Item> SURPRISE_BOX = ITEMS.register("surprise_box",
            () -> new LoreBlockItem(ModBlocks.SURPRISE_BOX.get(), new Item.Properties().rarity(Rarity.EPIC), 2));
    public static final RegistryObject<Item> WHOOPEE_CUSHION = ITEMS.register("whoopee_cushion",
            () -> new LoreBlockItem(ModBlocks.WHOOPEE_CUSHION.get(), new Item.Properties().rarity(Rarity.EPIC), 2));

    // --- Poderes del mundo de origen ---
    public static final RegistryObject<Item> ORIGIN_STAR = ITEMS.register("origin_star", () -> new OriginStarItem(tool()));
    public static final RegistryObject<Item> FAIR_HOOP = ITEMS.register("fair_hoop", () -> new FairHoopItem(tool()));
    public static final RegistryObject<Item> BLACK_BOX = ITEMS.register("black_box", () -> new BlackBoxItem(tool()));
    public static final RegistryObject<Item> FATE_DECK = ITEMS.register("fate_deck", () -> new FateDeckItem(tool()));
    public static final RegistryObject<Item> LAUGH_MIRROR = ITEMS.register("laugh_mirror", () -> new LaughMirrorItem(tool()));
    public static final RegistryObject<Item> JESTER_BELLS = ITEMS.register("jester_bells", () -> new JesterBellsItem(tool()));

    // --- Solo para dibujar los proyectiles (no salen en la pestaña) ---
    public static final RegistryObject<Item> THROWN_CARD = ITEMS.register("thrown_card", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> THROWN_KNIFE = ITEMS.register("thrown_knife", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BANG_FLAG = ITEMS.register("bang_flag", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SCARF_KNOT = ITEMS.register("scarf_knot", () -> new Item(new Item.Properties()));

    private ModItems() {}
}
