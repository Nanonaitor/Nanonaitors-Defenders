from pathlib import Path
import json
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
TEXTURES = ROOT / "src/main/resources/assets/defenders/textures/items"
MODELS = ROOT / "src/main/resources/assets/defenders/models/item"
PREVIEW = ROOT / "build/defender-sprite-preview.png"

PALETTES = {
    "wood": ("#3d2413", "#76502b", "#bd8b4f", "#e1bd78"),
    "stone": ("#33383a", "#596164", "#8d9899", "#c4cdca"),
    "gold": ("#72490c", "#bd7c10", "#f1c232", "#fff07a"),
    "iron": ("#3b454c", "#6f7d83", "#b7c2c3", "#f1f5ec"),
    "diamond": ("#075e68", "#0fa6a8", "#49e3d2", "#b6fff0"),
    "silver": ("#505a69", "#8996a9", "#d7e2ef", "#ffffff"),
    "bronze": ("#593016", "#945126", "#cf8143", "#f2bd72"),
    "steel": ("#26323a", "#485d68", "#8196a0", "#d5e1df"),
    "umbrium": ("#20182a", "#3d2753", "#694383", "#b67ad0"),
    "dragonbone": ("#5e5948", "#918a6c", "#d1c99d", "#fff4c4"),
    "flamed_dragonbone": ("#4d170d", "#9b2a13", "#ef5a19", "#ffd34d"),
    "iced_dragonbone": ("#15345f", "#286ca6", "#62c9ed", "#d8fbff"),
    "electric_dragonbone": ("#271b51", "#5542a2", "#b47bf1", "#fff16a"),
    "desert_myrmex": ("#4c2914", "#975025", "#d88a3d", "#ffd37b"),
    "jungle_myrmex": ("#173e26", "#267242", "#4dbd69", "#a7ef8e"),
    "desert_venom": ("#402014", "#884023", "#b867d1", "#efb7ff"),
    "jungle_venom": ("#14371f", "#276a36", "#8c54c7", "#d9a7ff"),
    "living": ("#351018", "#6f1f2d", "#b53942", "#f07063"),
    "sentient": ("#180b21", "#40134f", "#89246d", "#f02d83"),
}

DISPLAY = {
    "firstperson_lefthand": {"rotation": [0, -120, 35], "translation": [1.5, 3.5, 1.5], "scale": [.85, .85, .85]},
    "firstperson_righthand": {"rotation": [0, 120, -35], "translation": [1.5, 3.5, 1.5], "scale": [.85, .85, .85]},
    "thirdperson_lefthand": {"rotation": [0, -90, -35], "translation": [0, 2.5, 0], "scale": [.85, .85, .85]},
    "thirdperson_righthand": {"rotation": [0, 90, 35], "translation": [0, 2.5, 0], "scale": [.85, .85, .85]},
}

def sprite(name, colors):
    edge, dark, mid, light = colors
    image = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(image)
    # Short triangular blade, diagonal like a handheld item.
    d.polygon([(13, 1), (14, 2), (10, 8), (8, 9), (7, 8), (9, 5)], fill=edge)
    d.polygon([(13, 2), (10, 7), (9, 7), (10, 5)], fill=mid)
    d.line([(13, 2), (10, 5)], fill=light, width=1)
    # Distinctive asymmetric crescent guard—the Defender silhouette.
    d.polygon([(7, 6), (9, 8), (8, 11), (6, 13), (4, 12), (6, 10), (6, 8), (4, 7), (5, 6)], fill=edge)
    d.polygon([(7, 7), (8, 8), (7, 11), (6, 12), (5, 12), (7, 9), (5, 7)], fill=mid)
    d.point((6, 7), fill=light)
    # Grip and pommel.
    d.line([(7, 9), (3, 13)], fill=edge, width=3)
    d.line([(7, 9), (3, 13)], fill=dark, width=1)
    d.rectangle((2, 13, 4, 15), fill=edge)
    d.point((3, 13), fill=mid)

    if name == "flamed_dragonbone":
        d.point([(11, 4), (12, 5), (9, 3)], fill="#fff36a")
    elif name == "iced_dragonbone":
        d.point([(12, 3), (11, 5), (8, 6)], fill="#ffffff")
    elif name == "electric_dragonbone":
        d.line([(12, 3), (10, 4), (11, 5), (8, 7)], fill="#fff35b")
    elif name.endswith("venom"):
        d.point([(8, 7), (6, 11), (5, 12)], fill="#d9ff4b")
    elif name == "living":
        d.point([(11, 4), (8, 8), (6, 11)], fill="#ffb08d")
        d.point((7, 8), fill="#f7dd73")
    elif name == "sentient":
        d.point([(11, 4), (8, 8), (6, 11)], fill="#55f4e6")
        d.rectangle((7, 7, 8, 8), fill="#fff36a")
    return image

def write_models(name):
    normal = {
        "parent": "item/handheld",
        "textures": {"layer0": f"defenders:items/defender_{name}"},
        "overrides": [{"predicate": {"blocking": 1}, "model": f"defenders:item/defender_{name}_blocking"}],
    }
    blocking = {
        "parent": "item/handheld",
        "textures": {"layer0": f"defenders:items/defender_{name}"},
        "display": DISPLAY,
    }
    (MODELS / f"defender_{name}.json").write_text(json.dumps(normal, indent=2) + "\n", encoding="utf-8")
    (MODELS / f"defender_{name}_blocking.json").write_text(json.dumps(blocking, indent=2) + "\n", encoding="utf-8")

def main():
    TEXTURES.mkdir(parents=True, exist_ok=True)
    MODELS.mkdir(parents=True, exist_ok=True)
    rendered = []
    for name, colors in PALETTES.items():
        image = sprite(name, colors)
        image.save(TEXTURES / f"defender_{name}.png")
        write_models(name)
        rendered.append((name, image))

    cell_w, cell_h = 144, 120
    sheet = Image.new("RGBA", (cell_w * 5, cell_h * 4), "#20242b")
    draw = ImageDraw.Draw(sheet)
    font = ImageFont.load_default()
    for i, (name, image) in enumerate(rendered):
        x, y = (i % 5) * cell_w, (i // 5) * cell_h
        large = image.resize((96, 96), Image.Resampling.NEAREST)
        sheet.alpha_composite(large, (x + 24, y))
        label = name.replace("_", " ").title()
        draw.text((x + 6, y + 98), label, font=font, fill="#ffffff")
    PREVIEW.parent.mkdir(parents=True, exist_ok=True)
    sheet.convert("RGB").save(PREVIEW)

if __name__ == "__main__":
    main()
