# -*- coding: utf-8 -*-
import docx

doc = docx.Document('ArtjomPatoka_ArseniSergejev_SPSHOP.docx')

print("Searching for bullet points...")
with open('bullets_found.txt', 'w', encoding='utf-8') as f:
    for idx, p in enumerate(doc.paragraphs):
        text = p.text.strip()
        # check if paragraph starts with • or other bullet chars, or has style list/bullet
        has_bullet_char = '•' in text or '\u2022' in text or '·' in text or '*' in text
        is_bullet_style = 'bullet' in p.style.name.lower() or 'list' in p.style.name.lower()
        if has_bullet_char or is_bullet_style:
            f.write(f"PARA {idx} (Style: {p.style.name}): [{p.text}]\n")
print("Done. Check bullets_found.txt")
