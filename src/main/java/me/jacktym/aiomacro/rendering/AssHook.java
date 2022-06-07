package me.jacktym.aiomacro.rendering;

import me.jacktym.aiomacro.Main;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class AssHook implements IExtendedEntityProperties {
    private int aSize;

    private boolean isEnabled = false;

    public static AssHook get(EntityPlayer player) {
        return (AssHook) player.getExtendedProperties("big_booty_hook");
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        compound.setInteger("ass_size", this.aSize);
        compound.setBoolean("ass_enabled", this.isEnabled);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        this.aSize = compound.getInteger("ass_size");
        this.isEnabled = compound.getBoolean("ass_enabled");
    }

    @Override
    public void init(Entity entity, World world) {
        if (!entity.worldObj.isRemote) {
            Main.NETWORK.sendToServer(new SizeMessage(entity.getUniqueID(), this.aSize));
            Main.NETWORK.sendToServer(new ToggleMessage(entity.getUniqueID(), this.isEnabled));
        }
    }

    public int getSize() {
        return this.aSize;
    }

    public void setSize(int size) {
        this.aSize = size;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(boolean enable) {
        this.isEnabled = enable;
    }
}
