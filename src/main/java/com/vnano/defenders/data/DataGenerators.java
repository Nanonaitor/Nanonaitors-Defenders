package com.vnano.defenders.data;
import com.vnano.defenders.DefendersMod; import com.vnano.defenders.item.DefenderTier; import com.vnano.defenders.registry.ModItems;
import java.util.function.Consumer; import net.minecraft.data.*; import net.minecraft.data.recipes.*; import net.minecraft.tags.ItemTags; import net.minecraft.world.item.*; import net.minecraft.world.level.ItemLike; import net.minecraftforge.common.Tags; import net.minecraftforge.data.event.GatherDataEvent; import net.minecraftforge.eventbus.api.SubscribeEvent; import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid=DefendersMod.MOD_ID,bus=Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {
 @SubscribeEvent public static void gather(GatherDataEvent e){e.getGenerator().addProvider(e.includeServer(),new Recipes(e.getGenerator().getPackOutput()));}
 private static final class Recipes extends RecipeProvider {
  Recipes(PackOutput out){super(out);} @Override protected void buildRecipes(Consumer<FinishedRecipe> out){
   shaped(out,DefenderTier.WOOD,ItemTags.PLANKS,Items.OAK_PLANKS); shaped(out,DefenderTier.STONE,Tags.Items.COBBLESTONE,Items.COBBLESTONE);
   shaped(out,DefenderTier.COPPER,Items.COPPER_INGOT,Items.COPPER_INGOT); shaped(out,DefenderTier.GOLD,Items.GOLD_INGOT,Items.GOLD_INGOT);
   shaped(out,DefenderTier.IRON,Items.IRON_INGOT,Items.IRON_INGOT); shaped(out,DefenderTier.DIAMOND,Items.DIAMOND,Items.DIAMOND); shaped(out,DefenderTier.NETHERITE,Items.NETHERITE_INGOT,Items.NETHERITE_INGOT);
  }
  private static void shaped(Consumer<FinishedRecipe> out,DefenderTier tier,Object material,ItemLike unlock){
   ShapedRecipeBuilder b=ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,ModItems.get(tier).get()).pattern(" M").pattern("SM").pattern(" M").define('S',Items.STICK).unlockedBy("has_material",has(unlock));
   if(material instanceof ItemLike i)b.define('M',i);else if(material instanceof net.minecraft.tags.TagKey<?> tag)b.define('M',(net.minecraft.tags.TagKey<Item>)tag);
   b.save(out);
  }
 }
 private DataGenerators(){}
}
