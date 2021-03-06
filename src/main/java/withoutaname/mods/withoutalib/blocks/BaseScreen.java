package withoutaname.mods.withoutalib.blocks;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BaseScreen<T extends Container> extends ContainerScreen<T> {
	
	protected final ResourceLocation GUI_TEXTURE;
	
	public BaseScreen(T container, ResourceLocation gui_texture, PlayerInventory playerInventory, ITextComponent title, int xSize, int ySize) {
		super(container, playerInventory, title);
		this.GUI_TEXTURE = gui_texture;
		this.imageWidth = xSize;
		this.imageHeight = ySize;
		this.inventoryLabelY = this.imageHeight - 94;
	}
	
	@Override
	public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(matrixStack, mouseX, mouseY);
	}
	
	@Override
	protected void renderBg(@Nonnull MatrixStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		assert this.minecraft != null;
		this.minecraft.getTextureManager().bind(GUI_TEXTURE);
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
	}
	
}