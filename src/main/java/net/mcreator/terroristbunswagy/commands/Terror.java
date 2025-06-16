package net.mcreator.terroristbunswagy.commands; // 패키지 이름 유지

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.mcreator.terroristbunswagy.TerroristBunswagyMod; // 메인 플러그인 클래스 임포트 유지

public class Terror implements CommandExecutor {

    // MCreator의 자동 생성 코드에 맞춰 인자 없는 생성자를 사용합니다.
    // 플러그인 인스턴스는 메인 클래스의 static 변수를 통해 접근합니다.
    public Terror() {
        // 생성자에서 특별히 할 일은 없습니다.
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 명령어 레이블과 권한 확인
        if (label.equalsIgnoreCase("terrorcutter") && sender.hasPermission("terrorcutter.use")) { // 명령어 레이블 및 권한 변경
            Bukkit.getLogger().info("[DEBUG] onCommand entered - command label and permission check passed."); // 디버그 메시지

            // Check if exactly 1 argument is provided (player name)
            if (args.length != 1) {
                sender.sendMessage("§cUsage: /terrorcutter <playername>"); // 사용법 메시지 변경
                Bukkit.getLogger().info("[DEBUG] Usage error: missing arguments."); // 디버그 메시지
                return false; // Command failed
            }
            Bukkit.getLogger().info("[DEBUG] Argument check passed. Argument: " + args[0]); // 디버그 메시지

            // Get the target player name
            String targetPlayerName = args[0];
            Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

            // Check if the target player exists and is online
            if (targetPlayer == null || !targetPlayer.isOnline()) {
                sender.sendMessage("§cPlayer '" + targetPlayerName + "' not found or is offline."); // 플레이어 찾을 수 없음 메시지 변경
                Bukkit.getLogger().info("[DEBUG] Target player lookup failed: " + targetPlayerName); // 디버그 메시지
                return false; // Command failed
            }
            Bukkit.getLogger().info("[DEBUG] Target player lookup successful: " + targetPlayer.getName()); // 디버그 메시지

            // --- Execute Functionality ---

            // 1. Change target player's game mode to Survival
            targetPlayer.setGameMode(GameMode.SURVIVAL);
            sender.sendMessage("§aChanged " + targetPlayer.getName() + "'s game mode to Survival."); // 메시지 변경
            Bukkit.getLogger().info("[DEBUG] Game mode change complete."); // 디버그 메시지

            // 2. Teleport the player 1000 blocks up
            // Add 1000 to the current location's Y coordinate.
            Location initialTeleportLoc = targetPlayer.getLocation().clone().add(0, 1000, 0);
            targetPlayer.teleport(initialTeleportLoc);
            sender.sendMessage("§aTeleported " + targetPlayer.getName() + " 1000 blocks up."); // 메시지 변경
            Bukkit.getLogger().info("[DEBUG] Initial 1000 block up teleport complete."); // 디버그 메시지


            // 3. Repeat player view rotation and 1-block downward teleport (fall simulation)
            // Schedule the BukkitRunnable using TerroristBunswagyMod.plugin
            new BukkitRunnable() {
                @Override
                public void run() {
                    // Stop the task if the target player is no longer online or is dead.
                    // We do not check if they are on the ground, as they should fall until death.
                    if (!targetPlayer.isOnline() || targetPlayer.isDead()) {
                        Bukkit.getLogger().info("[DEBUG] View rotation/fall task stopped: " + targetPlayer.getName()); // 디버그 메시지
                        this.cancel();
                        return;
                    }

                    // Reapply Survival game mode to ensure it stays.
                    if (targetPlayer.getGameMode() != GameMode.SURVIVAL) {
                         targetPlayer.setGameMode(GameMode.SURVIVAL);
                         Bukkit.getLogger().info("[DEBUG] Survival mode reapplied: " + targetPlayer.getName()); // 디버그 메시지
                    }

                    // Get the current location.
                    Location currentLocation = targetPlayer.getLocation();

                    // Calculate the location 1 block down.
                    Location targetLocation = currentLocation.clone().subtract(0, 1, 0); // Subtract 1 from Y coordinate.

                    // Get current view direction (yaw and pitch).
                    float currentYaw = currentLocation.getYaw();
                    float currentPitch = currentLocation.getPitch();

                    // Create a new location object. (Coordinates are moved down, view is rotated)
                    Location newLocation = new Location(
                        currentLocation.getWorld(),
                        targetLocation.getX(), // X coordinate (moved down)
                        targetLocation.getY(), // Y coordinate (moved down)
                        targetLocation.getZ(), // Z coordinate (moved down)
                        currentYaw + 15.0f, // Rotate yaw by 15 degrees
                        currentPitch // Keep pitch the same
                    );

                    // Teleport the player to the new location.
                    targetPlayer.teleport(newLocation);
                    // Bukkit.getLogger().info("[DEBUG] Teleported 1 block down and applied view rotation: " + targetPlayer.getName()); // Commented out to avoid spam
                }
            // runTaskTimer(plugin instance, start delay in ticks, repeat interval in ticks)
            // Use TerroristBunswagyMod.plugin to access the main plugin instance.
            // Start after 10 ticks (0.5 seconds) delay, repeat every 1 tick (0.05 seconds) for smooth fall simulation.
            }.runTaskTimer(TerroristBunswagyMod.plugin, 10L, 1L); // 초기 순간이동 후 10틱(0.5초) 딜레이 후 시작

            targetPlayer.sendMessage("§cYou have been processed by 'Terror Cutter'!"); // 메시지 변경
            Bukkit.getLogger().info("[DEBUG] Message sent to target player."); // 디버그 메시지

            return true; // Command successful
        }
        Bukkit.getLogger().info("[DEBUG] onCommand entered - command label or permission check failed."); // 디버그 메시지
        return false;
    }
}
