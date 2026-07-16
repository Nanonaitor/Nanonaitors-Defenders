from pathlib import Path
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
MEDIA = ROOT / "media"
RESOURCE = ROOT / "src/main/resources/assets/defenders"
SPRITE = RESOURCE / "textures/items/defender_diamond.png"


def make_logo(size: int) -> Image.Image:
    scale = size / 1024.0
    image = Image.new("RGB", (size, size), "#101621")
    draw = ImageDraw.Draw(image)

    def points(values):
        return [(round(x * scale), round(y * scale)) for x, y in values]

    # Restrained pixel-grid background.
    tile = max(1, round(64 * scale))
    for y in range(0, size, tile):
        for x in range(0, size, tile):
            if (x // tile + y // tile) % 2 == 0:
                draw.rectangle((x, y, x + tile - 1, y + tile - 1), fill="#131c29")

    # Blocky crest and double rim remain readable at small thumbnail sizes.
    outer = [(512, 92), (820, 190), (850, 500), (764, 742), (512, 924),
             (260, 742), (174, 500), (204, 190)]
    inner = [(512, 132), (780, 218), (804, 492), (728, 710), (512, 866),
             (296, 710), (220, 492), (244, 218)]
    core = [(512, 170), (742, 244), (766, 486), (696, 676), (512, 812),
            (328, 676), (258, 486), (282, 244)]
    draw.polygon(points(outer), fill="#071015")
    draw.polygon(points(inner), fill="#d59b27")
    draw.polygon(points(core), fill="#193344")

    # Cyan parry arc.
    arc_box = tuple(round(v * scale) for v in (288, 258, 736, 706))
    width = max(2, round(24 * scale))
    draw.arc(arc_box, start=210, end=505, fill="#52f2dc", width=width)
    inner_arc = tuple(round(v * scale) for v in (326, 296, 698, 668))
    draw.arc(inner_arc, start=210, end=505, fill="#168d9a", width=max(1, round(10 * scale)))

    # Enlarge the exact in-game Defender sprite with nearest-neighbor pixels.
    sprite = Image.open(SPRITE).convert("RGBA")
    sprite_size = max(16, round(544 * scale / 16) * 16)
    enlarged = sprite.resize((sprite_size, sprite_size), Image.Resampling.NEAREST)
    image.paste(enlarged, ((size - sprite_size) // 2, (size - sprite_size) // 2), enlarged)

    # Pixel corner accents and border.
    accent = "#83fff0"
    corner = round(78 * scale)
    inset = round(36 * scale)
    line = max(2, round(12 * scale))
    for sx, sy in ((1, 1), (-1, 1), (1, -1), (-1, -1)):
        x = inset if sx > 0 else size - inset
        y = inset if sy > 0 else size - inset
        draw.line((x, y, x + sx * corner, y), fill=accent, width=line)
        draw.line((x, y, x, y + sy * corner), fill=accent, width=line)
    return image


def main():
    MEDIA.mkdir(parents=True, exist_ok=True)
    RESOURCE.mkdir(parents=True, exist_ok=True)
    logo = make_logo(1024)
    logo.save(MEDIA / "defenders-logo.png", optimize=True)
    logo.resize((256, 256), Image.Resampling.NEAREST).save(
        RESOURCE / "logo.png", optimize=True)


if __name__ == "__main__":
    main()
