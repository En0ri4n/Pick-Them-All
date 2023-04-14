package fr.en0ri4n.pickthemall.utils;

import com.mojang.datafixers.util.Pair;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftMetaBook;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static fr.en0ri4n.pickthemall.utils.Colors.*;

@SuppressWarnings("ConstantConditions")
public class Utils
{
    public static final Comparator<List<?>> LIST_COMPARATOR = Comparator.comparingInt(List::size);

    public static final List<Pair<PotionEffect, String>> DEATH_PENALTIES = Arrays.asList(
            Pair.of(effect(PotionEffectType.SLOW, 120 * 20, 3), gray("Tout doux le loup")),
            Pair.of(effect(PotionEffectType.SLOW_DIGGING, 30 * 20, 3), gold("Un mineur fatigué")),
            Pair.of(effect(PotionEffectType.DARKNESS, 90 * 20, 5), darkPurple("Un réveil compliqué")),
            Pair.of(effect(PotionEffectType.HUNGER, 60 * 20, 4), yellow("Un jeûne venu de nulle part")),
            Pair.of(effect(PotionEffectType.WEAKNESS, 120 * 20, 3), blue("En avril ne te découvre pas d'un fil")));

    public static ItemStack tool(Material material, int digSpeedLvl, int fortunelvl)
    {
        ItemStack itemStack = unbreakable(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addEnchant(Enchantment.DIG_SPEED, digSpeedLvl, true);
        itemMeta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, fortunelvl, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void setName(ItemStack itemStack, String name)
    {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
    }

    public static ItemStack unbreakable(Material material)
    {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static ItemStack unbreakable(Material material, int amount)
    {
        ItemStack itemStack = unbreakable(material);
        itemStack.setAmount(amount);
        return itemStack;
    }

    public static ItemStack enchant(Material material, Enchantment enchantment, int level)
    {
        ItemStack itemStack = unbreakable(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addEnchant(enchantment, level, true);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static PotionEffect effect(PotionEffectType effectType, int time, int level)
    {
        return new PotionEffect(effectType, time, level);
    }

    public static ItemStack getTutorialBook()
    {
        ItemStack bookStack = new ItemStack(Material.WRITTEN_BOOK);
        CraftMetaBook bookMeta = (CraftMetaBook) bookStack.getItemMeta();
        bookMeta.setTitle(red("Tutorial Book"));
        bookMeta.setAuthor("En0ri4n");

        List<String> pages = new ArrayList<>();

        pages.add("""
                    §4Pick §cThem §6All§r

                §1Règles du jeu §r:
                Récupérer le plus d'item dans son inventaire avant la fin du temps impartie.

                C'est un petit mode de jeu chill, il n'y a pas de règles particulières, juste s'amuser.
                
                """);

        pages.add(darkRed("Additional Informations\n\n") + reset("- All recipes given\n- Smelt time of 3s\n- Death Penalties"));

        bookMeta.setPages(pages);
        bookStack.setItemMeta(bookMeta);

        return bookStack;
    }

    public static Pair<PotionEffect, String> getRandomDeathPenalty()
    {
        return Randomizer.random(DEATH_PENALTIES);
    }
}
