package com.jester.jestertricks.item;

import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Caja Negra: un bolsillo al mundo del Jester.
 * Clic derecho: abre el bolsillo (27 huecos que viajan con la caja).
 * Agachado + clic derecho sobre una criatura: se la traga (una cada vez; ni jugadores ni jefes).
 * Agachado + clic derecho en el suelo (o al aire) con una criatura dentro: la suelta ahí.
 */
public class BlackBoxItem extends Item {
    private static final String POCKET = "Pocket";
    private static final String MOB = "Mob";
    private static final String MOB_NAME = "MobName";

    public BlackBoxItem(Properties props) {
        super(props);
    }

    private static boolean hasMob(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(MOB);
    }

    // ------------------------------------------------------------------ tragar criaturas
    @Override
    public InteractionResult interactLivingEntity(ItemStack ignored, Player player, LivingEntity target, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;
        if (!JesterLock.check(player)) return InteractionResult.FAIL;
        if (player.level().isClientSide) return InteractionResult.SUCCESS;
        if (hasMob(stack) || target instanceof Player || target.getType().is(Tags.EntityTypes.BOSSES)) {
            player.displayClientMessage(Component.translatable("message.jestertricks.box_cannot").withStyle(ChatFormatting.GRAY), true);
            return InteractionResult.FAIL;
        }
        CompoundTag mob = new CompoundTag();
        if (!target.save(mob)) return InteractionResult.FAIL;
        CompoundTag tag = stack.getOrCreateTag();
        tag.put(MOB, mob);
        tag.putString(MOB_NAME, target.getName().getString());
        if (player.level() instanceof ServerLevel sl) {
            Fx.rift(sl, target.position().add(0, target.getBbHeight() / 2, 0), player.getYRot(), Math.max(0.6, target.getBbHeight() * 0.6));
        }
        Fx.play(player.level(), target.position(), ModSounds.RIFT.get(), 1.0F, 1.6F);
        target.discard();
        return InteractionResult.SUCCESS;
    }

    private static boolean release(Level level, Player player, ItemStack stack, Vec3 at) {
        if (!hasMob(stack)) return false;
        CompoundTag mob = stack.getTag().getCompound(MOB);
        Entity e = EntityType.create(mob, level).orElse(null);
        stack.getTag().remove(MOB);
        stack.getTag().remove(MOB_NAME);
        if (e == null) return false;
        e.moveTo(at.x, at.y, at.z, player.getYRot() + 180.0F, 0.0F);
        level.addFreshEntity(e);
        if (level instanceof ServerLevel sl) {
            Fx.rift(sl, at.add(0, e.getBbHeight() / 2, 0), player.getYRot(), Math.max(0.6, e.getBbHeight() * 0.6));
            Fx.confetti(sl, at.add(0, 0.5, 0), 8, 0.5);
        }
        Fx.play(level, at, ModSounds.RIFT.get(), 1.0F, 0.7F);
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null || !player.isShiftKeyDown() || !hasMob(ctx.getItemInHand())) return InteractionResult.PASS;
        if (!JesterLock.check(player)) return InteractionResult.FAIL;
        if (ctx.getLevel().isClientSide) return InteractionResult.SUCCESS;
        BlockPos p = ctx.getClickedPos().relative(ctx.getClickedFace());
        release(ctx.getLevel(), player, ctx.getItemInHand(), Vec3.atBottomCenterOf(p));
        return InteractionResult.CONSUME;
    }

    // ------------------------------------------------------------------ bolsillo de objetos
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!JesterLock.check(player)) return InteractionResultHolder.fail(stack);
        if (level.isClientSide) return InteractionResultHolder.success(stack);

        if (player.isShiftKeyDown()) {
            if (hasMob(stack)) {
                Vec3 look = player.getLookAngle();
                release(level, player, stack, player.position().add(look.x * 2.5, 0, look.z * 2.5));
            }
            return InteractionResultHolder.success(stack);
        }

        SimpleContainer pocket = new SimpleContainer(27);
        NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
        if (stack.hasTag()) ContainerHelper.loadAllItems(stack.getTag().getCompound(POCKET), items);
        for (int i = 0; i < 27; i++) pocket.setItem(i, items.get(i));

        player.openMenu(new SimpleMenuProvider((id, inv, p) -> new ChestMenu(MenuType.GENERIC_9x3, id, inv, pocket, 3) {
            @Override
            public boolean stillValid(Player who) {
                return who.getItemInHand(hand) == stack;
            }

            @Override
            public void clicked(int slot, int button, ClickType type, Player who) {
                // la caja no se puede meter dentro de sí misma
                if (type == ClickType.SWAP) return;
                if (slot >= 0 && slot < slots.size() && getSlot(slot).getItem() == stack) return;
                super.clicked(slot, button, type, who);
            }

            @Override
            public void removed(Player who) {
                super.removed(who);
                NonNullList<ItemStack> out = NonNullList.withSize(27, ItemStack.EMPTY);
                for (int i = 0; i < 27; i++) out.set(i, pocket.getItem(i));
                stack.getOrCreateTag().put(POCKET, ContainerHelper.saveAllItems(new CompoundTag(), out));
            }
        }, stack.getHoverName()));
        Fx.play(level, player.position(), ModSounds.RIFT.get(), 0.5F, 1.8F);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Lore.add(this, tooltip, 3);
        if (hasMob(stack)) {
            tooltip.add(Component.translatable("message.jestertricks.box_holds", stack.getTag().getString(MOB_NAME))
                    .withStyle(ChatFormatting.DARK_PURPLE));
        }
    }
}
