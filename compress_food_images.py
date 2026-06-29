# -*- coding: utf-8 -*-
"""
压缩 vue3/public/images/food 下所有图片：
  - 限制最大边 <= 512px（等比缩放）
  - 转 webp，质量 75
  - 去除 EXIF 元数据
  - 原 jpg/png 删除，仅保留 .webp

依赖：pip install Pillow

用法：
    python compress_food_images.py              # 默认最大边 512, 质量 75
    python compress_food_images.py 400 70      # 自定义 最大边 400, 质量 70
"""
import sys
from pathlib import Path
from PIL import Image, ExifTags

SCRIPT_DIR = Path(__file__).resolve().parent
FOOD_DIR = SCRIPT_DIR / "vue3" / "public" / "images" / "food"

MAX_SIDE = 512
QUALITY = 75


def main():
    global MAX_SIDE, QUALITY
    if len(sys.argv) >= 2:
        MAX_SIDE = int(sys.argv[1])
    if len(sys.argv) >= 3:
        QUALITY = int(sys.argv[2])

    if not FOOD_DIR.exists():
        print(f"目录不存在: {FOOD_DIR}")
        sys.exit(1)

    print(f"目标: {FOOD_DIR}")
    print(f"最大边: {MAX_SIDE}px  质量: {QUALITY}")
    print("-" * 70)

    total_before = 0
    total_after = 0
    count = 0

    # 扫描所有图片（包括已有的 .webp，重新压缩）
    exts = {".jpg", ".jpeg", ".png", ".bmp", ".gif", ".webp"}
    targets = [p for p in sorted(FOOD_DIR.rglob("*")) if p.is_file() and p.suffix.lower() in exts]

    # 去重：如果同名既有 .jpg 又有 .webp，只处理 .webp（避免覆盖）
    seen_stem = set()
    files = []
    for p in targets:
        key = (p.parent, p.stem)
        if p.suffix.lower() == ".webp":
            files.append(p)
            seen_stem.add(key)
        elif key not in seen_stem:
            files.append(p)

    for img_path in files:
        webp_path = img_path.with_suffix(".webp")
        try:
            orig_size = img_path.stat().st_size
            total_before += orig_size

            with Image.open(img_path) as im:
                # 去 EXIF（旋转信息要先应用再丢弃）
                try:
                    exif = im.getexif()
                    orientation = None
                    for tag, name in ExifTags.TAGS.items():
                        if name == "Orientation":
                            orientation = tag
                            break
                    if orientation and orientation in exif:
                        val = exif[orientation]
                        if val == 3: im = im.rotate(180, expand=True)
                        elif val == 6: im = im.rotate(270, expand=True)
                        elif val == 8: im = im.rotate(90, expand=True)
                except Exception:
                    pass

                # 转 RGB（webp 不存调色板/alpha 时体积更小）
                if im.mode not in ("RGB", "RGBA"):
                    im = im.convert("RGB")
                elif im.mode == "RGBA":
                    # 菜品图不需要透明通道，白底合成
                    bg = Image.new("RGB", im.size, (255, 255, 255))
                    bg.paste(im, mask=im.split()[3])
                    im = bg

                # 等比缩放
                w, h = im.size
                if max(w, h) > MAX_SIDE:
                    ratio = MAX_SIDE / max(w, h)
                    new_size = (int(w * ratio), int(h * ratio))
                    im = im.resize(new_size, Image.LANCZOS)

                # 保存 webp（无 exif，锐化一次提升观感）
                from PIL import ImageFilter
                im = im.filter(ImageFilter.UnsharpMask(radius=0.6, percent=60, threshold=2))
                im.save(webp_path, format="WEBP", quality=QUALITY, method=6)

            new_size_bytes = webp_path.stat().st_size
            total_after += new_size_bytes

            # 删除原图（仅当扩展名不是 .webp 时）
            if img_path.suffix.lower() != ".webp":
                img_path.unlink()

            rel = img_path.relative_to(FOOD_DIR)
            print(
                f"[OK] {str(rel):<30} {orig_size//1024:>5}KB -> {new_size_bytes//1024:>4}KB  "
                f"{(1 - new_size_bytes/orig_size)*100:5.1f}%↓  {im.size[0]}x{im.size[1]}"
            )
            count += 1
        except Exception as e:
            print(f"[FAIL] {img_path.relative_to(FOOD_DIR)}  ->  {e}")

    print("-" * 70)
    print(f"共处理 {count} 张")
    print(f"原始总大小: {total_before/1024/1024:.2f} MB")
    print(f"压缩后总大小: {total_after/1024/1024:.2f} MB")
    print(f"整体压缩率: {(1 - total_after/total_before)*100:.1f}%")
    print(f"平均大小: {total_after/count/1024:.1f} KB/张")


if __name__ == "__main__":
    main()
