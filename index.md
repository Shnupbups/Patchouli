---
title: "Getting started with Patchouli"
keywords: sample homepage
tags: [getting_started]
sidebar: mydoc_sidebar
permalink: index.html
summary: Installing the mod and a quick overview of Patchouli's data model
---

## Prerequisites
There are some easy prerequisites that must be satisfied before using Patchouli:
* Minecraft: This goes without saying, but Minecraft and MinecraftForge must be installed. The Fabric port of Patchouli should also work, but will receive less support.
* Patchouli heavily makes use of the [JSON](https://en.wikipedia.org/wiki/JSON) data format. Familiarity with it is assumed.

## Obtaining Patchouli

### For Modders
Add a dependency on Patchouli using Maven. In your `build.gradle`, add
```groovy
repositories {
    maven { url 'https://maven.blamejared.com' }
}

dependencies {
    // 1.14+
    compileOnly fg.deobf("vazkii.patchouli:Patchouli:[VERSION]:api")
    runtimeOnly fg.deobf("vazkii.patchouli:Patchouli:[VERSION]")

    compile "vazkii.patchouli:Patchouli:[VERSION]"           // 1.12
}
```

Don't forget to replace `[VERSION]` with the version of the mod that you want to work with! The available versions are the folder names seen on Jared's maven.
For 1.14 and up, the version is prepended with the Minecraft version.

### For Modpack Creators
Patchouli can be obtained on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/patchouli), as usual.


## Data Model
Patchouli loads multiple *books* from `/data/<domain>/patchouli_books` (for mods) and from the `<instance_folder>/patchouli_books` folder (for modpacks and
beside your `config` and `mods` folders).
Each book is comprised of a number of *categories*, each of which has a number of *entries*.

An entry is the unit of separation that you will find the most useful, and is comprised of a sequence of *pages*. Pages can take many different forms, as seen in [TODO LINK PAGE TYPES OVERVIEW]().

As an example, the Botania mod exposes one Patchouli book, the *Lexica Botania*. The lexicon has a handful of categories, for example *Functional Flowers* and *Mana Manipulation*,
covering major areas of the mod. Within the *Functional Flowers* category, each different flower type has an entry. Each entry has a few pages of introductory text, then a page
showing how the flower is crafted.

## Creating Your First Book

### Locate your patchouli_books directory
Your books and their contents will go in your patchouli\_books directory, so you need to find it.
* For modpack makers, it'll be in your instance folder (next to mods, config, etc). Note that you need to run the game with patchouli installed for it to show up (or just make it yourself).
* For modders, it'll be /data/_yourmodid_/patchouli_books, you'll have to make it yourself.

### Create your folder structure
Once you found patchouli\_books, decide on a name for your book. Only lower case characters and underscores are allowed. This is purely an internal name so feel free to call it whatever you want. After you have a name, create folders and files inside patchouli_books so that it looks like this:

* patchouli_books (folder)
    * _yourbookid_ (folder)
        * book.json (empty file)
        * en_us (folder)
            * entries (empty folder)
            * categories (empty folder)
            * templates (empty folder)

You may have as many books as you want, even within the same mod(pack). Naturally each one should have a different ID. In game, each book is assigned an internal ID in the form "_yourmodid:yourbookid_", or "patchouli:_yourbookid_" if you're loading them as a modpack maker.

Note that creating an "en\_us" folder means you're creating the "english" version of your book. The contents you put in the "en_us" folder are always the "main" ones loaded, so even if your book isn't meant to be natively in english, you need to put your main stuff there.

Any translators may create folders with their languages and override any files they wish. They're automatically loaded if the game language is changed. For translators: Please don't include in your folder anything you aren't overriding.

### Populate book.json
`book.json` declares the folder it's in as an actual book to Patchouli. Without it, Patchouli will not load anything else from the folder.

Open it up using your favorite text editor, and fill it in as follows:

```json
{
	"name": "BOOK NAME",
	"landing_text": "LANDING TEXT",
	"version": 1
}
```

where "BOOK NAME" is the name your book will be displayed as, and "LANDING TEXT" the text that will show up on the left landing page of your book ingame.
The version field specifies which edition your book is, and should be incremented whenever you update the book.
Both `name` and `landing_text` can be translation strings.

### Launch the Game
Load your game and check that your book is there. Unless you specified otherwise, it should be in the Miscellaneous creative tab, but you can also search for it. 

If you don't see it, check that Patchouli is properly loaded and if there's any errors in your log. 

You should see something like this: ![](https://i.imgur.com/lsdDrrk.png)


### Add Content
Time to add some content to your book. Go to your book's folder and then `en_us`, and create folders and files so that it looks like this:

* en_us (folder)
    * entries (folder)
        * test_category (folder)
            * test_entry.json (empty file)
    * categories (folder)
        * test_category.json (empty file)
    * templates (empty folder)

Open test\_category.json and test\_entry.json and fill them in as follows:

**test_category.json**
```json
{
	"name": "Test Category",
	"description": "This is a test category for testing!",
	"icon": "minecraft:writable_book"
}
```

**test_entry.json**:
```json
{
    "name": "Test Entry",
    "icon": "minecraft:writable_book",
    "category": "test_category",
    "pages": [
        {
            "type": "text",
            "text": "This is a test entry, but it should show up!"
        }
    ]
}
```

Save your files, then return ingame and open your book. Shift-click the pencil in the bottom-left corner. When you do so, Patchouli will hot-reload the book contents, and you should see the category and entry you just defined appear.

Besides `book.json`, anything else can be hot-reloaded without closing the game, so feel free to keep it open as you do further edits.

{% include note.html content="Modders should be aware that your IDE may copy resources to a separate folder before launching the game. As a result, Patchouli does not see any edits you make to the actual files. If you find hot-reloading not to work, you might have to rerun the `processResources` Gradle task." %}

### Obtaining the Book Item

Patchouli registers only a single item and uses NBT data to distinguish which exact book it is. Recall the internal ID of your book, as described [above](#create-your-folder-structure). The item's NBT data will contain the internal ID of your book under the key `patchouli:book`. For example:

**CraftTweaker**:
`<patchouli:guide_book>.withTag({"patchouli:book": "YOURBOOKID"});`
or just use `/ct hand`

**Vanilla Recipes and Advancements**:
```json
"item": "patchouli:guide_book",
"nbt": {
    "patchouli:book": "YOURBOOKID"
}
```

## Learn More

You're done with the basics, now it's time to learn more about what you can do with Patchouli's book system. Check the following pages out:
TODO

{% include links.html %}
