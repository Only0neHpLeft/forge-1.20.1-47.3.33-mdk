package dev.bombardinokrokodylo.block.entity;

import dev.bombardinokrokodylo.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class ExplosiveChestBlockEntity extends ChestBlockEntity {
    private UUID ownerUUID;

    public ExplosiveChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXPLOSIVE_CHEST.get(), pos, state);
    }

    public void setOwner(UUID uuid) {
        this.ownerUUID = uuid;
        setChanged();
    }

    public boolean isOwner(Player player) {
        return player.getUUID().equals(ownerUUID);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (ownerUUID != null) {
            tag.putUUID("Owner", ownerUUID);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Owner")) {
            ownerUUID = tag.getUUID("Owner");
        }
    }
}
