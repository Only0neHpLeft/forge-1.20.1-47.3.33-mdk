package dev.bombardinokrokodylo.block;

import dev.bombardinokrokodylo.block.entity.ExplosiveChestBlockEntity;
import dev.bombardinokrokodylo.cumbum.CumBum;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CumBum.MOD_ID);

    public static final RegistryObject<BlockEntityType<ExplosiveChestBlockEntity>> EXPLOSIVE_CHEST =
            BLOCK_ENTITIES.register("explosive_chest",
                    () -> BlockEntityType.Builder.of(ExplosiveChestBlockEntity::new,
                            ModBlocks.EXPLOSIVE_TNT_CHEST.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}