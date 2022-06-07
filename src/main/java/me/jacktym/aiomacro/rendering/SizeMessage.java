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

public class SizeMessage implements IMessageHandler<SizeMessage, SizeMessage>, IMessage {

    private int size;

    private UUID playerId;

    public SizeMessage() {

    }

    public SizeMessage(UUID playerId, int size) {
        this.playerId = playerId;
        this.size = size;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.size = buf.readInt();
        this.playerId = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.size);
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
    }

    @Override
    public SizeMessage onMessage(SizeMessage message, MessageContext ctx) {
        EntityPlayer player;

        if (ctx.side == Side.SERVER) {
            player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerByUUID(message.playerId);
            Main.NETWORK.sendToAll(new SizeMessage(message.playerId, message.size));
        } else {
            player = Main.mcWorld.getPlayerEntityByUUID(message.playerId);
        }

        if (player != null) {
            AssHook hook = AssHook.get(player);

            if (hook != null) {
                hook.setSize(message.size);
            }
        }

        return null;
    }

}