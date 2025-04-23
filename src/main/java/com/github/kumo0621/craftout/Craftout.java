package com.github.kumo0621.craftout;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Craftout extends JavaPlugin implements Listener {

    private final Set<Material> restrictedTools = new HashSet<>();
    private final Random random = new Random();
    // グローバル変数
    private String ToolCraft;
    private String bone;
    private String armament;
    private boolean dispenser;
    private String Enchant;
    private String oak;
    private String nougyou;

    private String anvil;

    private String rocket;
    @Override
    public void onEnable() {
        // デフォルトのconfigを保存する
        saveDefaultConfig();
        // configから値を読み込む
        loadConfigValues();
        // イベントリスナーを登録
        getServer().getPluginManager().registerEvents(this, this);

        // 制限するツールのリスト
        restrictedTools.addAll(Arrays.asList(
                Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL,
                Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE,
                Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE,
                Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE,
                Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD
        ));
    }

    // Configの値を読み込むメソッド
    public void loadConfigValues() {
        FileConfiguration config = this.getConfig();

        // 各値をグローバル変数に設定
        ToolCraft = config.getString("ToolCraft");
        bone = config.getString("bone");
        armament = config.getString("armament");
        dispenser = config.getBoolean("dispenser");
        Enchant = config.getString("Enchant");
        oak = config.getString("oak");
        nougyou = config.getString("nougyou");
        anvil = config.getString("anvil");
        rocket = config.getString("rocket");
    }
    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item != null && restrictedTools.contains(item.getType())) {
            if (event.getWhoClicked() instanceof Player player) {
                Team team = player.getScoreboard().getEntryTeam(player.getName());
                if (team == null || !team.getName().equals(ToolCraft)) {
                    // チーム名が「鍛冶屋」でなければ、クラフトをキャンセル
                    event.setCancelled(true);
                    player.sendMessage("あなたはこのツールをクラフトできません！");
                }
            }
        }
        if (item != null && item.getType() == Material.BONE_MEAL) {
            if (event.getWhoClicked() instanceof Player player) {
                Team team = player.getScoreboard().getEntryTeam(player.getName());
                if (team == null || !team.getName().equals(bone)) {
                    // チーム名が「鍛冶屋」でなければ、作業台のクラフトをキャンセル
                    event.setCancelled(true);
                    player.sendMessage("あなたは骨粉をクラフトすることができません！");
                }
            }
        }
        if (item != null && isArmor(item.getType())) {
            if (event.getWhoClicked() instanceof Player player) {
                Team team = player.getScoreboard().getEntryTeam(player.getName());
                if (team == null || !team.getName().equals(armament)) {
                    // チーム名が「裁縫師」でなければ、防具のクラフトをキャンセル
                    event.setCancelled(true);
                    player.sendMessage("あなたは防具をクラフトすることができません！");
                }
            }
        }
    }

    private boolean isArmor(Material material) {
        return material.name().endsWith("_HELMET") || material.name().endsWith("_CHESTPLATE") ||
                material.name().endsWith("_LEGGINGS") || material.name().endsWith("_BOOTS");
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        Block block = event.getBlock();

        // ディスペンサーからのアイテム放出をキャンセル
        if (block.getType() == Material.DISPENSER) {
            event.setCancelled(dispenser);
        }
        // ドロッパーからのアイテム放出はそのまま許可（何もしない）
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        Team team = player.getScoreboard().getEntryTeam(player.getName());
        if (team == null || !team.getName().equals(Enchant)) {
            // チーム名が「パン屋」でなければ、エンチャントをキャンセル
            event.setCancelled(true);
            player.sendMessage("あなたはエンチャントを行うことができません！");
        }
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Team team = player.getScoreboard().getEntryTeam(player.getName());
        World world = block.getWorld();
        Material type = block.getType();
        if (team == null || !team.getName().equals(oak)) {
            // Check if the block placed is a sapling and the world is The End
            if (type.name().endsWith("_SAPLING") && world.getEnvironment() == World.Environment.THE_END) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("苗木は木こりしか置けません");
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Animals) {
            Player player = event.getPlayer();
            Team team = player.getScoreboard().getEntryTeam(player.getName());
            if (team == null || !team.getName().equals(nougyou)) {
                // チーム名が「パン屋」でなければ、動物の繁殖をキャンセル
                event.setCancelled(true);
                player.sendMessage("あなたは動物の繁殖を行うことができません！");
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (isWood(block.getType())) {
            // 超極小確率でハチミツをドロップ
            if (random.nextDouble() < 0.001) { // 0.1% の確率
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.HONEYCOMB));
            }
        }
    }

    private boolean isWood(Material material) {
        switch (material) {
            case OAK_LOG:
            case SPRUCE_LOG:
            case BIRCH_LOG:
            case JUNGLE_LOG:
            case ACACIA_LOG:
            case DARK_OAK_LOG:
                return true;
            default:
                return false;
        }
    }
    @EventHandler
    public void anvil(PrepareAnvilEvent event) {
        AnvilInventory anvilInventory = event.getInventory();
        ItemStack firstItem = anvilInventory.getItem(0);  // 左側のアイテム
        ItemStack secondItem = anvilInventory.getItem(1);  // 右側のアイテム（素材）

        // 両方のアイテムが存在し、かつ耐久値のあるアイテムの場合
        if (firstItem != null && secondItem != null && firstItem.getType() != Material.AIR && secondItem.getType() != Material.AIR) {
            // 第一アイテムが耐久値を持つ場合
            if (firstItem.getType().getMaxDurability() > 0) {
                short currentDurability = firstItem.getDurability();
                short maxDurability = firstItem.getType().getMaxDurability();

                // 耐久値が最大値よりも減少している（つまり、回復しようとしている）
                if (currentDurability > 0) {
                    // イベントをキャンセルして修復を防ぐ
                    event.setResult(null);  // 修復結果をなしにする
                    event.getViewers().forEach(viewer -> {
                        if (viewer instanceof Player) {
                            ((Player) viewer).sendMessage("鉄床による耐久値の回復は禁止されています。");
                        }
                    });
                }
            }
        }
    }



    @EventHandler
    public void onAnvilUse(InventoryClickEvent event) {
        if (event.getInventory() instanceof AnvilInventory) {
            if (event.getWhoClicked() instanceof Player) {
                Player player = (Player) event.getWhoClicked();
                Team team = player.getScoreboard().getEntryTeam(player.getName());

                // 鍛冶屋チーム以外のプレイヤーが金床を使用しようとした場合
                if (team == null || !team.getName().equals(anvil)) {
                    event.setCancelled(true);
                    player.sendMessage("鍛冶屋チームのメンバーが金床を使用できます。");
                }
            }
        }
    }




    @EventHandler
    public void onRightClickNetherStar(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        ItemStack item = event.getItem();
        // メインハンドでネザースターを持っているかチェック
        if (mainHandItem != null && mainHandItem.getType() == Material.NETHER_STAR) {
            // オフハンドのアイテムの耐久値を回復
            if (offHandItem != null && offHandItem.getType().getMaxDurability() > 0) {
                offHandItem.setDurability((short) Math.max(offHandItem.getDurability() - 1000, 0));
                player.sendMessage("オフハンドのアイテムが修理されました。");

                // ネザースターを1つ消費
                if (mainHandItem.getAmount() > 1) {
                    mainHandItem.setAmount(mainHandItem.getAmount() - 1);
                } else {
                    // ネザースターが1つしかない場合は、アイテムを削除
                    player.getInventory().setItemInMainHand(null);
                }
            }
        }

    }




    @EventHandler
    public void onRocketItem(CraftItemEvent event) {
        // ロケット花火のクラフトをチェック
        if (event.getRecipe().getResult().getType() == Material.FIREWORK_ROCKET) {
            Player player = (Player) event.getWhoClicked();
            Team team = player.getScoreboard().getEntryTeam(player.getName());

            if (team == null || !team.getName().equals(rocket)) {
                event.setCancelled(true);
                player.sendMessage("あなたは鍛冶屋のチームに所属していないため、ロケット花火を作成することはできません。");
            }
        }
        if (event.getRecipe().getResult().getType() == Material.FIREWORK_STAR) {
            Player player = (Player) event.getWhoClicked();
            Team team = player.getScoreboard().getEntryTeam(player.getName());

            if (team == null || !team.getName().equals(rocket)) {
                event.setCancelled(true);
                player.sendMessage("あなたは鍛冶屋のチームに所属していないため、ロケット花火を作成することはできません。");
            }
        }
    }

}

