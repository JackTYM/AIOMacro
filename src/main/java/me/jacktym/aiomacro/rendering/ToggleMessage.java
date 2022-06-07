package me.jacktym.aiomacro.rendering;

import io.netty.buffer.ByteBuf;
import me.jacktym.aiomacro.Main;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

public class ToggleMessage implements IMessageHandler<ToggleMessage, ToggleMessage>, IMessage {

    private boolean enable;

    private UUID playerId;

    public ToggleMessage() {

    }

    public ToggleMessage(UUID playerId, boolean enable) {
        this.playerId = playerId;
        this.enable = enable;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.enable = buf.readBoolean();
        this.playerId = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.enable);
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
    }

    @Override
    public ToggleMessage onMessage(ToggleMessage message, MessageContext ctx) {
        EntityPlayer player;

        if (ctx.side == Side.SERVER) {
            player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerByUUID(message.playerId);
            Main.NETWORK.sendToAll(new ToggleMessage(message.playerId, message.enable));
        } else {
            player = Main.mcWorld.getPlayerEntityByUUID(message.playerId);
        }

        if (player != null) {
            AssHook hook = AssHook.get(player);

            if (hook != null) {
                hook.setEnabled(message.enable);
            }
        }

        return null;
    }
}
