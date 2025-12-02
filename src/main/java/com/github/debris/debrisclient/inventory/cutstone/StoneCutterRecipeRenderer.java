package com.github.debris.debrisclient.inventory.cutstone;

import com.github.debris.debrisclient.util.AccessorUtil;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class StoneCutterRecipeRenderer {
    private static final StoneCutterRecipeRenderer INSTANCE = new StoneCutterRecipeRenderer();

    private final Minecraft mc = Minecraft.getInstance();
    private int recipeListX;
    private int recipeListY;
    private int recipesPerColumn;
    private int columnWidth;
    private int columns;
    private int numberTextWidth;
    private int gapColumn;
    private int entryHeight;
    private double scale;

    public static StoneCutterRecipeRenderer getInstance() {
        return INSTANCE;
    }

    public void renderStoneCutterRecipe(GuiGraphics graphics, int mouseX, int mouseY) {
        if (StoneCutterUtil.isStoneCutterRecipeViewOpen()) {
            StoneCutterRecipeStorage recipeStorage = StoneCutterRecipeStorage.getInstance();
            final int first = recipeStorage.getFirstVisibleRecipeId();
            final int countPerPage = recipeStorage.getRecipeCountPerPage();
            final int lastOnPage = first + countPerPage - 1;

            AbstractContainerScreen<?> gui = (AbstractContainerScreen<?>) GuiUtils.getCurrentScreen();

            this.calculateRecipePositions(gui);

            GuiContext drawContext = GuiContext.fromGuiGraphics(graphics);

            drawContext.pose().pushMatrix();
            drawContext.pose().translate(this.recipeListX, this.recipeListY);
            drawContext.pose().scale((float) this.scale, (float) this.scale);

            String str = StringUtils.translate("itemscroller.gui.label.recipe_page", (first / countPerPage) + 1, recipeStorage.getTotalRecipeCount() / countPerPage);

            drawContext.drawString(this.mc.font, str, 16, -12, 0xC0C0C0C0, false);

            for (int i = 0, recipeId = first; recipeId <= lastOnPage; ++i, ++recipeId) {
                ItemStack stack = recipeStorage.getRecipe(recipeId).getResult();
                boolean selected = recipeId == recipeStorage.getSelection();
                int row = i % this.recipesPerColumn;
                int column = i / this.recipesPerColumn;

                this.renderStoredRecipeStack(stack, recipeId, row, column, gui, selected, drawContext);
            }

            final int recipeId = this.getHoveredRecipeId(mouseX, mouseY, gui);
            StoneCutterRecipePattern recipe = recipeId >= 0 ? recipeStorage.getRecipe(recipeId) : recipeStorage.getSelectedRecipe();

            this.renderRecipeDetail(recipe, recipeStorage.getRecipeCountPerPage(), gui, drawContext);

            drawContext.pose().popMatrix();
        }
    }

    private void calculateRecipePositions(AbstractContainerScreen<?> gui) {
        StoneCutterRecipeStorage recipes = StoneCutterRecipeStorage.getInstance();
        final int gapHorizontal = 2;
        final int gapVertical = 2;
        final int stackBaseHeight = 16;

        this.recipesPerColumn = 9;
        this.columns = (int) Math.ceil((double) recipes.getRecipeCountPerPage() / (double) this.recipesPerColumn);
        this.numberTextWidth = 12;
        this.gapColumn = 4;

        int usableHeight = GuiUtils.getScaledWindowHeight();
        int usableWidth = AccessorUtil.getGuiLeft(gui);
        // Scale the maximum stack size by taking into account the relative gap size
        double gapScaleVertical = (1D - (double) gapVertical / (double) (stackBaseHeight + gapVertical));
        // the +1.2 is for the gap and page text height on the top and bottom
        int maxStackDimensionsVertical = (int) ((usableHeight / ((double) this.recipesPerColumn + 1.2)) * gapScaleVertical);
        // assume a maximum of 3x3 recipe size for now... thus columns + 3 stacks rendered horizontally
        double gapScaleHorizontal = (1D - (double) gapHorizontal / (double) (stackBaseHeight + gapHorizontal));
        int maxStackDimensionsHorizontal = (int) (((usableWidth - (this.columns * (this.numberTextWidth + this.gapColumn))) / (this.columns + 3 + 0.8)) * gapScaleHorizontal);
        int stackDimensions = (int) Math.min(maxStackDimensionsVertical, maxStackDimensionsHorizontal);

        this.scale = (double) stackDimensions / (double) stackBaseHeight;
        this.entryHeight = stackBaseHeight + gapVertical;
        this.recipeListX = usableWidth - (int) ((this.columns * (stackBaseHeight + this.numberTextWidth + this.gapColumn) + gapHorizontal) * this.scale);
        this.recipeListY = (int) (this.entryHeight * this.scale);
        this.columnWidth = stackBaseHeight + this.numberTextWidth + this.gapColumn;
    }

    private void renderStoredRecipeStack(ItemStack stack,
                                         int recipeId,
                                         int row,
                                         int column,
                                         AbstractContainerScreen<?> gui,
                                         boolean selected,
                                         GuiContext drawContext) {
        final Font font = this.mc.font;
        final String indexStr = String.valueOf(recipeId + 1);

        int x = column * this.columnWidth + this.gapColumn + this.numberTextWidth;
        int y = row * this.entryHeight;
        this.renderStackAt(stack, x, y, selected, drawContext);

        float scale = 0.75F;
        x = x - (int) (font.width(indexStr) * scale) - 2;
        y = row * this.entryHeight + this.entryHeight / 2 - font.lineHeight / 2;

        drawContext.pose().pushMatrix();
        drawContext.pose().translate(x, y);
        drawContext.pose().scale(scale, scale);

        drawContext.drawString(font, indexStr, 0, 0, 0xFFC0C0C0, false);

        drawContext.pose().popMatrix();
    }

    public int getHoveredRecipeId(int mouseX, int mouseY, AbstractContainerScreen<?> gui) {
        if (StoneCutterUtil.isStoneCutterRecipeViewOpen()) {
            this.calculateRecipePositions(gui);
            final int stackDimensions = (int) (16 * this.scale);

            for (int column = 0; column < this.columns; ++column) {
                int startX = this.recipeListX + (int) ((column * this.columnWidth + this.gapColumn + this.numberTextWidth) * this.scale);

                if (mouseX >= startX && mouseX <= startX + stackDimensions) {
                    for (int row = 0; row < this.recipesPerColumn; ++row) {
                        int startY = this.recipeListY + (int) (row * this.entryHeight * this.scale);

                        if (mouseY >= startY && mouseY <= startY + stackDimensions) {
                            return StoneCutterRecipeStorage.getInstance().getFirstVisibleRecipeId() + column * this.recipesPerColumn + row;
                        }
                    }
                }
            }
        }

        return -1;
    }

    private void renderRecipeDetail(StoneCutterRecipePattern recipe,
                                    int recipeCountPerPage,
                                    AbstractContainerScreen<?> gui,
                                    GuiContext drawContext) {
        int x = -3 * 17 + 2;
        int y = 3 * this.entryHeight;
        this.renderStackAt(recipe.getInput(), x, y, false, drawContext);
        this.renderStackAt(new ItemStack(Items.STONECUTTER), x + 17, y, false, drawContext);
        this.renderStackAt(recipe.getResult(), x + 34, y, false, drawContext);
    }

    private void renderStackAt(ItemStack stack, int x, int y, boolean border, GuiContext drawContext) {
        final int w = 16;

        if (border) {
            // Draw a light/white border around the stack
            RenderUtils.drawOutline(drawContext, x - 1, y - 1, w + 2, w + 2, 0xFFFFFFFF);
        }

        // light background for the item
        RenderUtils.drawRect(drawContext, x, y, w, w, 0x20FFFFFF);

        if (!stack.isEmpty()) {
            stack = stack.copy();
            stack.setCount(1);

            drawContext.pose().pushMatrix();
            drawContext.pose().translate(0, 0);      // z = 100.f

//            DiffuseLighting.enableGuiDepthLighting();
            drawContext.renderItem(stack, x, y);

            drawContext.pose().popMatrix();
        }
    }
}
