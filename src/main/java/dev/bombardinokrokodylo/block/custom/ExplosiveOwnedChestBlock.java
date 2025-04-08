package dev.bombardinokrokodylo.block.custom;

import dev.bombardinokrokodylo.block.entity.ExplosiveChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class ExplosiveOwnedChestBlock extends Block implements EntityBlock {

    public ExplosiveOwnedChestBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ExplosiveChestBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, @Nullable ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer instanceof Player player) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ExplosiveChestBlockEntity chest) {
                chest.setOwner(player.getUUID());
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ExplosiveChestBlockEntity chest) {
            if (!chest.isOwner(player)) {
                level.explode(null, pos.getX(), pos.getY(), pos.getZ(),
                        50.0f, Level.ExplosionInteraction.TNT);
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                return InteractionResult.SUCCESS;
            }

            MenuProvider menuProvider = new SimpleMenuProvider(
                    (id, inventory, p) -> ChestMenu.threeRows(id, inventory, chest),
                    Component.translatable("block.cumbum.explosive_tnt_chest")
            );
            
            NetworkHooks.openScreen((ServerPlayer) player, menuProvider, pos);
            player.awardStat(Stats.OPEN_CHEST);
        }

        return InteractionResult.CONSUME;
    }
}