package me.jacktym.aiomacro.macros;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import kotlin.jvm.internal.Intrinsics;
import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;


public class Failsafe {

    public static boolean goToIsland = false;

    private static boolean goLobby = true;
    private static boolean goSkyblock = false;
    private static boolean goIsland = false;
    private static boolean startStopFly = false;
    private static boolean endStopFly = false;

    private int tick = 0;

    public static boolean bedrockFailsafe() {
        MovingObjectPosition mop = Main.mc.getRenderViewEntity().rayTrace(200, 1.0F);
        if (mop != null) {
            return Main.mcWorld.getBlockState(mop.getBlockPos()).getBlock() == Block.getBlockFromName("bedrock");
        } else {
            return false;
        }
    }

    public static boolean jacobFailsafe() {
        ScoreObjective sidebarObjective = Main.mcWorld.getScoreboard().getObjectiveInDisplaySlot(1);

        List<String> scoreboardLines = new ArrayList<>();
        Collection<Score> scores = Main.mcWorld.getScoreboard().getSortedScores(sidebarObjective);
        for (Score line : scores) {
            ScorePlayerTeam team = Main.mcWorld.getScoreboard().getPlayersTeam(line.getPlayerName());
            String scoreboardLine = ScorePlayerTeam.formatPlayerName(team, line.getPlayerName()).trim();
            String strippedCleansedScoreboardLine = Pattern.compile("(?i)�[0-9A-FK-ORZ]").matcher(scoreboardLine).replaceAll("");
            scoreboardLines.add(strippedCleansedScoreboardLine);
        }
        boolean jacobsEvent = false;
        for (String scoreboardLine : scoreboardLines) {
            if ((StringUtils.stripControlCodes(scoreboardLine).replaceAll("[^a-zA-Z0-9]", "").contains("YourIsland"))) {
                jacobsEvent = true;
            }
        }
        if (AIOMVigilanceConfig.devmode) {
            for (String scoreboardLine : scoreboardLines) {
                System.out.println(StringUtils.stripControlCodes(scoreboardLine).replaceAll("[^a-zA-Z0-9]", ""));
            }
        }
        return jacobsEvent;
    }

    public static boolean islandFailsafe() {
        if (Main.mcWorld.getScoreboard().getObjectiveInDisplaySlot(1) != null) {
            ScoreObjective sidebarObjective = Main.mcWorld.getScoreboard().getObjectiveInDisplaySlot(1);

            List<String> scoreboardLines = new ArrayList<>();
            Collection<Score> scores = Main.mcWorld.getScoreboard().getSortedScores(sidebarObjective);
            for (Score line : scores) {
                ScorePlayerTeam team = Main.mcWorld.getScoreboard().getPlayersTeam(line.getPlayerName());
                String scoreboardLine = ScorePlayerTeam.formatPlayerName(team, line.getPlayerName()).trim();
                String strippedCleansedScoreboardLine = Pattern.compile("(?i)�[0-9A-FK-ORZ]").matcher(scoreboardLine).replaceAll("");
                scoreboardLines.add(strippedCleansedScoreboardLine);
            }
            boolean onIsland = false;
            for (String scoreboardLine : scoreboardLines) {
                if ((StringUtils.stripControlCodes(scoreboardLine).replaceAll("[^a-zA-Z0-9]", "").contains("YourIsland"))) {
                    onIsland = true;
                }
            }
            if (AIOMVigilanceConfig.devmode) {
                for (String scoreboardLine : scoreboardLines) {
                    System.out.println(StringUtils.stripControlCodes(scoreboardLine).replaceAll("[^a-zA-Z0-9]", ""));
                }
            }
            return !onIsland;
        } else {
            return false;
        }
    }

    @SubscribeEvent
    public void playerTick(@NotNull TickEvent.ClientTickEvent event) {
        Intrinsics.checkNotNullParameter(event, "event");
        if (MacroHandler.isMacroOn && Main.mcPlayer != null && Main.mcWorld != null && AIOMVigilanceConfig.macroType != 2) {
            if (AIOMVigilanceConfig.bedrockfailsafe) {
                if (bedrockFailsafe()) {
                    Main.sendMarkedChatMessage("Macro Disabled! | " + EnumChatFormatting.RED + "FAILSAFE!");

                    MacroHandler.isMacroOn = false;

                    KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindRight.getKeyCode(), false);
                    KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindBack.getKeyCode(), false);
                    KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindLeft.getKeyCode(), false);
                    KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindForward.getKeyCode(), false);

                    if (AIOMVigilanceConfig.soundfailsafe) {
                        for (int i = 0; i < 5; i++) {
                            Main.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("note.pling"), (float) Main.mcPlayer.posX, (float) Main.mcPlayer.posY, (float) Main.mcPlayer.posZ));
                        }
                    }
                    if (AIOMVigilanceConfig.webhookAlerts) {

                        String screenshotLink = Objects.requireNonNull(Utils.takeScreenshot()).replace("\"", "");

                        String jsonString = "{\"content\":null,\"embeds\":[{\"title\":\"Failsafe Alert | Macro Stopped\",\"description\":\"The macro has stopped due to bedrock\\nbeing detected in front of the player.\\n\\nAccount: " + Main.mcPlayer.getName() + "\",\"color\":5814783,\"author\":{\"name\":\"AIO-Macro\"},\"image\":{\"url\":\"" + screenshotLink + "\"}}]}\n";

                        String trimmed = jsonString.trim();

                        JsonParser parser = new JsonParser();

                        JsonElement jsonElement = parser.parse(trimmed);

                        Utils.sendWebhook(jsonElement);
                    }
                }
            }
            //if (AIOMVigilanceConfig.isJacobfailsafe()) {
            //if (jacobFailsafe()) {
            //    MacroHandler.isMacroOn = false;
            //    if (AIOMVigilanceConfig.isSoundfailsafe()) {
            //        for (int i = 0; i < 5; i++) {
            //                if (Utils.millisPassed(Utils.currentTimeMillis() + 3)) {
            //                    Main.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("note.pling"), (float) Main.mcPlayer.posX, (float) Main.mcPlayer.posY, (float) Main.mcPlayer.posZ));
            //                }
            //        }
            //        Main.mcPlayer.addChatMessage((IChatComponent) new ChatComponentText(EnumChatFormatting.GOLD + "[AIOM]" + EnumChatFormatting.WHITE + "Macro Paused! | " + EnumChatFormatting.RED + "FAILSAFE!"));

            //        MacroHandler.isMacroOn = false;
            //    }
            //    if (AIOMVigilanceConfig.isWebhookAlerts()) {

            //        String screenshotLink = Utils.takeScreenshot().replace("\"", "");

            //        String jsonString = "{\"content\":null,\"embeds\":[{\"title\":\"Failsafe Alert | Macro Paused\",\"description\":\"The macro has paused due to a jacob's\\nevent starting.\\n\\nAccount: " + Main.mcPlayer.getName() + "\",\"color\":5814783,\"author\":{\"name\":\"AIO-Macro\"},\"image\":{\"url\":\"" + screenshotLink + "\"}}]}\n";

            //        String trimmed = jsonString.trim();

            //        JsonParser parser = new JsonParser();

            //       JsonElement jsonElement = parser.parse(trimmed);

            //       Utils.sendWebhook(jsonElement);
            //   }
            //}
            //}
            if (AIOMVigilanceConfig.islandfailsafe != 2) {
                if (islandFailsafe()) {
                    if (AIOMVigilanceConfig.soundfailsafe) {
                        for (int i = 0; i < 5; i++) {
                            Main.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("note.pling"), (float) Main.mcPlayer.posX, (float) Main.mcPlayer.posY, (float) Main.mcPlayer.posZ));
                        }
                    }
                    if (AIOMVigilanceConfig.islandfailsafe == 0) {
                        Main.sendMarkedChatMessage("Macro Disabled! | " + EnumChatFormatting.RED + "FAILSAFE!");

                        MacroHandler.isMacroOn = false;

                        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindRight.getKeyCode(), false);
                        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindBack.getKeyCode(), false);
                        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindLeft.getKeyCode(), false);
                        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindForward.getKeyCode(), false);

                        if (AIOMVigilanceConfig.webhookAlerts) {

                            String screenshotLink = Objects.requireNonNull(Utils.takeScreenshot()).replace("\"", "");

                            String jsonString = "{\"content\":null,\"embeds\":[{\"title\":\"Failsafe Alert | Macro Stopped\",\"description\":\"The macro has stopped due to not\\nbeing on your island.\\n\\nAccount: " + Main.mcPlayer.getName() + "\",\"color\":5814783,\"author\":{\"name\":\"AIO-Macro\"},\"image\":{\"url\":\"" + screenshotLink + "\"}}]}\n";

                            String trimmed = jsonString.trim();

                            JsonParser parser = new JsonParser();

                            JsonElement jsonElement = parser.parse(trimmed);

                            Utils.sendWebhook(jsonElement);
                        }
                    }
                    if (AIOMVigilanceConfig.islandfailsafe == 1) {
                        Main.sendMarkedChatMessage("Macro Paused! | " + EnumChatFormatting.RED + "FAILSAFE!");

                        MacroHandler.isMacroOn = false;

                        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindRight.getKeyCode(), false);
                        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindBack.getKeyCode(), false);
                        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindLeft.getKeyCode(), false);
                        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindForward.getKeyCode(), false);
                        if (!goToIsland) {
                            goToIsland = true;
                        }

                        if (AIOMVigilanceConfig.webhookAlerts) {

                            String screenshotLink = Objects.requireNonNull(Utils.takeScreenshot()).replace("\"", "");

                            String jsonString = "{\"content\":null,\"embeds\":[{\"title\":\"Failsafe Alert | Macro Paused\",\"description\":\"The macro has paused due to not\\nbeing on your island.\\n\\nAccount: " + Main.mcPlayer.getName() + "\",\"color\":5814783,\"author\":{\"name\":\"AIO-Macro\"},\"image\":{\"url\":\"" + screenshotLink + "\"}}]}\n";

                            String trimmed = jsonString.trim();

                            JsonParser parser = new JsonParser();

                            JsonElement jsonElement = parser.parse(trimmed);

                            Utils.sendWebhook(jsonElement);
                        }
                    }
                }
            }
            if (AIOMVigilanceConfig.autoWebhook != 0 && ((Utils.currentTimeMillis() - MacroHandler.macroStartMillis) / 60000 % AIOMVigilanceConfig.autoWebhook) == 0 && AIOMVigilanceConfig.webhookAlerts && (Utils.currentTimeMillis() - MacroHandler.macroStartMillis) >= 60000) {

                String screenshotLink = Objects.requireNonNull(Utils.takeScreenshot()).replace("\"", "");

                String jsonString = "{\"content\":null,\"embeds\":[{\"title\":\"Failsafe Alert | Automatic Webhook\",\"description\":\"The macro has not been stopped\\nthe automatic timer has passed.\\n\\nAccount: " + Main.mcPlayer.getName() + "\",\"color\":5814783,\"author\":{\"name\":\"AIO-Macro\"},\"image\":{\"url\":\"" + screenshotLink + "\"}}]}\n";

                String trimmed = jsonString.trim();

                JsonParser parser = new JsonParser();

                JsonElement jsonElement = parser.parse(trimmed);

                Utils.sendWebhook(jsonElement);

            }
            if (AIOMVigilanceConfig.autoDisable != 0 && ((Utils.currentTimeMillis() - MacroHandler.macroStartMillis) / 60000 % AIOMVigilanceConfig.autoDisable) == 0 && (Utils.currentTimeMillis() - MacroHandler.macroStartMillis) >= 60000) {
                MacroHandler.toggleMacro();
                if (AIOMVigilanceConfig.webhookAlerts) {

                    String screenshotLink = Objects.requireNonNull(Utils.takeScreenshot()).replace("\"", "");

                    String jsonString = "{\"content\":null,\"embeds\":[{\"title\":\"Failsafe Alert | Macro Stopped\",\"description\":\"The macro has stopped due to the\\nautomatic timer ending.\\n\\nAccount: " + Main.mcPlayer.getName() + "\",\"color\":5814783,\"author\":{\"name\":\"AIO-Macro\"},\"image\":{\"url\":\"" + screenshotLink + "\"}}]}\n";

                    String trimmed = jsonString.trim();

                    JsonParser parser = new JsonParser();

                    JsonElement jsonElement = parser.parse(trimmed);

                    Utils.sendWebhook(jsonElement);
                }
            }
        }
    }

    @SubscribeEvent
    public void goToIsland(@NotNull TickEvent.ClientTickEvent event) {
        if (goToIsland) {
            if (tick >= 1000 && Main.mcWorld != null && Main.mcPlayer != null) {
                if (goLobby) {
                    System.out.println("/l");
                    Main.mcPlayer.sendChatMessage("/l");
                    goLobby = false;
                    goSkyblock = true;
                } else if (goSkyblock) {
                    System.out.println("/skyblock");
                    Main.mcPlayer.sendChatMessage("/skyblock");
                    goSkyblock = false;
                    goIsland = true;
                } else if (goIsland) {
                    System.out.println("/is");
                    Main.mcPlayer.sendChatMessage("/is");
                    goIsland = false;
                    startStopFly = true;

                    Main.sendMarkedChatMessage("Macro Unpaused! | " + EnumChatFormatting.RED + "FAILSAFE!");

                    MacroHandler.isMacroOn = true;
                } else if (startStopFly) {
                    startStopFly = false;
                    endStopFly = true;

                    KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindSneak.getKeyCode(), true);
                } else if (endStopFly) {
                    endStopFly = false;
                    goLobby = true;
                    goToIsland = false;

                    KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindSneak.getKeyCode(), false);

                    MacroHandler.isMacroOn = true;
                }
                tick = 0;
            }

            tick++;
        }
    }
}
