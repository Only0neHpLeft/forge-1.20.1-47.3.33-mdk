package dev.bombardinokrokodylo.block.entity;

import dev.bombardinokrokodylo.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class ExplosiveChestBlockEntity extends BlockEntity implements Container {
    private UUID ownerUUID;
    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
    
    private boolean isExploding = false;
    private int explosionTimer = 0;
    private static final int EXPLOSION_DELAY = 40;

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
    
    public void startExplosion() {
        if (!isExploding) {
            isExploding = true;
            explosionTimer = EXPLOSION_DELAY;
            setChanged();
            
            if (level != null) {
                level.playSound(null, worldPosition, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, ExplosiveChestBlockEntity blockEntity) {
        if (blockEntity.isExploding) {
            blockEntity.explosionTimer--;
            
            if (level instanceof ServerLevel serverLevel) {
                double x = pos.getX() + 0.5;
                double y = pos.getY() + 0.5;
                double z = pos.getZ() + 0.5;
                
                int particleCount = 5 + (EXPLOSION_DELAY - blockEntity.explosionTimer) / 5;
                
                for (int i = 0; i < particleCount; i++) {
                    double offsetX = level.random.nextDouble() - 0.5;
                    double offsetY = level.random.nextDouble() - 0.5;
                    double offsetZ = level.random.nextDouble() - 0.5;
                    
                    if (level.random.nextBoolean()) {
                        serverLevel.sendParticles(ParticleTypes.SMOKE, 
                                x + offsetX, y + offsetY, z + offsetZ, 
                                1, 0, 0, 0, 0.05);
                    } else {
                        serverLevel.sendParticles(ParticleTypes.FLAME, 
                                x + offsetX, y + offsetY, z + offsetZ, 
                                1, 0, 0, 0, 0.05);
                    }
                }
                
                if (blockEntity.explosionTimer % 5 == 0) {
                    float pitch = 0.8F + (EXPLOSION_DELAY - blockEntity.explosionTimer) * 0.01F;
                    level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.BLOCKS, 1.0F, pitch);
                }
            }
            
            if (blockEntity.explosionTimer <= 0) {
                level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        50.0f, Level.ExplosionInteraction.TNT);
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (ownerUUID != null) {
            tag.putUUID("Owner", ownerUUID);
        }
        ContainerHelper.saveAllItems(tag, this.items);
        
        tag.putBoolean("Exploding", isExploding);
        tag.putInt("ExplosionTimer", explosionTimer);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Owner")) {
            ownerUUID = tag.getUUID("Owner");
        }
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        
        isExploding = tag.getBoolean("Exploding");
        explosionTimer = tag.getInt("ExplosionTimer");
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