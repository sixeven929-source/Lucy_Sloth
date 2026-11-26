# Lucy's Sloths — port a NeoForge 1.21.1 (+ perezoso domesticable)

Port del mod Fabric 1.19 de dayofpi (CC0-1.0). Reconstruido desde el bytecode del jar (no había código fuente).

## Compilar
1. Instala JDK 21.
2. Copia `gradlew`, `gradlew.bat` y `gradle/` de un MDK oficial de NeoForge 1.21.1 (o corre `gradle wrapper`).
3. `./gradlew build` → jar en `build/libs/`.
4. Si falla por versiones, ajusta `neo_version` en `gradle.properties` y la versión del plugin `net.neoforged.moddev` en `build.gradle` a las del MDK 1.21.1 actual.

## Domesticación
- **Domar:** clic derecho con bayas dulces o bayas brillantes (1/3 de probabilidad por baya). Lista editable en `data/lucys_sloths/tags/item/taming_items.json`.
- **Sentar / levantar:** clic derecho (sin agacharte) al perezoso propio.
- **Seguirte:** automático al estar domesticado y de pie (teletransporta si te alejas mucho).
- **Subirse a tu cabeza:** agáchate + clic derecho (como en el mod original los salvajes se suben con clic normal).
- Las hojas siguen sirviendo para criar y para que le crezca una planta; las tijeras se la quitan.

## Cambios respecto al original
- `Animal` → `TamableAnimal`; mixin del ocelote reemplazado por un evento (`EntityJoinLevelEvent`).
- Spawn por `neoforge:add_spawns` (`#minecraft:is_jungle`, peso 50, grupo 3–5).
- Loot table movida a `loot_table/` y `looting_enchant` → `enchanted_count_increase`.
- Animación `SITTING` (que existía sin usar) ahora se usa al sentarse.
- Keyframes de `IDLING_IN_WATER` reordenados (venían desordenados).
