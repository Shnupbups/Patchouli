---
title: "Book JSON Specification"
keywords: book json format
tags: [specifications]
sidebar: mydoc_sidebar
permalink: mydoc_book_json.html
summary: Exhaustive specification of book JSON files
---

This page details every key you can have in a book.json file, for quick reference. For guided tutorial-style documentation, please see TODO.

| Key | Type | Default Value | Description |
|-----|------|---------------|-------------|
| `name` | string | **required** | The name of the book that will be displayed in the book item and the GUI. For modders, this can be a localization key. |
| `landing_text` | string | **required** | The text that will be displayed in the landing page of your book. This text can be formatted. For modders, this can be a localization key. |
| `advancement_namespaces` | string array | `[]` | The advancement namespaces this book uses for locking its entries. Any namespaces used need to be declared here so Patchouli knows to track them and listen for them to unlock stuff. As an example, if you use "yourmod:cooladvancement" in one of your entries, you need to have "yourmod" in this array. See Locking Content with Advancements for more information on advancement locking.<br/>Adding "minecraft" to this array isn't allowed to prevent a needlessly large amount of data from being sent around. |
| `book_texture` | string | `patchouli:textures/gui/book_brown.png` | foo |
| `filler_texture` | string | `null` | The texture for the page filler (the cube thing that shows up on entries with an odd number of pages). Define if you want something else than the cube to fill your empty pages. |
