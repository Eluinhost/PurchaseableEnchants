Purchaseable Enchants
=====================

![Example](/images/example.png)

This plugin allows you to define enchantment tier costs and allow players to purchase enchants.

You can disable crafting of enchantment tables and/or anvils as you please.

You can also configure which item when clicked opens the enchantment tool

# Configuration

Example config:

```yaml
remove enchants on repair: true
disable anvil use: true
disable enchanting table use: true
disable items: [ENCHANTMENT_TABLE]
tool items: [ANVIL]
costs:
  tier 1:
    material: GOLD_INGOT
    amount: 2
  tier 2:
    material: GOLD_INGOT
    amount: 8
  tier 3:
    material: GOLDEN_APPLE
    amount: 2
  tier 4:
    material: GOLDEN_APPLE
    amount: 8
enchantments:
  DAMAGE_ALL:
    1: tier 1
    2: tier 2
    3: tier 3
    4: tier 4
  ARROW_DAMAGE:
    1: tier 1
    2: tier 2
    3: tier 3
    4: tier 4
  PROTECTION_ENVIRONMENTAL:
    1: tier 1
    2: tier 2
    3: tier 3
    4: tier 4
  PROTECTION_FALL:
    1: tier 1
    2: tier 1
    3: tier 2
  PROTECTION_PROJECTILE:
    1: tier 1
    2: tier 2
    3: tier 3
    4: tier 4
  ARROW_FIRE:
    1: tier 4
  FIRE_ASPECT:
    1: tier 4
  ARROW_INFINITE:
    1: tier 4
```

### remove enchants on repair

If true any items repaired at a crafting table/inventory will have their enchants removed. THIS DOES NOT STOP COMBINING
IN AN ANVIL FOR HIGHER LEVELS

### disable anvil use

Disables placed anvils in the world opening

### disable enchanting table use

Disabeles placed tabeles in the world opening

### disable items

This is a list of materials that crafting will be disabled on. A list of materials can be found [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html)

### tool items

Same as `disable items` but these items when clicked in any way open the enchanting tool

### costs

This is a set of costs. A cost is formatted like this:

```yaml
COST_NAME:
  material: MATERIAL_NAME
  amount: 2
  data: 2
```

`material` - the type of item, names can be found [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html)  
`amount` - how many it costs  
`data` - optional, the data value required (e.g. WOOL/INK_SACK)  

`COST_NAME` is the name of this cost used in `enchantments` and should be unique

### enchantments

This is a set of enchantments and their costs for each level. Each enchant is formatted like this:

```yaml
ENCHANT_NAME:
  1: COST_NAME
  2: COST_NAME
```

`COST_NAME` is the name of the cost defined in `costs`  
1/2/3 e.t.c. is the level to set the cost for, if the level is not defined it cannot be bought  

`ENCHANT_NAME` is the name of the enchant, if an enchant isn't in the list it cannot be bought. A list of enchantments
can be found [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/enchantments/Enchantment.html)