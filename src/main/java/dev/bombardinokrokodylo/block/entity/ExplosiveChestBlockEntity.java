package dev.bombardinokrokodylo.block.entity;

import dev.bombardinokrokodylo.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class ExplosiveChestBlockEntity extends BlockEntity implements Container {
    private UUID ownerUUID;
    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);

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
        ContainerHelper.saveAllItems(tag, this.items);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Owner")) {
            ownerUUID = tag.getUUID("Owner");
        }
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.items.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack itemstack = ContainerHelper.removeItem(this.items, index, count);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }
        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.items, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.items.set(index, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.level.getBlockEntity(this.worldPosition) == this && 
               player.distanceToSqr(this.worldPosition.getX() + 0.5, 
                                   this.worldPosition.getY() + 0.5, 
                                   this.worldPosition.getZ() + 0.5) <= 64;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }
}
