package withoutaname.mods.withoutalib.blocks;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseBakedModel implements IDynamicBakedModel {

	protected void putVertex(BakedQuadBuilder builder, Vector3d normal,
						   double x, double y, double z, float u, float v, TextureAtlasSprite sprite) {
		putVertex(builder, normal, x, y, z, u, v, sprite, 1.0f, 1.0f, 1.0f);
	}

	protected void putVertex(BakedQuadBuilder builder, Vector3d normal,
						   double x, double y, double z, float u, float v, TextureAtlasSprite sprite, float r, float g, float b) {

		ImmutableList<VertexFormatElement> elements = builder.getVertexFormat().getElements().asList();
		for (int j = 0 ; j < elements.size() ; j++) {
			VertexFormatElement e = elements.get(j);
			switch (e.getUsage()) {
				case POSITION:
					builder.put(j, (float) x, (float) y, (float) z, 1.0f);
					break;
				case COLOR:
					builder.put(j, r, g, b, 1.0f);
					break;
				case UV:
					switch (e.getIndex()) {
						case 0:
							float iu = sprite.getInterpolatedU(u);
							float iv = sprite.getInterpolatedV(v);
							builder.put(j, iu, iv);
							break;
						case 2:
							builder.put(j, (short) 0, (short) 0);
							break;
						default:
							builder.put(j);
							break;
					}
					break;
				case NORMAL:
					builder.put(j, (float) normal.x, (float) normal.y, (float) normal.z);
					break;
				default:
					builder.put(j);
					break;
			}
		}
	}

	protected static Vector3d v(double x, double y, double z) {
		return new Vector3d(x, y, z);
	}

	protected BakedQuad createQuad(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4, TextureAtlasSprite sprite) {
		return createQuad(v1, v2, v3, v4, sprite, false);
	}

	protected BakedQuad createQuad(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4, TextureAtlasSprite sprite, boolean reversed) {
		int u = sprite.getWidth();
		int v = sprite.getHeight();

		return createQuad(v1, v2, v3, v4, 0, 0, u, v, sprite, reversed);
	}

	protected BakedQuad createQuad(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4, float uFrom, float vFrom, float uTo, float vTo, TextureAtlasSprite sprite) {
		return createQuad(v1, v2, v3, v4, uFrom, vFrom, uTo, vTo, sprite, false);
	}

	protected BakedQuad createQuad(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4, float uFrom, float uTo, float vFrom, float vTo, TextureAtlasSprite sprite, boolean reversed) {
		Vector3d normal = reversed ?
				v3.subtract(v1).crossProduct(v2.subtract(v1)).normalize() :
				v3.subtract(v2).crossProduct(v1.subtract(v2)).normalize();

		if (reversed) {
			Vector3d v0;
			v0 = v1;
			v1 = v4;
			v4 = v0;
			v0 = v2;
			v2 = v3;
			v3 = v0;
		}

		BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
		builder.setQuadOrientation(Direction.getFacingFromVector(normal.x, normal.y, normal.z));
		putVertex(builder, normal, v1.x, v1.y, v1.z, reversed ? uTo : uFrom, vFrom, sprite);
		putVertex(builder, normal, v2.x, v2.y, v2.z, reversed ? uTo : uFrom, vTo, sprite);
		putVertex(builder, normal, v3.x, v3.y, v3.z, reversed ? uFrom : uTo, vTo, sprite);
		putVertex(builder, normal, v4.x, v4.y, v4.z, reversed ? uFrom : uTo, vFrom, sprite);

		return builder.build();
	}

	protected List<BakedQuad> createCube(Vector3d from, Vector3d to, TextureAtlasSprite allFaces, boolean dynamicUV) {
		return createCube(from, to, allFaces, dynamicUV, false);
	}

	protected List<BakedQuad> createCube(Vector3d from, Vector3d to, TextureAtlasSprite allFaces, boolean dynamicUV, boolean withReversed) {
		return createCube(from, to, allFaces, allFaces, allFaces, allFaces, allFaces, allFaces, dynamicUV, withReversed);
	}

	protected List<BakedQuad> createCube(Vector3d from, Vector3d to, TextureAtlasSprite up, TextureAtlasSprite down, TextureAtlasSprite north, TextureAtlasSprite south, TextureAtlasSprite east, TextureAtlasSprite west, boolean dynamicUV) {
		return createCube(from, to, up, down, north, south, east, west, dynamicUV, false);
	}

	protected List<BakedQuad> createCube(Vector3d from, Vector3d to, TextureAtlasSprite up, TextureAtlasSprite down, TextureAtlasSprite north, TextureAtlasSprite south, TextureAtlasSprite east, TextureAtlasSprite west, boolean dynamicUV, boolean withReversed) {
		List<BakedQuad> quads = new ArrayList<>();

		double fx = from.getX();
		double fy = from.getY();
		double fz = from.getZ();
		double tx = to.getX();
		double ty = to.getY();
		double tz = to.getZ();

		quads.add(createQuad(v(fx, ty, fz), v(fx, ty, tz), v(tx, ty, tz), v(tx, ty, fz), getUFrom(dynamicUV, up, fx), getUTo(dynamicUV, up, tx), getVFrom(dynamicUV, up, fz), getVTo(dynamicUV, up, tz), up));
		quads.add(createQuad(v(tx, fy, fz), v(tx, fy, tz), v(fx, fy, tz), v(fx, fy, fz), getUFrom(dynamicUV, down, fx), getUTo(dynamicUV, down, tx), getVFrom(dynamicUV, down, fz), getVTo(dynamicUV, down, tz), down));
		quads.add(createQuad(v(tx, ty, fz), v(tx, fy, fz), v(fx, fy, fz), v(fx, ty, fz), getUFrom(dynamicUV, north, fx), getUTo(dynamicUV, north, tx), getVFrom(dynamicUV, north, fy), getVTo(dynamicUV, north, ty), north));
		quads.add(createQuad(v(fx, ty, tz), v(fx, fy, tz), v(tx, fy, tz), v(tx, ty, tz), getUFrom(dynamicUV, south, fx), getUTo(dynamicUV, south, tx), getVFrom(dynamicUV, south, fy), getVTo(dynamicUV, south, ty), south));
		quads.add(createQuad(v(tx, ty, tz), v(tx, fy, tz), v(tx, fy, fz), v(tx, ty, fz), getUFrom(dynamicUV, east, fz), getUTo(dynamicUV, east, tz), getVFrom(dynamicUV, east, fy), getVTo(dynamicUV, east, ty), east));
		quads.add(createQuad(v(fx, ty, fz), v(fx, fy, fz), v(fx, fy, tz), v(fx, ty, tz), getUFrom(dynamicUV, west, fz), getUTo(dynamicUV, west, tz), getVFrom(dynamicUV, west, fy), getVTo(dynamicUV, west, ty), west));

		if (withReversed) {

			quads.add(createQuad(v(fx, ty, fz), v(fx, ty, tz), v(tx, ty, tz), v(tx, ty, fz), getUFrom(dynamicUV, up, fx), getUTo(dynamicUV, up, tx), getVFrom(dynamicUV, up, fz), getVTo(dynamicUV, up, tz), up, true));
			quads.add(createQuad(v(tx, fy, fz), v(tx, fy, tz), v(fx, fy, tz), v(fx, fy, fz), getUFrom(dynamicUV, down, fx), getUTo(dynamicUV, down, tx), getVFrom(dynamicUV, down, fz), getVTo(dynamicUV, down, tz), down, true));
			quads.add(createQuad(v(tx, ty, fz), v(tx, fy, fz), v(fx, fy, fz), v(fx, ty, fz), getUFrom(dynamicUV, north, fx), getUTo(dynamicUV, north, tx), getVFrom(dynamicUV, north, fy), getVTo(dynamicUV, north, ty), north, true));
			quads.add(createQuad(v(fx, ty, tz), v(fx, fy, tz), v(tx, fy, tz), v(tx, ty, tz), getUFrom(dynamicUV, south, fx), getUTo(dynamicUV, south, tx), getVFrom(dynamicUV, south, fy), getVTo(dynamicUV, south, ty), south, true));
			quads.add(createQuad(v(tx, ty, tz), v(tx, fy, tz), v(tx, fy, fz), v(tx, ty, fz), getUFrom(dynamicUV, east, fz), getUTo(dynamicUV, east, tz), getVFrom(dynamicUV, east, fy), getVTo(dynamicUV, east, ty), east, true));
			quads.add(createQuad(v(fx, ty, fz), v(fx, fy, fz), v(fx, fy, tz), v(fx, ty, tz), getUFrom(dynamicUV, west, fz), getUTo(dynamicUV, west, tz), getVFrom(dynamicUV, west, fy), getVTo(dynamicUV, west, ty), west, true));

		}
		return quads;
	}

	private float getUFrom(boolean dynamicUV, TextureAtlasSprite sprite, double d) {
		return dynamicUV && 0 <= d && d <= 1 ? sprite.getWidth() * (float) d : 0;
	}

	private float getUTo(boolean dynamicUV, TextureAtlasSprite sprite, double d) {
		return dynamicUV && 0 <= d && d <= 1 ? sprite.getWidth() * (float) d : sprite.getWidth();
	}

	private float getVFrom(boolean dynamicUV, TextureAtlasSprite sprite, double d) {
		return dynamicUV && 0 <= d && d <= 1 ? sprite.getHeight() * (float) d : 0;
	}

	private float getVTo(boolean dynamicUV, TextureAtlasSprite sprite, double d) {
		return dynamicUV && 0 <= d && d <= 1 ? sprite.getHeight() * (float) d : sprite.getHeight();
	}

	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean isSideLit() {
		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.EMPTY;
	}

}