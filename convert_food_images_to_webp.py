# -*- coding: utf-8 -*-
"""
将 vue3/public/images/food 下所有 jpg/jpeg/png/bmp 图片转换为 webp 格式。
- 同名同目录生成 .webp 文件
- 删除原 jpg/png 文件
- 在脚本同目录输出 update_food_image_url.sql，用于更新数据库中 food.image_url
- 可选通过命令行参数指定 webp 质量 (1-100，默认 82)

用法：
    python convert_food_images_to_webp.py            # 默认质量 82
    python convert_food_images_to_webp.py 75         # 自定义质量 75

依赖：
    pip install Pillow
"""

import os
import sys
from pathlib import Path
from datetime import datetime

try:
    from PIL import Image
except ImportError:
    print("缺少 Pillow 库，请先执行: pip install Pillow")
    sys.exit(1)


# 脚本所在目录为 java实训/，前端位于同级 vue3/
SCRIPT_DIR = Path(__file__).resolve().parent
FOOD_DIR = SCRIPT_DIR / "vue3" / "public" / "images" / "food"
SQL_OUTPUT = SCRIPT_DIR / "update_food_image_url.sql"

SUPPORTED_EXTS = {".jpg", ".jpeg", ".png", ".bmp", ".gif"}
QUALITY = 82


def main():
    global QUALITY
    if len(sys.argv) > 1:
        try:
            QUALITY = max(1, min(100, int(sys.argv[1])))
        except ValueError:
            print(f"无效的质量参数 '{sys.argv[1]}'，使用默认值 {QUALITY}")

    if not FOOD_DIR.exists():
        print(f"食物图片目录不存在: {FOOD_DIR}")
        sys.exit(1)

    print(f"扫描目录: {FOOD_DIR}")
    print(f"WebP 质量: {QUALITY}")
    print("-" * 60)

    converted = []
    skipped = []
    failed = []

    for img_path in sorted(FOOD_DIR.rglob("*")):
        if not img_path.is_file():
            continue
        if img_path.suffix.lower() not in SUPPORTED_EXTS:
            continue

        webp_path = img_path.with_suffix(".webp")
        if webp_path.exists() and webp_path != img_path:
            skipped.append(img_path)
            continue

        try:
            with Image.open(img_path) as im:
                if im.mode not in ("RGB", "RGBA"):
                    im = im.convert("RGBA" if "A" in im.getbands() else "RGB")
                im.save(webp_path, format="WEBP", quality=QUALITY, method=4)

            orig_size = img_path.stat().st_size
            new_size = webp_path.stat().st_size
            ratio = (1 - new_size / orig_size) * 100 if orig_size else 0
            print(
                f"[OK]   {img_path.relative_to(FOOD_DIR)} "
                f"{orig_size//1024}KB -> {new_size//1024}KB  ({ratio:.1f}% ↓)"
            )

            converted.append(
                {
                    "orig_rel": img_path.relative_to(FOOD_DIR),
                    "webp_rel": webp_path.relative_to(FOOD_DIR),
                    "orig_abs": img_path,
                    "webp_abs": webp_path,
                }
            )
        except Exception as e:
            failed.append((img_path, str(e)))
            print(f"[FAIL] {img_path.relative_to(FOOD_DIR)}  ->  {e}")

    print("-" * 60)
    for item in converted:
        try:
            item["orig_abs"].unlink()
        except Exception as e:
            print(f"[WARN] 无法删除原图 {item['orig_abs']}: {e}")

    with open(SQL_OUTPUT, "w", encoding="utf-8") as f:
        f.write("-- ============================================================\n")
        f.write(f"-- 自动生成于 {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n")
        f.write("-- 将 food.image_url 中的 .jpg/.png 引用改为 .webp\n")
        f.write("-- 请根据实际库表结构调整后执行\n")
        f.write("-- ============================================================\n\n")
        for item in converted:
            old = "/images/" + str(item["orig_rel"]).replace("\\", "/")
            new = "/images/" + str(item["webp_rel"]).replace("\\", "/")
            f.write(
                f"UPDATE food SET image_url = '{new}' "
                f"WHERE image_url = '{old}';\n"
            )

    print(f"\n转换完成: {len(converted)} 张")
    if skipped:
        print(f"跳过(已存在 webp): {len(skipped)} 张")
    if failed:
        print(f"失败: {len(failed)} 张")
        for p, e in failed:
            print(f"  - {p.name}: {e}")
    print(f"\nSQL 更新脚本已生成: {SQL_OUTPUT}")
    print("请在数据库中执行该脚本，保持前端引用与文件扩展名一致。")


if __name__ == "__main__":
    main()
