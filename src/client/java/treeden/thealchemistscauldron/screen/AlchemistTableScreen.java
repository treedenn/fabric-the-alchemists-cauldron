package treeden.thealchemistscauldron.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import treeden.thealchemistscauldron.TheAlchemistsCauldronMod;

import java.util.List;
import java.util.Map;

public class AlchemistTableScreen extends HandledScreen<ScreenHandler> {
    private static final Identifier TEXTURE = TheAlchemistsCauldronMod.id("textures/gui/container/alchemist_table.png");

    private final AlchemistTableScreenHandler handler;
    private boolean scrolling = false;
    private float scrollPosition = 0f;

    public AlchemistTableScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.handler = (AlchemistTableScreenHandler) handler;
        this.backgroundWidth = 208;
    }

    @Override
    protected void init() {
        super.init();
        titleY -= 2;
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        if (this.handler.getLiquidDropper() == null)
            return;

        drawLiquidData(context);

        if (hasScrollbar()) {
            context.drawTexture(TEXTURE, this.x + 115, (int) (this.y + 20 + 35 * this.scrollPosition), 160, 168, 7, 10);
        }

        List<Map.Entry<StatusEffect, Float>> effects = this.handler.getOrderedEffects();
        if (effects != null)
            for (int i = 0; i < effects.size(); i++) {
                drawStatusEffect(context, effects.get(i), this.x + 7, this.y + 14 + 19 * i);
            }
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.onClickScrollButton(this.x + 115, this.y + 14, mouseX, mouseY, () -> {
                int row = this.handler.getRow(scrollPosition);
                this.scrollPosition = this.handler.getScrollPosition(MathHelper.clamp(row-1, 0, this.handler.getOverflowRows()));
                this.handler.scrollItems(this.scrollPosition);
            });
            this.onClickScrollButton(this.x + 115, this.y + 65, mouseX, mouseY, () -> {
                int row = this.handler.getRow(scrollPosition);
                this.scrollPosition = this.handler.getScrollPosition(MathHelper.clamp(row+1, 0, this.handler.getOverflowRows()));
                this.handler.scrollItems(this.scrollPosition);
            });
            this.onClickScrollBar(mouseX, mouseY);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.scrolling = false;

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrolling) {
            this.calculateScrollPosition(mouseY);
            this.handler.scrollItems(this.scrollPosition);

            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!this.hasScrollbar())
            return false;

        this.scrollPosition = this.handler.getScrollPosition(this.scrollPosition, amount);
        this.handler.scrollItems(this.scrollPosition);
        return true;
    }

    private boolean hasScrollbar() {
        return this.handler.shouldShowScrollbar();
    }

    private boolean isPointWithinRegion(int x, int y, int width, int height, double xP, double yP) {
        return x <= xP && xP <= x + width && y <= yP && yP <= y + height;
    }

    private void onClickScrollBar(double mouseX, double mouseY) {
        if (hasScrollbar()) {
            if (this.isPointWithinRegion(this.x + 115, this.y + 20, 7, 45, mouseX, mouseY)) {
                this.calculateScrollPosition(mouseY);
                this.handler.scrollItems(this.scrollPosition);
                this.scrolling = true;
            }
        }
    }

    private void onClickScrollButton(int x, int y, double mouseX, double mouseY, Runnable runnable) {
        if (this.isPointWithinRegion(x, y, 7, 6, mouseX, mouseY)) {
            runnable.run();
        }
    }

    /**
     * Calculates the ratio between the scroll top and scroll bottom.
     * Subtracts the height of the bar at the bottom and half of it on the top to center the bar when moving the mouse.
     * @param mouseY Y coordinate of mouse
     */
    private void calculateScrollPosition(double mouseY) {
        int scrollTop = this.y + 20;
        int scrollBottom = this.y + 65;

        this.scrollPosition = (float) ((mouseY - scrollTop -5f) / ((scrollBottom - scrollTop) -10f));
        this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
    }

    private void drawLiquidData(DrawContext context) {
        int startX = this.x + 125, startY = this.y + 13;

        // draw mixed
        context.drawTexture(TEXTURE, startX, startY, 0, 208, 16, 16);
        Text mixedText = Text.of((int) (this.handler.mixed * 100) + "%");
        context.drawTextWrapped(this.textRenderer, mixedText, startX + 16, startY + 4, 20,0);

        // draw temperature
        context.drawTexture(TEXTURE, startX, startY += 20, 16, 208, 16, 16);
        Text temperatureText = Text.of((int) this.handler.temperature + "C");
        context.drawTextWrapped(this.textRenderer, temperatureText, startX + 16, startY + 4, 25,0);

        // draw duration
        context.drawTexture(TEXTURE, startX, startY += 20, 32, 208, 16, 16);
        Text durationText = Text.of(Integer.toString(this.handler.duration));
        context.drawTextWrapped(this.textRenderer, durationText, startX + 16, startY + 4, 25,0);

        // draw base potion
        String[] potionName = Text.translatable(this.handler.basePotion.getTranslationKey()).getString().split(" ");
        StringBuilder initials = new StringBuilder(potionName.length);
        for (String s : potionName) { initials.append(s.charAt(0)); }
        context.drawTextWrapped(this.textRenderer, Text.of(initials.toString()), this.x + 190, this.y + 18, 20,0);
    }

    private void drawStatusEffect(DrawContext context, Map.Entry<StatusEffect, Float> entry, int anchorX, int anchorY) {
        context.drawTexture(TEXTURE, anchorX, anchorY, 0, 166, 106, 19);
        Sprite sprite = MinecraftClient.getInstance().getStatusEffectSpriteManager().getSprite(entry.getKey());
        context.drawSprite(anchorX+1, anchorY+1, 0, 17, 17, sprite);

        float SCALE = 2 / 3f;
        int textX = (anchorX + 23);
        int textY = (anchorY + 7);

        context.getMatrices().push();
        context.getMatrices().translate(textX, textY, 0);
        context.getMatrices().scale(SCALE, SCALE, SCALE);

        MutableText seText = Text.translatable(entry.getKey().getTranslationKey())
                .append(ScreenTexts.SPACE).append(Text.translatable("enchantment.level." + entry.getValue().intValue()));
        context.drawText(this.textRenderer, seText, 0, 0, 0xffffffff, false);
        context.getMatrices().pop();
    }
}
